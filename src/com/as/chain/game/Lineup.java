package com.as.chain.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.as.app.Setting;
import com.js.log.Level;
import com.js.log.Logger;

public class Lineup {
	public static final String TAG = Lineup.class.getSimpleName();
	
	public static final String KEY_LINEUP_INDEX = "lineup_index";
	public static final String KEY_LINEUP_DATA = "lineup_data_%d";
	public static final String KEY_POSITION = "pos";
	public static final String KEY_HERO = "hero";
	
	public static final int DATA_NUM = 3;
	
	public static final int GRID_NUM = 9;
	
	public static final int MIN_HERO_NUM = 1;
	public static final int MAX_HERO_NUM = 4;
	
	public static class Node {
		public int position;
		public Hero hero;
	}
	
	public static void saveIndex(int index) {
		Setting.getInstance().setInt(KEY_LINEUP_INDEX, index);
	}
	
	public static int loadIndex() {
		int index = Setting.getInstance().getInt(Lineup.KEY_LINEUP_INDEX);
		
		if (index < 1 || index > DATA_NUM) {
			index = 1;
			saveIndex(index);
		}
		
		return index;
	}
	
	public static void saveData(List<Node> heroes) {
		saveData(loadIndex(), heroes);
	}
	
	public static void saveData(int index, List<Node> heroes) {
		try {
			JSONArray ja = new JSONArray();
			
			for (Node node : heroes) {
				JSONObject jo = new JSONObject();
				jo.put(KEY_POSITION, node.position);
				jo.put(KEY_HERO, node.hero.id);
				
				ja.put(ja.length(), jo);
			}
			
			Setting.getInstance().setString(
				String.format(KEY_LINEUP_DATA, index),
				ja.toString());
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
	}
	
	public static List<Node> loadData() {
		return loadData(loadIndex());
	}
	
	public static List<Node> loadData(int index) {
		List<Node> heroes = new ArrayList<Node>();
		Set<Integer> posSet = new HashSet<Integer>();
		Set<String> heroSet = new HashSet<String>();
		
		String key = String.format(KEY_LINEUP_DATA, index);
		String str = Setting.getInstance().getString(key);
		
		if (str != null) {
			try {
				JSONArray ja = new JSONArray(str);
				
				for (int i = 0; i < ja.length(); i++) {
					JSONObject jo = ja.getJSONObject(i);
					Node node = new Node();
					
					node.position = jo.getInt(KEY_POSITION);
					if (node.position < 1 || node.position > GRID_NUM
							|| posSet.contains(node.position)) {
						
						continue;
					}
					
					String id = jo.getString(KEY_HERO);
					node.hero = ScriptMgr.getInstance().getHero(id);
					if (node.hero == null || heroSet.contains(id)) {
						continue;
					}
					
					heroes.add(node);
					posSet.add(node.position);
					heroSet.add(id);
				}
			} catch (Exception e) {
				Logger.getInstance().print(TAG, Level.E, e);
			}
		}
		
		if (heroes.size() < MIN_HERO_NUM) {
			List<Hero> hs = ScriptMgr.getInstance().getHeroes();
			
			for (int i = 0; heroes.size() < MIN_HERO_NUM; i++) {
				Hero hero = hs.get(i);
				
				if (heroSet.contains(hero.id) == false) {
					for (int j = 1; j <= GRID_NUM; j++) {
						if (posSet.contains(j) == false) {
							Node node = new Node();
							node.position = j;
							node.hero = hero;
							
							heroes.add(node);
							posSet.add(node.position);
							heroSet.add(hero.id);
							break;
						}
					}
				
					break;
				}
			}
		} else if (heroes.size() > MAX_HERO_NUM) {
			do {
				heroes.remove(heroes.size() - 1);
			} while (heroes.size() > MAX_HERO_NUM);
		}
		
		Collections.sort(heroes, new Comparator<Node>() {
			@Override
			public int compare(Node a, Node b) {
				return a.position - b.position;
			}
		});
		
		return heroes;
	}
}
