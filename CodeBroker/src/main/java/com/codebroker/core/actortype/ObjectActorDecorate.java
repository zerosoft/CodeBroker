package com.codebroker.core.actortype;

import com.esotericsoftware.reflectasm.MethodAccess;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ObjectActorDecorate<T> implements MethodInterceptor {

	T subject;
	T service;
	MethodAccess methodAccess;

	public ObjectActorDecorate(T subject, T service) {
		super();
		this.subject = subject;
		this.service = service;
		this.methodAccess=MethodAccess.get(subject.getClass());
	}

	public <T> T newProxyInstance(Class<T> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(this);
		return (T) enhancer.create();
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		if (method.getName().equals("hashCode")){
			return service.hashCode();
		}else if (method.getName().equals("toString")){
			return service.toString();
		}else if (method.getName().equals("equals")){
			return service.equals(objects);
		}
		return methodAccess.invoke(subject,method.getName(),objects);
	}
}
