package com.codebroker.extensions;

import com.codebroker.api.AppListener;
import com.codebroker.api.IGameUser;
import com.codebroker.core.entities.GameUser;
import org.apache.commons.io.FileUtils;
import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.applet.AppletListener;

import javax.script.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

public class JythonExtension {

	private static JythonExtension instance = null;


	public static JythonExtension getInstance(){
		if(instance == null){
			instance = new JythonExtension();
		}
		return instance;

	}

	private static PyObject pyObject = null;

	private Logger log= LoggerFactory.getLogger(JythonExtension.class);

//	public JythonExtension() {
//		File f = new File("./extensions/");
//		Properties props = new Properties();
//		props.setProperty("python.path", f.getAbsolutePath());
//		PythonInterpreter.initialize(System.getProperties(), props, new String[0]);
//		this.interp = new PythonInterpreter();
//	}
//
//	public void init() {
//		try {
//			loadPyScript();
//			this.fnInit.__call__();
//		} catch (Exception e) {
//			this.log.warn("Failed initializing Python Extension: " + getName() + " -> " + e);
//		}
//	}
//
//	public void destroy() {
//		try {
//			this.fnDestroy.__call__();
//		} catch (Exception e) {
//			this.log.warn("Failed destroying Python Extension: " + getName() + " -> " + e);
//		}
//	}
//
//	private String getName() {
//		return "JT";
//	}
//
//	public void handleClientRequest(String cmdName, IGameUser sender, Object params) throws RuntimeException {
//		try {
//			PyObject[] args = { (PyObject)new PyString(cmdName),
//					(PyObject)new PyJavaInstance(sender),
//					(PyObject)new PyJavaInstance(params) };
//			this.fnHandleClientRequest.__call__(args);
//		} catch (Exception e) {
//			this.log.warn("handleClientRequest error in Python Extension: " + getName() + " -> " + e);
//		}
//	}
//
//	public Object handleInternalMessage(String cmdName, Object params) {
//		Object result = null;
//		try {
//			PyObject[] args = { (PyObject)new PyString(cmdName),
//					(PyObject)new PyJavaInstance(params) };
//			result = this.fnHandleMessage.__call__(args);
//		} catch (Exception e) {
//			this.log.warn("handleInternalMessage error in Python Extension: " + getName() + " -> " + e);
//		}
//		return result;
//	}
//
//	private void loadPyScript() throws Exception {
//		String sourceFilePath = "extensions/" + getName() + "/";
//		String pyScript = loadSourceCode(sourceFilePath);
//		this.interp.exec(pyScript);
////		this.interp.set("_sfsApi", (PyObject)new PyJavaInstance(this.sfsApi));
////		this.interp.set("_base", (PyObject)new PyJavaInstance(this));
////		this.interp.set("_app", SmartFoxServer.getInstance());
////		this.interp.set("_sfsGameApi", SmartFoxServer.getInstance().getAPIManager().getGameApi());
//		String fnTraceCode = "def trace(*args):\n\tfrom java.lang import Object\n\tfrom jarray import array\n\t_base.trace(array(args, Object))\n";
//		this.interp.exec(fnTraceCode);
//		this.fnInit = this.interp.get("init");
//		this.fnDestroy = this.interp.get("destroy");
//		this.fnHandleClientRequest = this.interp.get("handleClientRequest");
//		this.fnHandleMessage = this.interp.get("handleInternalMessage");
//	}

	public static void main(String[] args) throws FileNotFoundException, ScriptException {
		StringWriter writer = new StringWriter();
		ScriptContext context = new SimpleScriptContext();
		context.setWriter(writer);
		Properties properties = new Properties();
		properties.setProperty("python.path", "D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\src\\main\\resource\\script\\");
		PythonInterpreter.initialize(System.getProperties(), properties, new String[]{""});
		PythonInterpreter pi = new PythonInterpreter();

		JythonObjectFactory factory = JythonObjectFactory.getInstance();
		AppListener building = (AppListener) factory.createObject(AppListener.class, "PythonScriptListener");
		building.init("www");
		IGameUser iGameUser=new GameUser("122",null);
		building.userLogin(iGameUser);
//		ScriptEngineManager manager = new ScriptEngineManager();
//		ScriptEngine engine = manager.getEngineByName("python");

//		Object eval = engine.eval(new FileReader(new File("D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\src\\main\\resource\\script\\PythonScriptListener.py")), context);

//		PyCode compile = pi.compile(new FileReader(new File("D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\src\\main\\resource\\script\\PythonScriptListener.py")));
//		compile.invoke("say");
//		pi.execfile("D:\\Users\\Documents\\github\\CodeBrokerGit\\CodeBroker\\src\\main\\resource\\script\\PythonScriptListener.py");


	}

	private String loadSourceCode(String sourceFilePath) throws IOException {
		return FileUtils.readFileToString(new File(sourceFilePath), Charset.forName("UTF-8"));
	}
}
class JythonObjectFactory {
	private static JythonObjectFactory instance = null;
	private static PyObject pyObject = null;

	protected JythonObjectFactory() {

	}

	public static JythonObjectFactory getInstance(){
		if(instance == null){
			instance = new JythonObjectFactory();
		}

		return instance;

	}


	public Object createObject(Object interfaceType, String moduleName){
		Object javaInt = null;
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("from " + moduleName + " import " + moduleName);

		pyObject = interpreter.get(moduleName);

		try {

			PyObject newObj = pyObject.__call__();

			javaInt = newObj.__tojava__(Class.forName(interfaceType.toString().substring(
					interfaceType.toString().indexOf(" ")+1, interfaceType.toString().length())));
		} catch (ClassNotFoundException ex) {
//			Logger.getLogger(JythonObjectFactory.class.getName()).log(Level.SEVERE, null, ex);
		}

		return javaInt;
	}

}
