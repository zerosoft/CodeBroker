package com.codebroker.core.actortype;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.DispatcherSelector;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import com.codebroker.api.AppListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.ISessionActor;
import com.codebroker.core.actortype.message.IUserActor;
import com.codebroker.core.actortype.message.IUserManager;


import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * description
 * 用户管理器
 * @author LongJu
 * @Date 2020/3/25
 */
public class UserManager extends AbstractBehavior<IUserManager> {

    public static final String IDENTIFY = UserManager.class.getSimpleName();
    private Map<String, ActorRef<IUserActor>> userMap=new HashMap<>();
    private Map<String,Long> lostSessionUser=new HashMap<>();


    public static Behavior<IUserManager> create(long gameWorldId,Duration checkUserLostSessionTime) {
        return Behaviors.setup(
                (ActorContext<IUserManager> context) ->
                    Behaviors.withTimers(
                        timers -> {
                            timers.startTimerWithFixedDelay("User-Check",IUserManager.TimeCheck.INSTANCE,checkUserLostSessionTime);
                            context
                                    .getSystem()
                                    .receptionist()
                                    .tell(Receptionist.register(ServiceKey.create(IUserManager.class, UserManager.IDENTIFY + "." + gameWorldId), context.getSelf()));
                            return new UserManager(context);
                        })
        ).narrow();
    }

    public UserManager(ActorContext<IUserManager> context) {
        super(context);
        getContext().getSystem().log().info("UserManager start");
    }

    @Override
    public Receive<IUserManager> createReceive() {
        return newReceiveBuilder()
                .onMessage(IUserManager.TryBindingUser.class,this::bindingSession)
                .onMessage(IUserManager.UserLostSession.class,this::userLostSession)
                .onMessage(IUserManager.SendMessageToGameUser.class,this::sendMessageToGameUser)
                .onMessage(IUserManager.TimeCheck.class,this::timeCheck)
                .onMessage(IUserManager.UserClose.class,this::userClose)
                .build();
    }

    private Behavior<IUserManager> sendMessageToGameUser(IUserManager.SendMessageToGameUser message) {
        if (userMap.containsKey(message.userId)){
            ActorRef<IUserActor> iUserActorRef = userMap.get(message.userId);
            iUserActorRef.tell(new IUserActor.GetSendMessageToGameUserActor(message.message));
        }
        return Behaviors.same();
    }

    /**
     * 关闭User Actor时候调用
     * @param message
     * @return
     */
    private Behavior<IUserManager> userClose(IUserManager.UserClose message) {
        userMap.remove(message.uid);
        return Behaviors.same();
    }

    private Behavior<IUserManager> timeCheck(Object message) {
        getContext().getLog().info("time check lost session");
        if (lostSessionUser.size()>0){
            for (String uid : lostSessionUser.keySet()) {
                ActorRef<IUserActor> userActorRef = userMap.get(uid);
                if (userActorRef!=null){
                    userActorRef.tell(new IUserActor.Disconnect(false));
                    userMap.remove(uid);
                }
            }
        }
        lostSessionUser.clear();
        return Behaviors.same();
    }

    private Behavior<IUserManager> userLostSession(IUserManager.UserLostSession message) {
        String name = message.self.path().name();
        lostSessionUser.put(name,System.currentTimeMillis());
        return Behaviors.same();
    }


    private Behavior<IUserManager> bindingSession(IUserManager.TryBindingUser tryBindingUser) {
        AppListener appListener = ContextResolver.getAppListener();
        //TODO 根据需求调整
        byte[] message = tryBindingUser.message.getRawData();

        String uid;
        try {
            uid = appListener.sessionLoginVerification(message);
        }catch (Exception e){
            getContext().getLog().error(e.getMessage(),e);
            //登入失败,关闭处理
            tryBindingUser.ioSession.tell(new ISessionActor.TryBindingUserFail());
            return Behaviors.same();
        }

        String key = User.IDENTIFY + "." + uid;
        if (userMap.containsKey(key)){
            lostSessionUser.remove(key);

            ActorRef<IUserActor> userActorRef= userMap.get(key);
            //通知User之前的session被顶掉了
            userActorRef.tell(new IUserActor.NewSessionLogin(tryBindingUser.ioSession));
            //通知Session绑定User
            tryBindingUser.ioSession.tell(new ISessionActor.SessionActorBindingUser(userActorRef));

        }else {
            ActorRef<IUserActor> spawn = getContext().spawn(
                    User.create(
                            uid,
                            tryBindingUser.ioSession,getContext().getSelf()),
                            key,
                            DispatcherSelector.fromConfig("game-logic"));
            //通知Session绑定User
            tryBindingUser.ioSession.tell(new ISessionActor.SessionActorBindingUser(spawn));
            spawn.tell(IUserActor.NewGameUserActorInit.INSTANCE);

            //加入监听
            getContext().watchWith(spawn,new IUserManager.UserClose(uid));
            userMap.put(key,spawn);
        }

        return Behaviors.same();
    }


}
