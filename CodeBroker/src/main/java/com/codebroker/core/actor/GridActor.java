package com.codebroker.core.actor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.codebroker.api.IUser;
import com.codebroker.api.event.AddEventListener;
import com.codebroker.api.event.HasEventListener;
import com.codebroker.api.event.IEventListener;
import com.codebroker.api.event.RemoveEventListener;
import com.codebroker.core.data.IObject;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
/**
 * 格子Actor对象
 * @author zero
 *
 */
public class GridActor extends AbstractActor {
	/**
	 * 父类Actor
	 */
	private final ActorRef parentAreaRef;
	
	public GridActor(ActorRef parentAreaRef) {
		super();
		this.parentAreaRef = parentAreaRef;
	}

	private Map<String, IUser> userMap = new TreeMap<String, IUser>();

	private final Map<String, IEventListener> eventListener = new HashMap<String, IEventListener>();
	@Override
	public Receive createReceive() {
		return receiveBuilder()
		  .match(EnterGrid.class, msg -> {
			if (userMap.containsKey(msg.user.getUserId())) {
				getSender().tell(false, getSelf());
			} else {
				userMap.put(msg.user.getUserId(), msg.user);
				getSender().tell(true, getSelf());
			}
		}).match(LeaveGrid.class, msg -> {
			if (userMap.containsKey(msg.userId)) {
				userMap.remove(msg.userId);
			}
		}).match(GetPlayers.class, msg -> {
			Collection<IUser> values = userMap.values();
			List<IUser> list = new ArrayList<IUser>();
			list.addAll(values);
			getSender().tell(list, getSelf());
		})
		//处理分发事件
		.match(IObject.class, msg->{
			dispatchEvent(msg);		
		})
		// 广播相关
		.match(AddEventListener.class, msg -> {
			eventListener.put(msg.topic, msg.paramIEventListener);
		}).match(RemoveEventListener.class, msg -> {
			eventListener.remove(msg.topic);
		}).match(HasEventListener.class, msg -> {
			getSender().tell(eventListener.containsKey(msg.topic), getSelf());
		}).build();
	}

	/**
	 * 处理分发信息
	 * 
	 * @param msg
	 */
	private void dispatchEvent(IObject msg) {
		String topic = msg.getUtfString("e");
		try {
			IEventListener iEventListener = eventListener.get(topic);
			if (iEventListener != null) {
				iEventListener.handleEvent(topic,msg);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	

	public static class EnterGrid implements Serializable {

		private static final long serialVersionUID = -7809307785484209371L;
		public final IUser user;

		public EnterGrid(IUser user) {
			super();
			this.user = user;
		}

	}

	public static class LeaveGrid implements Serializable {
		private static final long serialVersionUID = 2793900887224969528L;
		public final String userId;

		public LeaveGrid(String userId) {
			super();
			this.userId = userId;
		}
	}

	public static class BroadCastAllUser implements Serializable {
		private static final long serialVersionUID = 2143027987941307508L;

		public final String jsonString;

		public BroadCastAllUser(String jsonString) {
			super();
			this.jsonString = jsonString;
		}

	}




	public static class GetPlayers implements Serializable {

		private static final long serialVersionUID = -6878647894314032793L;

	}
}
