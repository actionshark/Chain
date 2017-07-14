package com.as.game;

import java.util.List;

import org.luaj.vm2.LuaValue;

import com.js.log.Level;
import com.js.log.Logger;

public class Hero {
	public static final String TAG = Hero.class.getSimpleName();
	
	public String name;
	public String country;
	
	public int health;
	public int attack;
	public int defence;
	
	public List<Skill> skills;
	
	public Hero() {
	}
	
	public boolean init(LuaValue data) {
		try {
			name = data.get("name").tojstring();
			country = data.get("country").tojstring();
			
			health = data.get("health").toint();
			attack = data.get("attack").toint();
			defence = data.get("defence").toint();
			
			
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return false;
	}
}
