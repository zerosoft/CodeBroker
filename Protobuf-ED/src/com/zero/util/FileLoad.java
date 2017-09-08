package com.zero.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jodd.io.StreamUtil;
import jodd.io.findfile.ClassScanner;
import jodd.util.ClassLoaderUtil;
import jodd.util.ClassUtil;

public class FileLoad {
	public static final String PB_MESSAGEKEY_PATH = "D:\\Users\\xl\\workspace\\Protobuf-ED\\src\\system\\pb_messagekey.proto";
	public static final String BEAN_PATH =    "D:\\Users\\xl\\workspace\\FlayShooting\\src\\main\\java\\com\\huahang\\message\\bean\\";
	public static final String HANDLER_PATH = "D:\\Users\\xl\\workspace\\FlayShooting\\src\\main\\java\\com\\huahang\\handlers\\";
	private static Configuration configuration;
	private static List<String> classNames = new ArrayList<>();

	static Map<String, Class> messageBuilder = new HashMap<String, Class>();
	static Map<String, PBClass> pbClassMap = new HashMap<>();
	static Map<String, Class> message = new HashMap<String, Class>();
	static Map<String, String> messageKey = new HashMap<>();

	public void init() {
		try {
			configuration = new Configuration();
			File root = new File("");
			File absoluteFile = root.getAbsoluteFile();
			configuration.setDirectoryForTemplateLoading(new File(absoluteFile + "/FreeMakerTemplate"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException {

		FileLoad fileLoad = new FileLoad();
		fileLoad.init();
		findClazz();
		System.out.println("start build message");
		buildMessageKey();

		findPBInfo();

		buildBeanAndHandler();

	}

	public static void findPBInfo() {
		for (Entry<String, Class> entry : message.entrySet()) {
			if (messageBuilder.containsKey(entry.getKey() + "OrBuilder")) {
				PBClass pbClass = new PBClass();
				pbClass.className = entry.getKey();
				Class value = entry.getValue();
				Field[] accessibleFields = ClassUtil.getAccessibleFields(value);
				Class classBuilder = messageBuilder.get(entry.getKey() + "OrBuilder");
				Method[] accessibleMethods = ClassUtil.getAccessibleMethods(classBuilder);
				pbClass.parName = entry.getValue().getDeclaringClass().getSimpleName().toLowerCase();
				pbClass.protoName = value.getName().replace("$", ".");
				pbClass.imports.add(value.getName().replace("$", "."));
				for (Field field : accessibleFields) {
					String fieldName = field.getName();
					if (field.getModifiers() == java.lang.reflect.Modifier.PRIVATE
							&& !fieldName.equals("memoizedIsInitialized") && !fieldName.equals("memoizedSerializedSize")
							&& !fieldName.equals("bitField0_")) {
						for (Method method : accessibleMethods) {

							if (method.getName().startsWith("get")
									&& method.getName().toLowerCase()
											.contains(fieldName.substring(0, fieldName.length() - 1).toLowerCase())
									&& method.getParameters().length == 0) {
								if (java.util.List.class.isAssignableFrom(method.getReturnType())) {
									PBInfo pbInfo = new PBInfo();
									pbInfo.collection = true;
									pbInfo.name = fieldName.substring(0, fieldName.length() - 1);
									String typeName = field.getGenericType().getTypeName();
									if (field.getGenericType().getTypeName()
											.equals("com.google.protobuf.LazyStringList")) {
										pbInfo.type = "java.util.List<java.lang.String>";
									} else {
										pbInfo.type = typeName;
									}
									pbClass.pbInfos.add(pbInfo);
								} else {
									if (method.getName().toLowerCase()
											.endsWith(fieldName.substring(0, fieldName.length() - 1).toLowerCase())) {
										PBInfo pbInfo = new PBInfo();
										pbInfo.name = fieldName.substring(0, fieldName.length() - 1);
										pbInfo.type = method.getReturnType().getTypeName();
										pbClass.pbInfos.add(pbInfo);
									}
								}
							}
						}
					}
				}
				pbClassMap.put(pbClass.className, pbClass);
			}
		}
	}

	public static void buildBeanAndHandler() {
		try {
			for (Entry<String, PBClass> string : pbClassMap.entrySet()) {
				Template template = configuration.getTemplate("ProtobufExample.ftl");
				Map<String, Object> dataModel = new HashMap<String, Object>();
				dataModel.put("protoName", string.getValue().protoName);
				dataModel.put("packageName", "com.huahang.message.bean" + "." + string.getValue().parName);
				dataModel.put("java_class_name", Util.getJavaName(string.getKey()) + "Bean");
				dataModel.put("imports", string.getValue().imports);
				dataModel.put("FieldSequence", string.getValue().pbInfos);
				dataModel.put("key", messageKey.get(string.getKey() + "_VALUE"));
				Util.isExist(BEAN_PATH + string.getValue().parName.toLowerCase() + "\\"
						+ Util.getJavaName(string.getKey()) + "Bean.java");
				Writer out = new OutputStreamWriter(
						new FileOutputStream(BEAN_PATH + string.getValue().parName.toLowerCase() + "\\"
								+ Util.getJavaName(string.getKey()) + "Bean.java"),
						"UTF-8");
				template.process(dataModel, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}

		try {
			for (Entry<String, PBClass> string : pbClassMap.entrySet()) {
				Template template = configuration.getTemplate("MessageHandler.ftl");
				Map<String, Object> dataModel = new HashMap<String, Object>();
				dataModel.put("protoName", string.getValue().protoName);
				dataModel.put("packageName", "com.huahang.handlers." + string.getValue().parName);
				dataModel.put("java_class_name", Util.getJavaName(string.getKey()));
				dataModel.put("imports", string.getValue().imports);
				dataModel.put("beanPath", "com.huahang.message.bean." + string.getValue().parName + "."
						+ Util.getJavaName(string.getKey()) + "Bean");

				dataModel.put("FieldSequence", string.getValue().pbInfos);
				dataModel.put("key", messageKey.get(string.getKey() + "_VALUE"));
				Util.isExist(HANDLER_PATH + string.getValue().parName.toLowerCase() + "\\"
						+ Util.getJavaName(string.getKey()) + "RequestHandler.java");
				Writer out = new OutputStreamWriter(
						new FileOutputStream(HANDLER_PATH + string.getValue().parName.toLowerCase() + "\\"
								+ Util.getJavaName(string.getKey()) + "RequestHandler.java"),
						"UTF-8");
				template.process(dataModel, out);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	public static void buildMessageKey() {
		MessageKeyStore readClass = MessageKeyStore.readClass();
		int index = 100+readClass.classNameKey.size();
		List<String> key = new ArrayList<>();
		for (String string : classNames) {
			if (readClass.classNameKey.containsKey(string)) {
				key.add(string + " = " + readClass.classNameKey.get(string));
			} else {
				key.add(string + " = " + (index++));
				readClass.classNameKey.put(string, index);
			}
		}

		MessageKeyStore.writeClass(readClass);
		try {

			Template template = configuration.getTemplate("PBMessageKey.ftl");
			Map<String, Object> dataModel = new HashMap<String, Object>();
			dataModel.put("FieldSeqence", key);
			Writer out = new OutputStreamWriter(new FileOutputStream(PB_MESSAGEKEY_PATH), "UTF-8");
			template.process(dataModel, out);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (TemplateException e) {
			e.printStackTrace();
		}
	}

	public static void findClazz() {
		ClassScanner cs = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData)
					throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
				if (entryData.getName().endsWith("PBSystem.java")) {
					return;
				}
				System.out.println(
						"---> " + entryData.getName() + ':' + entryData.getArchiveName() + "\t\t" );
				Class loadClass = ClassLoaderUtil.loadClass(
						entryData.getName().replaceAll("\\/", ".").substring(1, entryData.getName().length() - 5));
				Class[] subclasss = loadClass.getClasses();
				for (Class string : subclasss) {
					// 找到消息实体类
					if (com.google.protobuf.GeneratedMessage.class.isAssignableFrom(string)) {
						message.put(string.getSimpleName(), string);
						classNames.add(string.getSimpleName()); 
					}
					// Builder
					else if (com.google.protobuf.MessageOrBuilder.class.isAssignableFrom(string)) {
						messageBuilder.put(string.getSimpleName(), string);
					}
					Class[] innerSubClass = string.getClasses();
					for (Class clazz : innerSubClass) {
						// 取得枚举类
						if (clazz.isEnum()) {
							Object[] enumConstants = clazz.getEnumConstants();
							for (Object string3 : enumConstants) {
								Field[] accessibleFields = ClassUtil.getAccessibleFields(string3.getClass());
								for (Field string4 : accessibleFields) {
									if (string4.getModifiers() == (java.lang.reflect.Modifier.PUBLIC
											+ java.lang.reflect.Modifier.STATIC + java.lang.reflect.Modifier.FINAL)) {
										messageKey.put(string4.getName(),string4.getDeclaringClass().getName().replaceAll("\\$", ".") + "."
														+ string4.getName());
									}
								}
							}
						}
					}
				}
			}
		};
		cs.setIncludeResources(true);
		cs.scan("./src-gen");
	}

}
