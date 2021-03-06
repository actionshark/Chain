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
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.jse.JsePlatform;

import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;

public class ScriptMgr {
	public static final String TAG = ScriptMgr.class.getSimpleName();
	
	private static ScriptMgr sMgr;
	
	public static synchronized ScriptMgr getInstance() {
		if (sMgr == null) {
			sMgr = new ScriptMgr();
			sMgr.init();
		}
		
		return sMgr;
	}
	
	public static void print(String str) {
		Logger.getInstance().print(TAG, Level.I, str);
	}
	
	public static String toString(LuaValue lv, int depth) {
		return toString(lv, depth, 0);
	}
	
	private static String toString(LuaValue lv, int depth, int padding) {
		if (depth > 0 && lv instanceof LuaTable) {
			LuaTable lt = (LuaTable) lv;
			LuaValue[] keys = lt.keys();

			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < padding; i++) {
				sb.append("  ");
			}
			String tabEnd = sb.toString();
			String tab = sb.append("  ").toString();
			
			sb.setLength(0);
			sb.append("{\n");
			
			for (LuaValue key : keys) {
				LuaValue value = lt.get(key);
				
				sb.append(tab).append(key).append(" = ")
					.append(toString(value, depth - 1, padding + 1))
					.append('\n');
			}
			
			sb.append(tabEnd).append("}");
			
			return sb.toString();
		} else if (lv instanceof LuaString) {
			return String.format("\"%s\"", lv.tojstring());
		} else {
			return String.valueOf(lv);
		}
	}
	
	///////////////////////////////////////////////
	
	private final Globals mGlobals;
	
	private final List<Hero> mHeroList = new ArrayList<Hero>();
	private final Map<String, Hero> mHeroMap = new HashMap<String, Hero>();
	
	private ScriptMgr() {
		mGlobals = JsePlatform.standardGlobals();
	}
	
	private void init() {
		LuaValue main = mGlobals.loadfile(DataMgr.UPDATE_PATH + "src/common/main.lua").call();
		main.get("init").call(DataMgr.UPDATE_PATH + "src/");
		
		loadHeroes();
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
		return mHeroList;
	}
	
	public synchronized Hero getHero(String id) {
		return mHeroMap.get(id);
	}
}
