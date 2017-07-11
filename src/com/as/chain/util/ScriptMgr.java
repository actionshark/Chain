package com.as.chain.util;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptMgr {
	private static ScriptMgr sMgr;
	
	public static void createMgr() {
		sMgr = new ScriptMgr();
	}
	
	public static ScriptMgr getInstance() {
		return sMgr;
	}
	
	///////////////////////////////////////////////
	
	private final Globals mGlobals;
	
	private final String mParentPath = DataMgr.getSrcDir().getPath();
	
	public ScriptMgr() {
		mGlobals = JsePlatform.standardGlobals();
		init();
	}
	
	private void init() {
		LuaValue common = mGlobals.loadfile(DataMgr.getSrcDir() + "");
	}
}
