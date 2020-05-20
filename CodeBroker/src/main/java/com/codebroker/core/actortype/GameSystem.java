package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.cluster.ClusterEvent;
import com.codebroker.cluster.ClusterListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.*;
import com.codebroker.core.actortype.timer.UserManagerTimer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


/**
 * 游戏世界Actor
 * @author LongJu
 * @Date 2020/3/25
 */
public class GameSystem extends AbstractBehavior<IWorldMessage> {

    /**
     * 游戏世界标识Id
     */
    private long gameWorldId;

    public static Behavior<IWorldMessage> create(int id) {
        Behavior<IWorldMessage> setup =
                Behaviors.setup(ctx-> new GameSystem(ctx,id));
        return setup;
    }


    public GameSystem(ActorContext<IWorldMessage> context, int id) {
        super(context);
        this.gameWorldId=id;
    }

    private ActorRef<ISessionManager> sessionManager;
    private ActorRef<IUserManager> userManager;
    private ActorRef<IGameWorldMessage> gameWorldMessageActorRef;
    private ActorRef<UserManagerTimer.Command> userManagerTimer;
    private ActorRef<ClusterEvent.ClusterDomainEvent> clusterDomainEventActorRef;


    @Override
    public Receive<IWorldMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(IWorldMessage.SessionOpen.class,this::sessionOpen)
                .onMessage(IWorldMessage.StartWorldMessage.class,this::startGameWorld)
                .onMessage(IWorldMessage.ReStartWorldMessage.class,this::reStartGameWorld)
                .onMessage(IWorldMessage.StopWorldMessage.class,this::stopGameWorld)
                .onMessage(IWorldMessage.CreateService.class,this::createService)
                .onSignal(PreRestart.class,signal->onRestart())
                .onSignal(PostStop.class, signal ->onPostStop())
                .build();
    }

    private Behavior<IWorldMessage> createService(IWorldMessage.CreateService m) {
        ActorRef<IService> spawn = getContext().spawn(ServiceActor.create(m.name, m.service), m.name, DispatcherSelector.fromConfig("game-service"));
        ServiceWithActor serviceActor=new ServiceWithActor(m.name,spawn);
        com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor, m.service).newProxyInstance(m.service.getClass());
        ContextResolver.setManager(iService);
        m.replyTo.tell(IWorldMessage.ReplyCreateService.INSTANCE);
        return Behaviors.same();
    }

    private Behavior<IWorldMessage> reStartGameWorld(IWorldMessage.ReStartWorldMessage message) {
        getContext().getSystem().log().info("GameWorld reStart");
        return Behaviors.same();
    }

    private Behavior<IWorldMessage> startGameWorld(IWorldMessage.StartWorldMessage message) {
        getContext().getSystem().log().info("GameWorld start id {}",gameWorldId);

        sessionManager = getContext().spawn(SessionManager.create(gameWorldId), SessionManager.IDENTIFY+"."+gameWorldId);
        getContext().getSystem().log().info("SessionManager Path {}",sessionManager.path());

        userManager = getContext().spawn(UserManager.create(gameWorldId), UserManager.IDENTIFY+"."+gameWorldId);

        userManagerTimer = getContext().spawn(UserManagerTimer.create(userManager, Duration.of(1, ChronoUnit.MINUTES)), UserManagerTimer.IDENTIFY);
        getContext().getSystem().log().info("UserManager Path {}",userManager.path());

        clusterDomainEventActorRef = getContext().spawn(ClusterListener.create(), ClusterListener.IDENTIFY+"."+gameWorldId);
        getContext().getSystem().log().info("clusterDomainEventActorRef Path {}",clusterDomainEventActorRef.path());

        gameWorldMessageActorRef = getContext().spawn(GameWorld.create(gameWorldId), GameWorld.IDENTIFY+"."+gameWorldId);
        GameWorldWithActor gameWorldWithActor = new GameWorldWithActor(GameWorld.IDENTIFY, gameWorldMessageActorRef);

        ContextResolver.setGameWorld(gameWorldWithActor);

        return Behaviors.same();
    }

    private Behavior<IWorldMessage> onRestart() {
        getContext().getSystem().log().info("Master Control Program restart");
        return Behaviors.same();
    }

    /**
     * 会在 stopGameWorld 前执行
     * @return
     */
    private Behavior<IWorldMessage> onPostStop() {
        getContext().getSystem().log().info("Master Control Program stopped");
        return Behaviors.same();
    }

    /**
     * 关闭服务
     * @param message
     * @return
     */
    private Behavior<IWorldMessage> stopGameWorld(IWorldMessage.StopWorldMessage message) {
        return Behaviors.stopped(()->getContext().getSystem().log().info("Clean up"));
    }

    private Behavior<IWorldMessage> sessionOpen(IWorldMessage.SessionOpen message) {
        getContext().getSystem().log().debug("GameWorld login session");
        sessionManager.tell(new ISessionManager.SessionOpen(message.ioSession));
        return Behaviors.same();
    }
}
