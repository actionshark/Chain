package com.as.chain.game;

import java.util.ArrayList;
import java.util.List;

import org.luaj.vm2.LuaValue;

import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;

public class Hero {
	public static final String TAG = Hero.class.getSimpleName();
	
	public static final String DEF_HERO = "caocao";
	
	private static int sHealthMax = 1;
	private static int sAttackMax = 1;
	private static int sDefenceMax = 1;
	
	public final String id;
	
	public String name;
	public String desc;
	
	public int country;
	public int sex;
	
	public int health;
	public int attack;
	public int defence;
	
	public final List<Skill> skills = new ArrayList<Skill>();
	
	public Hero(String id) {
		this.id = id;
	}
	
	public boolean init(LuaValue data) {
		try {
			name = data.get("name").tojstring();
			desc = data.get("desc").tojstring();
			
			country = data.get("country").toint();
			sex = data.get("sex").toint();
			
			health = data.get("health").toint();
			if (health > sHealthMax) {
				sHealthMax = health;
			}
			
			attack = data.get("attack").toint();
			if (attack > sAttackMax) {
				sAttackMax = attack;
			}
			
			defence = data.get("defence").toint();
			if (defence > sDefenceMax) {
				sDefenceMax = defence;
			}
			
			LuaValue sks = data.get("skills");
			int len = sks.length();
			skills.clear();
			
			for (int i = 1; i <= len; i++) {
				Skill skill = new Skill();
				String path = sks.get(i).tojstring();
				skill.initByPath(String.format("%ssrc/skill/%s.lua", DataMgr.UPDATE_PATH, path));
				skills.add(skill);
			}
			
			return true;
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return false;
	}
	
	public static int getHealthMax() {
		return sHealthMax;
	}
	
	public static int getAttackMax() {
		return sAttackMax;
	}
	
	public static int getDefenceMax() {
		return sDefenceMax;
	}
}
