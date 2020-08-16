package com.codebroker.core.actortype;

import akka.actor.typed.*;
import akka.actor.typed.javadsl.*;
import akka.cluster.ClusterEvent;
import akka.cluster.ddata.PNCounter;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.cluster.ddata.typed.javadsl.DistributedData;
import akka.cluster.sharding.external.ExternalShardAllocation;
import akka.cluster.sharding.external.javadsl.ExternalShardAllocationClient;
import akka.cluster.sharding.typed.ShardingEnvelope;
import akka.cluster.sharding.typed.javadsl.ClusterSharding;
import akka.cluster.sharding.typed.javadsl.Entity;
import akka.cluster.sharding.typed.javadsl.EntityTypeKey;
import com.codebroker.cluster.ClusterListener;
import com.codebroker.cluster.ReplicatedCache;
import com.codebroker.cluster.base.Counter;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.*;
import com.codebroker.core.actortype.timer.UserManagerTimer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletionStage;


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
                .onMessage(IWorldMessage.createGlobalService.class,this::createGlobalService)
                .onMessage(IWorldMessage.createClusterService.class,this::createClusterService)
                .onSignal(PreRestart.class,signal->onRestart())
                .onSignal(PostStop.class, signal ->onPostStop())
                .build();
    }

    private Behavior<IWorldMessage> createService(IWorldMessage.CreateService message) {
        ActorRef<IService> spawn = getContext().spawn(ServiceActor.create(message.name, message.service), message.name, DispatcherSelector.fromConfig("game-service"));
        return getWorldMessageBehavior(spawn, message.name, message.service, message.replyTo);
    }

    private Behavior<IWorldMessage> createClusterService(IWorldMessage.createClusterService message) {
        ClusterSharding clusterSharding = ClusterSharding.get(getContext().getSystem());
        EntityTypeKey<IService> typeKey = EntityTypeKey.create(IService.class, message.name);

        ActorRef<ShardingEnvelope<IService>> shardRegion =
                clusterSharding.init(Entity.of(typeKey, ctx -> {
                    String ctxEntityId = ctx.getEntityId();
                    Behavior<IService> commandBehavior = ClusterServiceActor.create(message.name, message.service);
                    return commandBehavior;
                }));
        ClusterServiceWithActor serviceActor=new ClusterServiceWithActor(message.name,clusterSharding);
        com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor,  message.service).newProxyInstance(message.service.getClass());
        ContextResolver.setManager(iService);
//        message.replyTo.tell(new IWorldMessage.ReplyCreateService(shardRegion));
//        shardRegion.

        ActorRef<IService> spawn = getContext().spawn(ServiceActor.create(message.name, message.service,true), message.name, DispatcherSelector.fromConfig("game-service"));
        return Behaviors.same();
    }

    private Behavior<IWorldMessage> createGlobalService(IWorldMessage.createGlobalService message) {
        ActorRef<IService> spawn = getContext().spawn(ServiceActor.create(message.name, message.service,true), message.name, DispatcherSelector.fromConfig("game-service"));
        return getWorldMessageBehavior(spawn, message.name, message.service, message.replyTo);
    }

    private Behavior<IWorldMessage> getWorldMessageBehavior(ActorRef<IService> spawn, String name, com.codebroker.api.internal.IService service, ActorRef<IWorldMessage.Reply> replyTo) {
        ServiceWithActor serviceActor=new ServiceWithActor(name,spawn);
        com.codebroker.api.internal.IService iService = new ObjectActorDecorate<>(serviceActor, service).newProxyInstance(service.getClass());
        ContextResolver.setManager(iService);
        replyTo.tell(new IWorldMessage.ReplyCreateService(spawn));
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


        SelfUniqueAddress selfUniqueAddress = DistributedData.get(getContext().getSystem()).selfUniqueAddress();

        ActorRef<ReplicatedCache.Command> replicatedCache = getContext().spawn(ReplicatedCache.create(), "ReplicatedCache");
        replicatedCache.tell(new ReplicatedCache.PutInCache("v","232"));



        getContext().getSystem().log().info("");
        //        PNCounter pnCounter = PNCounter.create();
//        PNCounter increment = pnCounter.increment(selfUniqueAddress, 1);
//        getContext().getSystem().log().info("PNCounter=",increment.value());

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
