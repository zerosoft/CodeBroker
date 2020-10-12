package ${packagePath};

import com.codebroker.api.IClientRequestHandler;
import com.codebroker.api.IGameUser;
import com.codebroker.protobuff.${SubPackage}.${ProtobuffClass};

public class ${ProtobuffClass}Handler implements IClientRequestHandler<${ProtobuffClass}> {
	@Override
	public void handleClientRequest(IGameUser iGameUser, ${ProtobuffClass} request) {
	<#list methodsList as method>
		${method.returnType.name}  ${method.name?replace("get","")?lower_case} = request.get${method.name?uncap_first}();
	</#list>
	}
}