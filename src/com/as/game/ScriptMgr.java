package com.as.game;

import org.luaj.vm2.Globals;
import org.luaj.vm2.lib.jse.JsePlatform;

public class ScriptMgr {
	public static final String TAG = ScriptMgr.class.getSimpleName();
	
	private static ScriptMgr sMgr;
	
	public static synchronized void createMgr() {
		sMgr = new ScriptMgr();
	}
	
	public static synchronized ScriptMgr getInstance() {
		if (sMgr == null) {
			createMgr();
		}
		
		return sMgr;
	}
	
	///////////////////////////////////////////////
	
	private final Globals mGlobals;
	
	public ScriptMgr() {
		mGlobals = JsePlatform.standardGlobals();
		
		Define.load();
	}
	
	public Globals getGlobals() {
		return mGlobals;
	}
}
