package com.codebroker.demo.protobuff;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jodd.io.findfile.ClassScanner;
import jodd.util.ClassLoaderUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeClientRequestProtobuff {

	public static void main(String[] args) throws IOException {

		Map<String,Class> classMap= Maps.newHashMap();

		ClassScanner scanner = new ClassScanner() {
			@Override
			protected void onEntry(EntryData entryData) throws IOException, ClassNotFoundException {
				String className = entryData.getName().replaceAll("\\/", ".");
				if (classMap.containsKey(className)||className.contains("$")||!className.contains("OrBuilder")){
					return;
				}
				InputStream inputStream = entryData.openInputStream();
				int available = inputStream.available();
				byte[] bytes=new byte[available];
				inputStream.read(bytes);

				Class aClass = ClassLoaderUtil.loadClass("com.codebroker.protobuff."+className);
				classMap.put(className,aClass);
			}
		};

		scanner.scan("D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\build\\classes\\java\\test\\com\\codebroker\\protobuff\\");

		Configuration configuration = new Configuration(Configuration.getVersion());
		// 构建数据
		String path =System.getProperty("user.dir")+"\\src\\test\\java\\com\\codebroker\\demo\\protobuff\\template\\";
		// 第二步：设置模板文件所在的路径。
		configuration.setDirectoryForTemplateLoading(new File(path));

		// 第三步：设置模板文件使用的字符集。一般就是utf-8.
		configuration.setDefaultEncoding("utf-8");

		Template template = configuration.getTemplate("ProtobuffRequest.ftl");

		classMap.values().stream().forEach(clazz-> {
			try {
				buildClass(template,clazz);
			} catch (TemplateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private static void buildClass(Template template,Class t) throws TemplateException, IOException {
		Package aPackage = t.getPackage();
		String[] split = aPackage.getName().split("\\.");


		MethodAccess methodAccess = MethodAccess.get(t);


		List<Method> methodsList= Lists.newLinkedList();

		Method[] methods =t.getMethods();
		for (Method method : methods) {

			if (method.getDeclaringClass().equals(t)){

				String[] methodNames = methodAccess.getMethodNames();
				for (String method1 : methodNames) {
					if (method.getName().equals(method1)){
						//排除掉protobuff的ByteString
						if (com.google.protobuf.ByteString.class.equals(method.getReturnType())
								||method.getReturnType().getName().contains("$")
								||method.getName().contains("$")
								||method.getName().contains("OrBuilder")){
							continue;
						}else{
							methodsList.add(method);
						}
						System.out.println(method.getReturnType().getName()+"---------------"+method.getName());
					}
				}
			}
		}


		Map dataModel = new HashMap();
		dataModel.put("packagePath","com.codebroker.demo.request");
		dataModel.put("SubPackage",split[split.length-1]);
		dataModel.put("ProtobuffClass", t.getSimpleName().replace("OrBuilder",""));
		dataModel.put("methodsList", methodsList);

		Writer out = new FileWriter(new File("D:\\Users\\Documents\\github\\CodeBrokerGit\\AccountServer\\src\\main\\java\\com\\codebroker\\demo\\request\\"+ t.getSimpleName().replace("OrBuilder","Handler")+".java"));
		template.process(dataModel,out);
		out.close();
	}
}
