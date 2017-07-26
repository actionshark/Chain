package com.as.chain.game;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import com.js.log.Level;
import com.js.log.Logger;

public class Skill {
	public static final String TAG = Skill.class.getSimpleName();
	
	public String name;
	public int type;
	public String desc;
	
	public int magicCost = -1;
	public int firstCd = -1;
	public int repeatCd = -1;
	
	public Skill() {
	}
	
	public boolean init(LuaValue data) {
		try {
			name = data.get("name").tojstring();
			type = data.get("type").toint();
			desc = data.get("desc").tojstring();
			
			LuaValue temp = data.get("magic_cost");
			if (temp.isnil()) {
				magicCost = -1;
			} else {
				magicCost = temp.toint();
			}
			
			temp = data.get("first_cd");
			if (temp.isnil()) {
				firstCd = -1;
			} else {
				firstCd = temp.toint();
			}
			
			temp = data.get("repeat_cd");
			if (temp.isnil()) {
				repeatCd = -1;
			} else {
				repeatCd = temp.toint();
			}
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return false;
	}
	
	public boolean initByPath(String path) {
		Globals globals = ScriptMgr.getInstance().getGlobals();
		LuaValue data = globals.loadfile(path).call();
		return init(data);
	}
}
