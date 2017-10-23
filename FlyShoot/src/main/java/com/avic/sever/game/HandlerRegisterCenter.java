package com.avic.sever.game;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import com.codebroker.extensions.request.AppListenerExtension;

import jodd.io.FileUtil;
import jodd.io.findfile.ClassScanner;
import jodd.util.ClassLoaderUtil;

public class HandlerRegisterCenter {
	
	public static void registerServerEventHandler(AppListenerExtension cokExtension) {
		  URL url = ClassLoaderUtil.getResourceUrl("com/avic/sever/game/handler");
		  File containerFile = FileUtil.toContainerFile(url);
		
		   ClassScanner cs = new ClassScanner() {
		        @Override
		        protected void onEntry(EntryData entryData) throws IOException {
					try {
						@SuppressWarnings("rawtypes")
						Class loadClass = ClassLoaderUtil.loadClass("com.avic.sever.game.handler."+entryData.getName());
						Field[] fields = loadClass.getFields();
						for (Field field : fields) {
							if (field.getName().equals("REQUEST_ID")) {
								int requestId = field.getInt(loadClass.newInstance());
								cokExtension.addRequestHandler(requestId, loadClass);
							}
						}
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
		        }
		    };
		    cs.setIncludeResources(true);
		    cs.scan(containerFile);
	}
}
