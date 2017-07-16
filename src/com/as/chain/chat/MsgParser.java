package com.as.chain.chat;

import org.json.JSONObject;

import com.js.log.Level;
import com.js.log.Logger;

public class MsgParser {
	public static final String TAG = MsgParser.class.getSimpleName();
	
	public static RspMsg parse(byte[] data, int offset, int length) {
		try {
			String str = new String(data, offset, length);
			Logger.getInstance().print(TAG, Level.V, str);
			JSONObject jo = new JSONObject(str);
			
			String uri = jo.getString(ChatMsg.KEY_URI);
			Class<?> cls = Class.forName("com.as.chain.chat.rsp." + uri);
			RspMsg msg = (RspMsg) cls.newInstance();
			
			msg.mParams = jo;
			msg.uri = uri;
			
			return msg;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return null;
	}
}
