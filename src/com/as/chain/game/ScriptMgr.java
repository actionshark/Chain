package com.as.chain.game;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.as.chain.util.DataMgr;

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
	
	private List<Hero> mHeroList;
	private final Map<String, Hero> mHeroMap = new HashMap<String, Hero>();
	
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
	
	private synchronized void loadHeroes() {
		if (mHeroList != null) {
			return;
		}

		mHeroList = new ArrayList<Hero>();
		
		File dir = new File(DataMgr.UPDATE_PATH + "src/hero");
		File[] files = dir.listFiles();
		
		for (File file : files) {
			LuaValue data = mGlobals.loadfile(file.getAbsolutePath()).call();
			String name = file.getName();
			Hero hero = new Hero(name.substring(0, name.length() - 4));
			hero.init(data);
			
			mHeroList.add(hero);
			mHeroMap.put(hero.id, hero);
		}
		
		final Collator collator = Collator.getInstance(Locale.CHINA);
		Collections.sort(mHeroList, new Comparator<Hero>() {
			@Override
			public int compare(Hero a, Hero b) {
				return collator.compare(a.name, b.name);
			}
		});
	}
	
	public synchronized List<Hero> getHeroes() {
		loadHeroes();
		return new ArrayList<Hero>(mHeroList);
	}
	
	public Hero getHero(String id) {
		return mHeroMap.get(id);
	}
}
