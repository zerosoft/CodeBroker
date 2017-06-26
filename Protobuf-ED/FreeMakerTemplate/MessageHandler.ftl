package ${packageName};

import com.codebroker.api.JavaProtocolTransform;
import com.codebroker.api.IUser;
import com.google.protobuf.InvalidProtocolBufferException;
import com.huahang.handlers.AbstractClientRequestHandler;
import ${beanPath};
<#list imports as item>
import ${item}.*;
</#list>

public class  ${java_class_name}RequestHandler extends AbstractClientRequestHandler{

	public static final int REQUEST_ID =${java_class_name}Bean.REQUEST_ID;

	@Override
	public JavaProtocolTransform bytesToProtocol(byte[] bs) throws InvalidProtocolBufferException {
		${java_class_name}Bean bean=new ${java_class_name}Bean();
		bean.protocolToJavaBean(bean.bytesToProtocol(bs));
		return bean;
	}

	@Override
	public boolean verifyParams(IUser user, JavaProtocolTransform params) {
		${java_class_name}Bean bean=(${java_class_name}Bean) params;
		return false;
	}

	@Override
	public JavaProtocolTransform handleRequest(IUser user, JavaProtocolTransform params) {
		${java_class_name}Bean bean=(${java_class_name}Bean) params;
		return null;
	}

}
