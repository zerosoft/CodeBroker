package com.avic.sever.game;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import com.avic.sever.game.handler.login.CreateUserRequest;
import com.avic.sever.game.handler.login.LoginRequest;
import com.avic.sever.game.handler.room.*;
import com.avic.sever.game.handler.world.SendMessageWorldRequest;
import com.codebroker.extensions.request.AppListenerExtension;

import com.codebroker.util.LogUtil;
import jodd.io.FileUtil;
import jodd.io.findfile.ClassScanner;
import jodd.util.ClassLoaderUtil;

public class HandlerRegisterCenter {
	
	public static void registerServerEventHandler(AppListenerExtension cokExtension) {
		cokExtension.addRequestHandler(CreateUserRequest.REQUEST_ID,CreateUserRequest.class);
		cokExtension.addRequestHandler(LoginRequest.REQUEST_ID,LoginRequest.class);

		cokExtension.addRequestHandler(CreateRoomRequest.REQUEST_ID,CreateRoomRequest.class);
		cokExtension.addRequestHandler(GetAllRoomRequest.REQUEST_ID,GetAllRoomRequest.class);
		cokExtension.addRequestHandler(JoinRoomRequest.REQUEST_ID,JoinRoomRequest.class);
		cokExtension.addRequestHandler(RemoveRoomRequest.REQUEST_ID,RemoveRoomRequest.class);
		cokExtension.addRequestHandler(SendMessageRoomRequest.REQUEST_ID,SendMessageRoomRequest.class);

		cokExtension.addRequestHandler(SendMessageWorldRequest.REQUEST_ID,SendMessageWorldRequest.class);
	}
}
