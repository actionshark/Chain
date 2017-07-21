package com.as.chain.game;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;

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
		
		mGlobals.loadfile(DataMgr.UPDATE_PATH + "src/common/define.lua").call();
	}
	
	public Globals getGlobals() {
		return mGlobals;
	}
	
	public LuaValue getGlobal(String key) {
		return mGlobals.get(key);
	}
	
	public LuaValue getGlobal(int index) {
		return mGlobals.get(index);
	}
	
	public List<Hero> getHeroes() {
		List<Hero> heroes = new ArrayList<Hero>();
		
		File dir = new File(DataMgr.UPDATE_PATH + "src/hero");
		File[] files = dir.listFiles();
		
		for (File file : files) {
			LuaValue data = mGlobals.loadfile(file.getAbsolutePath()).call();
			String name = file.getName();
			Hero hero = new Hero(name.substring(0, name.length() - 4));
			hero.init(data);
			heroes.add(hero);
		}
		
		final Collator collator = Collator.getInstance(Locale.CHINA);
		Collections.sort(heroes, new Comparator<Hero>() {
			@Override
			public int compare(Hero a, Hero b) {
				return collator.compare(a.name, b.name);
			}
		});
		
		return heroes;
	}
	
	public Hero getHero(String name) {
		try {
			String path = String.format("%ssrc/hero/%s.lua", DataMgr.UPDATE_PATH, name);
			LuaValue data = mGlobals.loadfile(path).call();
			Hero hero = new Hero(name);
			
			if (hero.init(data)) {
				return hero;
			}
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
		
		return null;
	}
}
