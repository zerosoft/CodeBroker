package ${packageName};

import com.google.protobuf.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.codebroker.api.JavaProtocolTransform;
<#list imports as item>
import ${item}.*;
</#list>

public class ${java_class_name} implements JavaProtocolTransform {
	
	public static final int REQUEST_ID = ${key};
	
	<#list FieldSequence as item>
	private ${item.type} ${item.name};
	</#list>

	<#list FieldSequence as item>

	public ${item.type} get${item.name?cap_first}()
	{
		return ${item.name};
	}

	public void set${item.name?cap_first}(${item.type} ${item.name})
	{
		this.${item.name} = ${item.name};
	}
	
	</#list>

	@Override
	public void protocolToJavaBean(Message message)
	{
		${protoName} protocal = (${protoName}) message;
		<#list FieldSequence as item>
		<#if item.collection>
			<#if item.pfile>
			{
				${item.type}> list = new ArrayList<${item.type?cap_first}>();
				for (${item.name?cap_first}Protocol pmessage : protocal.get${item.name?cap_first}List())
				{
					${item.type} protocol = new ${item.type}();
					protocol.protocolToJavaBean(pmessage);
					list.add(protocol);
				}
				this.set${item.name?cap_first}(list);
			}
			<#else>
				{
				${item.type} list = protocal.get${item.name?cap_first}List();
				this.set${item.name?cap_first}(list);
				}
			</#if>
		<#else>
			<#if item.pfile>
			{
				${item.type} bean = new ${item.type}();
				bean.protocolToJavaBean(protocal.get${item.name?cap_first}());
				this.set${item.name?cap_first}(bean);
			}
			<#else>
			this.set${item.name?cap_first}(protocal.get${item.name?cap_first}());
			</#if>
		</#if>
		</#list>
	}

	@Override
	public ${protoName} javaBeanToProtocol()
	{
		Builder newBuilder = ${protoName}.newBuilder();
		<#list FieldSequence as item>
		<#if item.collection>
			<#if item.pfile>{
			<${item.name?cap_first}Protocol list = new ArrayList<${item.name?cap_first}Protocol>();
			for (${item.type} JavaBean : this.get${item.name?cap_first}())
			{
				list.add((${item.name?cap_first}Protocol) JavaBean.javaBeanToProtocol());
			}
			newBuilder.addAll${item.name?cap_first}(list);
			}
			<#else>
			{
			newBuilder.addAll${item.name?cap_first}(this.get${item.name?cap_first}());
			}
			</#if>
		<#else>
		<#if item.pfile>
			{
			newBuilder.set${item.name?cap_first}(this.get${item.name?cap_first}().javaBeanToProtocol());
			}
		<#else>
			{
			newBuilder.set${item.name?cap_first}(this.get${item.name?cap_first}());
			}
		</#if>
		</#if>
		</#list>
		return newBuilder.build();
	}

	@Override
	public byte[] getByteArray()
	{
		return javaBeanToProtocol().toByteArray();
	}

	@Override
	public ${protoName} bytesToProtocol(byte[] bytes) throws InvalidProtocolBufferException
	{
		return ${protoName}.parseFrom(bytes);
	}
}