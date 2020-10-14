package ${packagePath};

import com.codebroker.api.IGameUser;
import com.codebroker.demo.AbstractClientRequestHandler;
import com.codebroker.protobuff.${SubPackage}.${ProtobuffClass};

public class ${ProtobuffClass}Handler extends AbstractClientRequestHandler<${ProtobuffClass}> {
	@Override
	public void handleClientProtocolBuffersRequest(IGameUser iGameUser, ${ProtobuffClass} request) {
	<#list methodsList as method>
		${method.returnType.name}  ${method.name?replace("get","")?lower_case} = request.${method.name}();
	</#list>
	}
}