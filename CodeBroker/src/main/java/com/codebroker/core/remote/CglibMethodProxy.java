package com.codebroker.core.remote;

import akka.actor.ActorSelection;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.codebroker.core.remote.IRemoteActorMessage.InvokeRPC;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * cglib代理
 *
 * @author zero
 */
public class CglibMethodProxy implements MethodInterceptor {
    // 代理发信息的actor
    private final ActorSelection subject;
    // 接口类的全名
    private final String className;
    private final int timeOut;

    public CglibMethodProxy(ActorSelection subject, String string, int timeOut) {
        super();
        this.subject = subject;
        this.className = string;
        this.timeOut = timeOut;
    }

    public <T> T newProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object arg0, Method method, Object[] objects, MethodProxy arg3) throws Throwable {
        String methodName = method.getName();
        IRemoteActorMessage.InvokeRPC invokeRPC = new InvokeRPC(className, methodName, objects);
        Timeout timeout = new Timeout(Duration.create(timeOut, TimeUnit.SECONDS));
        Future<Object> future = Patterns.ask(subject, invokeRPC, timeout);
        Object result = Await.result(future, timeout.duration());
        return result;
    }

}
