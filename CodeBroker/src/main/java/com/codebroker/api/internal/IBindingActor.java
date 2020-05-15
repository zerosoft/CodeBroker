package com.codebroker.api.internal;

import akka.actor.typed.ActorRef;

/**
 * 绑定Actor
 * @author LongJu
 */
public interface IBindingActor<T> {

    boolean bindingActor(ActorRef<T> ref);

}
