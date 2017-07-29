package com.as.chain.activity.battle;

import org.luaj.vm2.LuaValue;

import android.widget.TextView;

public class SkillHolder {
	public TextView name;
	public TextView cd;
	
	public LuaValue skill;
	public LuaValue hero;
	
	public int gridIdx;
}
