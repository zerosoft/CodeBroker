package com.codebroker.core.actortype;

import com.codebroker.api.internal.IService;
import com.esotericsoftware.reflectasm.MethodAccess;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Service的Cglib反射封装
 * @author LongJu
 */
public class ServiceWithActorDecorate implements MethodInterceptor {

	IService subject;
	IService service;
	MethodAccess methodAccess;

	public ServiceWithActorDecorate(IService subject, IService service) {
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
	public Object intercept(Object arg0, Method method, Object[] objects, MethodProxy arg3) throws Throwable {
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
