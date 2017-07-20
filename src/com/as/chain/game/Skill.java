package com.as.chain.game;

import org.luaj.vm2.LuaValue;

import com.js.log.Level;
import com.js.log.Logger;

public class Skill {
	public static final String TAG = Skill.class.getSimpleName();
	
	public String name;
	
	public int type;
	public String typeText;
	
	public String desc;
	
	public Skill() {
	}
	
	public boolean init(LuaValue data) {
		try {
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return false;
	}
	
	public boolean initByPath(String path) {
		return true;
	}
}
