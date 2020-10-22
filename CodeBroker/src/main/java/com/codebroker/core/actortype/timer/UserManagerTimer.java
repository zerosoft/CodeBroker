package com.codebroker.core.actortype.timer;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.*;
import com.codebroker.core.actortype.UserManager;
import com.codebroker.core.actortype.message.IUserManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * UserManager周期时间检测
 */
public class UserManagerTimer {

	public static final String IDENTIFY = UserManagerTimer.class.getSimpleName();

	public interface Command {}

	private static final Object TIMER_KEY = new Object();

	public enum Timeout implements Command {
		INSTANCE
	}

	public enum TimeCheck implements Command {
		INSTANCE
	}

	public static Behavior<Command> create(ActorRef<IUserManager> target, Duration interval) {
		return Behaviors.withTimers(timers -> new UserManagerTimer(timers, target, interval).start(TimeCheck.INSTANCE));
	}

	private final TimerScheduler<Command> timers;
	private final ActorRef<IUserManager> target;
	private final Duration interval;

	private UserManagerTimer(TimerScheduler<Command> timers, ActorRef<IUserManager> target, Duration interval) {
		this.timers = timers;
		this.target = target;
		this.interval = interval;
	}

	private Behavior<Command> start(Command message) {
		timers.startTimerAtFixedRate(TIMER_KEY, TimeCheck.INSTANCE, interval);
		return Behaviors.setup(context -> new Active(context, message));
	}

	private class Active extends AbstractBehavior<Command> {

		private final List<Command> buffer = new ArrayList<>();

		Active(ActorContext<Command> context, Command firstCommand) {
			super(context);
			buffer.add(firstCommand);
		}

		@Override
		public Receive<Command> createReceive() {
			return newReceiveBuilder()
					.onMessage(Timeout.class, message -> onTimeout())
					.onMessage(TimeCheck.class, message -> onTimeCheck())
					.build();
		}

		private Behavior<Command> onTimeCheck() {
			target.tell(IUserManager.TimeCheck.INSTANCE);
			return Behaviors.same();
		}

		private Behavior<Command> onTimeout() {
			timers.cancel(TIMER_KEY);
			return Behaviors.stopped();
		}
	}
}