package com.codebroker.api.internal.net;

import com.codebroker.protobuf.Login_C;
import com.codebroker.protobuf.Login_COrBuilder;
import com.esotericsoftware.reflectasm.FieldAccess;
import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.protobuf.Descriptors;
import com.google.protobuf.GeneratedMessageV3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class Test {

	public static void main(String[] args) {
		Login_C.Builder builder2 = Login_C.newBuilder();
		Login_C build = builder2.build();
		GeneratedMessageV3 messageOrBuilder=build;

		MethodAccess methodAccess = MethodAccess.get(Login_COrBuilder.class);

//		String[] methodNames = methodAccess.getMethodNames();
//		for (String methodName : methodNames) {
//
//			System.out.println("---------------"+methodName);
//		}
		Method[] methods = Login_COrBuilder.class.getMethods();
		for (Method method : methods) {
			if (method.getDeclaringClass().equals(Login_COrBuilder.class)){
				System.out.println(method.getName()+"---------------"+method.getReturnType().getName());
			}
		}


		Field[] methods1 = Login_C.getDefaultInstance().getClass().getFields();
		for (Field method : methods1) {
			System.out.println(method.getName()+"-@@@@-"+method.getType().getName());
		}


		FieldAccess fieldAccess = FieldAccess.get(Login_C.class);
		Field[] fields = fieldAccess.getFields();

		for (Field field : fields) {
			System.out.println(field.getName()+"=="+field.getType());
		}
		Login_C.Builder builder = Login_C.newBuilder();

		Map<Descriptors.FieldDescriptor, Object> allFields =
				builder.getAllFields();
		for (Descriptors.FieldDescriptor fieldDescriptor : allFields.keySet()) {
			System.out.println(fieldDescriptor.getName()+"======");
		}

	}
}
