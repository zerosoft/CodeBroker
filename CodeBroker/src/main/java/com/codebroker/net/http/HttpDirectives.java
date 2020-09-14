//package com.codebroker.net.http;
//
//import akka.http.javadsl.HandlerProvider;
//import akka.http.javadsl.server.AllDirectives;
//import com.codebroker.api.AppListener;
//import com.codebroker.api.internal.JarLoader;
//import com.codebroker.core.ContextResolver;
//import com.codebroker.core.ServerEngine;
//import com.codebroker.core.data.CObject;
//
//public class HttpDirectives extends AllDirectives {
//    public HandlerProvider createRoute() {
//        return concat(
//                path("hello", () ->
//                        get(() ->{
//                            AppListener appListener = ContextResolver.getAppListener();
//                            appListener.destroy(CObject.newInstance());
//                            appListener.init(CObject.newInstance());
//                            return complete("<h1>Say hello to akka-http</h1>");
//                                }
//
//                        )
//                ),
//                path("hello1", () ->
//                        get(() ->{
//                            ClassLoader classLoader = ServerEngine.getInstance().getiClassLoader();
//                            try {
//                                JarLoader jarLoader = new JarLoader();
//                                ClassLoader iClassLoader = jarLoader.loadClasses(new String[]{"D:\\Users\\Documents\\github\\CodeBrokerGit\\account_server\\build\\libs"}, ClassLoader.getSystemClassLoader());
//                                ServerEngine.getInstance().setiClassLoader(iClassLoader);
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            return complete("<h1>Say hello to akka-http</h1>");
//                                }
//
//                        ))
//        );
//    }
//}
