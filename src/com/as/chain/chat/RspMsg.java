package com.as.chain.chat;

import org.json.JSONObject;

public abstract class RspMsg extends ChatMsg {
	protected JSONObject mParams;
	
	public abstract void perform(ChatClient server) throws Exception;
}
