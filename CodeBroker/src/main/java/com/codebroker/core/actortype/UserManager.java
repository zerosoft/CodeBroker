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
import com.alibaba.fastjson.JSONObject;
import com.codebroker.api.AppListener;
import com.codebroker.core.ContextResolver;
import com.codebroker.core.actortype.message.ISession;
import com.codebroker.core.actortype.message.IUser;
import com.codebroker.core.actortype.message.IUserManager;

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

    private Map<String, ActorRef<IUser>> userMap=new HashMap<>();
    private Map<String,Long> lostSessionUser=new HashMap<>();

    public static Behavior<IUserManager> create(long gameWorldId) {
        return Behaviors.setup(
                context -> {
                    context
                            .getSystem()
                            .receptionist()
                            .tell(Receptionist.register(ServiceKey.create(IUserManager.class, UserManager.IDENTIFY+"."+gameWorldId), context.getSelf()));

                    return new UserManager(context);
                });
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
                .onMessage(IUserManager.TimeCheck.class,this::timeCheck)
                .onMessage(IUserManager.UserClose.class,this::userClose)
                .build();
    }

    private Behavior<IUserManager> userClose(IUserManager.UserClose message) {
        userMap.remove(message.uid);
        return Behaviors.same();
    }

    private Behavior<IUserManager> timeCheck(Object message) {
        if (lostSessionUser.size()>0){
            for (String uid : lostSessionUser.keySet()) {
                ActorRef<IUser> userActorRef = userMap.get(uid);
                if (userActorRef!=null){
                    userActorRef.tell(new IUser.Disconnect(false));
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
        //TODO 更具需求调整
        byte[] message = tryBindingUser.message.getRawData();
        JSONObject parse = (JSONObject) JSONObject.parse(new String(message));

        String uid = appListener.sessionLoginVerification(parse.getString("name"), parse.getString("parm"));
        if (userMap.containsKey(uid)){
            lostSessionUser.remove(uid);

            ActorRef<IUser> userActorRef= userMap.get(uid);
            //通知User之前的session被顶掉了
            userActorRef.tell(new IUser.NewSessionLogin(tryBindingUser.ioSession));
            //通知Session绑定User
            tryBindingUser.ioSession.tell(new ISession.SessionBindingUser(userActorRef));
        }else {
            ActorRef<IUser> spawn = getContext().spawn(User.create(uid,tryBindingUser.ioSession,getContext().getSelf()),User.IDENTIFY+"."+uid,DispatcherSelector.fromConfig("game-logic"));
            //通知Session绑定User
            tryBindingUser.ioSession.tell(new ISession.SessionBindingUser(spawn));
            spawn.tell(IUser.NewGameUserInit.INSTANCE);

            //加入监听
            getContext().watchWith(spawn,new IUserManager.UserClose(uid));
            userMap.put(User.IDENTIFY+"."+uid,spawn);
        }

        return Behaviors.same();
    }


}
