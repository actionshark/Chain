package com.as.game;

import org.luaj.vm2.LuaValue;

import com.stone.log.Logger;

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
			name = data.get("name").tojstring();
			
			type = data.get("type").toint();
			LuaValue skillType = Define.getSkillType();
			typeText = skillType.get(type).get("text").tojstring();
			
			desc = data.get("desc").tojstring();
			
			return true;
		} catch (Exception e) {
			Logger.print(TAG, e);
		}
		
		return false;
	}
}
