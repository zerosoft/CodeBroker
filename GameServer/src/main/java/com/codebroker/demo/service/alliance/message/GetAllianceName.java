package com.codebroker.demo.service.alliance.message;

import com.codebroker.api.IGameUser;

public class GetAllianceName {
	public final IGameUser gameUser;
	public final String message;

	public GetAllianceName(IGameUser gameUser, String message) {
		this.gameUser = gameUser;
		this.message = message;
	}
}
