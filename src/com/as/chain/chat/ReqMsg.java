package com.as.chain.chat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.json.JSONObject;

import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;

public abstract class ReqMsg extends ChatMsg {
	public static final String TAG = ReqMsg.class.getSimpleName();
	
	public ReqMsg() {
		uri = this.getClass().getSimpleName();
	}
	
	public void send() {
		try {
			JSONObject jo = new JSONObject();
			Field[] fields = this.getClass().getFields();
			
			for (Field field : fields) {
				int mod = field.getModifiers();
				
				if ((mod & Modifier.STATIC) == 0) {
					String name = field.getName();
					Object value = field.get(this);
					
					if (value != null) {
						jo.put(name, value);
					}
				}
			}
			
			ChatClient cc = DataMgr.getClient();
			cc.send(jo);
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
	}
}
