package com.as.chain.game;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaValue;

import com.js.log.Level;
import com.js.log.Logger;

public class Hero {
	public static final String TAG = Hero.class.getSimpleName();
	
	public String name;
	public int country;
	public int sex;
	
	public int health;
	public int attack;
	public int defence;
	
	public final List<Skill> skills = new ArrayList<Skill>();
	
	public Hero() {
	}
	
	public boolean init(LuaValue data) {
		try {
			name = data.get("name").tojstring();
			country = data.get("country").toint();
			sex = data.get("sex").toint();
			
			health = data.get("health").toint();
			attack = data.get("attack").toint();
			defence = data.get("defence").toint();
			
			LuaValue sks = data.get("skills");
			int len = sks.length();
			skills.clear();
			
			for (int i = 1; i <= len; i++) {
				Skill skill = new Skill();
				String path = sks.get(i).tojstring();
				skill.initByPath(path);
				skills.add(skill);
			}
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return false;
	}
}
