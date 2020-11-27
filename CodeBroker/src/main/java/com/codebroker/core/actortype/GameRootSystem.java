package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import com.codebroker.cluster.ClusterListenerActor;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.*;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;


/**
 * 游戏世界顶级GameRootSystem
 * @author LongJu
 * @Date 2020/3/25
 */
public class GameRootSystem extends AbstractBehavior<IGameRootSystemMessage> {

    /**
     * 游戏世界标识Id
     */
    private long gameWorldId;

    public static Behavior<IGameRootSystemMessage> create(int id) {
        Behavior<IGameRootSystemMessage> setup =
                Behaviors.setup(ctx-> new GameRootSystem(ctx,id));
        return setup;
    }


    public GameRootSystem(ActorContext<IGameRootSystemMessage> context, int id) {
        super(context);
        this.gameWorldId=id;
    }



    @Override
    public Receive<IGameRootSystemMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(IGameRootSystemMessage.SessionOpen.class,this::sessionOpen)
                .onMessage(IGameRootSystemMessage.StartGameRootSystemMessage.class,this::startGameWorld)
                .onMessage(IGameRootSystemMessage.ReStartGameRootSystemMessage.class,this::reStartGameWorld)
                .onMessage(IGameRootSystemMessage.StopGameRootSystemMessage.class,this::stopGameWorld)
                .onMessage(IGameRootSystemMessage.CreateService.class,this::createService)
                .onMessage(IGameRootSystemMessage.createGlobalService.class,this::createGlobalService)
                .onSignal(PreRestart.class,signal->onRestart())
                .onSignal(PostStop.class, signal ->onPostStop())
                .build();
    }

    private Behavior<IGameRootSystemMessage> createService(IGameRootSystemMessage.CreateService message) {
            ActorRef<IService> spawn = getContext()
                    .spawn(ServiceActor.create(message.name, message.service),
                    message.name+"."+gameWorldId,
                    DispatcherSelector.fromConfig("game-service"));
            return getWorldMessageBehavior(spawn, message.name, message.service, message.replyTo);
    }

    private Behavior<IGameRootSystemMessage> createGlobalService(IGameRootSystemMessage.createGlobalService message) {
        ActorRef<IService> spawn = getContext().spawn(ServiceActor.create(message.name, message.service,true),
                    message.name+"."+gameWorldId,
                DispatcherSelector.fromConfig("game-service"));
        return getWorldMessageBehavior(spawn, message.name, message.service, message.replyTo);
    }

    private Behavior<IGameRootSystemMessage> getWorldMessageBehavior(ActorRef<IService> spawn, String name, com.codebroker.api.internal.IService service, ActorRef<IGameRootSystemMessage.Reply> replyTo) {
        ServiceWithActor serviceActor=new ServiceWithActor(name,spawn);
        com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());
        ContextResolver.setManager(iService);
        replyTo.tell(new IGameRootSystemMessage.ReplyCreateService(spawn));
        return Behaviors.same();
    }

    private Behavior<IGameRootSystemMessage> reStartGameWorld(IGameRootSystemMessage.ReStartGameRootSystemMessage message) {
        getContext().getSystem().log().info("GameWorld reStart");
        return Behaviors.same();
    }

    private Behavior<IGameRootSystemMessage> startGameWorld(IGameRootSystemMessage.StartGameRootSystemMessage message) {
        getContext().getSystem().log().info("GameWorld start id {}",gameWorldId);

        ActorPathService.sessionManager = getContext().spawn(SessionManager.create(gameWorldId), SessionManager.IDENTIFY+"."+gameWorldId);
        getContext().getSystem().log().info("SessionManager Path {}", ActorPathService.sessionManager.path());

        ActorPathService.gameWorldMessageActorRef = getContext().spawn(GameWorld.create(gameWorldId), GameWorld.IDENTIFY+"."+gameWorldId);
        GameWorldWithActor gameWorldWithActor = new GameWorldWithActor(GameWorld.IDENTIFY,  ActorPathService.gameWorldMessageActorRef);

        ActorPathService.userManager = getContext().spawn(UserManager.create(gameWorldId,Duration.of(60, ChronoUnit.SECONDS)), UserManager.IDENTIFY+"."+gameWorldId);
        getContext().getSystem().log().info("UserManager Path {}", ActorPathService.userManager.path());

        ActorPathService.clusterDomainEventActorRef = getContext().spawn(ClusterListenerActor.create(gameWorldId), ClusterListenerActor.IDENTIFY+"."+gameWorldId);
        getContext().getSystem().log().info("cluster Domain Event ActorRef Path {}", ActorPathService.clusterDomainEventActorRef.path());

        ContextResolver.setGameWorld(gameWorldWithActor);
        message.replyTo.tell(new IGameRootSystemMessage.StartWorldFinish());
        return Behaviors.same();
    }

    private Behavior<IGameRootSystemMessage> onRestart() {
        getContext().getSystem().log().info("Master Control Program restart");
        return Behaviors.same();
    }

    /**
     * 会在 stopGameWorld 前执行
     * @return
     */
    private Behavior<IGameRootSystemMessage> onPostStop() {
        getContext().getSystem().log().info("Master Control Program stopped");
        return Behaviors.same();
    }

    /**
     * 关闭服务
     * @param message
     * @return
     */
    private Behavior<IGameRootSystemMessage> stopGameWorld(IGameRootSystemMessage.StopGameRootSystemMessage message) {
        return Behaviors.stopped(()->getContext().getSystem().log().info("Clean up"));
    }

    private Behavior<IGameRootSystemMessage> sessionOpen(IGameRootSystemMessage.SessionOpen message) {
        ActorSystem<Void> system = getContext().getSystem();
        system.log().debug("GameWorld login session");
        ActorPathService.sessionManager.tell(new ISessionManager.SessionOpen(message.replyTo,message.ioSession));
        return Behaviors.same();
    }
}
