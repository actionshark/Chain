package com.as.chain.script;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import com.as.chain.util.DataMgr;

public class Define {
	public static void load() {
		Globals globals = ScriptMgr.getInstance().getGlobals();
		LuaValue define = globals.loadfile(DataMgr.FILE_PATH + "common/define.lua");
		define.call();
	}
	
	public static LuaValue getCountryType() {
		Globals globals = ScriptMgr.getInstance().getGlobals();
		return globals.get("CountryType");
	}
	
	public static LuaValue getSkillType() {
		Globals globals = ScriptMgr.getInstance().getGlobals();
		return globals.get("SkillType");
	}
	
	public static LuaValue getChainType() {
		Globals globals = ScriptMgr.getInstance().getGlobals();
		return globals.get("ChainType");
	}
}
