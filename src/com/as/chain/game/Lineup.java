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
import com.as.chain.chat.req.UploadLineup;
import com.js.log.Level;
import com.js.log.Logger;

public class Lineup {
	public static final String TAG = Lineup.class.getSimpleName();
	
	public static final String KEY_LINEUP_INDEX = "lineup_index";
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
	
	private static final List<List<Node>> sData = new ArrayList<List<Node>>();
	private static String SERVER_DATA;
	
	private static final Comparator<Node> CMP = new Comparator<Node>() {
		@Override
		public int compare(Node a, Node b) {
			return a.position - b.position;
		}
	};
	
	static {
		for (int i = 0; i < DATA_NUM; i++) {
			sData.add(new ArrayList<Node>());
		}
	}
	
	public static int checkIndex(int index) {
		if (index < 0 || index >= DATA_NUM) {
			return 0;
		} else {
			return index;
		}
	}
	
	public static void saveIndex(int index) {
		Setting.getInstance().setInt(KEY_LINEUP_INDEX, checkIndex(index));
	}
	
	public static int loadIndex() {
		int index = Setting.getInstance().getInt(Lineup.KEY_LINEUP_INDEX);
		return checkIndex(index);
	}
	
	public static void saveData(List<Node> list) {
		saveData(loadIndex(), list);
	}
	
	public static void saveData(int index, List<Node> list) {
		sData.set(checkIndex(index), checkData(list));
	}
	
	public static List<Node> loadData() {
		return loadData(loadIndex());
	}
	
	public static List<Node> loadData(int index) {
		List<Node> list = sData.get(checkIndex(index));
		return checkData(list);
	}
	
	private static List<Node> checkData(List<Node> list) {
		Set<Integer> posSet = new HashSet<Integer>();
		Set<String> heroSet = new HashSet<String>();
		
		for (int i = list.size() - 1; i >= 0; i--) {
			Node node = list.get(i);
			
			if (node.position < 1 || node.position > GRID_NUM
					|| posSet.contains(node.position)) {
				
				list.remove(i);
				continue;
			}
			
			if (node.hero == null || heroSet.contains(node.hero.id)) {
				list.remove(i);
				continue;
			}
			
			posSet.add(node.position);
			heroSet.add(node.hero.id);
		}
		
		if (list.size() < MIN_HERO_NUM) {
			List<Hero> hs = ScriptMgr.getInstance().getHeroes();
			
			for (int i = 0; list.size() < MIN_HERO_NUM; i++) {
				Hero hero = hs.get(i);
				
				if (heroSet.contains(hero.id) == false) {
					for (int j = 1; j <= GRID_NUM; j++) {
						if (posSet.contains(j) == false) {
							Node node = new Node();
							node.position = j;
							node.hero = hero;
							
							list.add(node);
							posSet.add(node.position);
							heroSet.add(hero.id);
							break;
						}
					}
				
					break;
				}
			}
		} else if (list.size() > MAX_HERO_NUM) {
			do {
				list.remove(list.size() - 1);
			} while (list.size() > MAX_HERO_NUM);
		}
		
		Collections.sort(list, CMP);
		
		return list;
	}
	
	public static void onPushData(String str) throws Exception {
		SERVER_DATA = str;
		
		JSONArray jas = new JSONArray(str);
		
		for (int i = 0; i < DATA_NUM; i++) {
			JSONArray ja;
			if (i < jas.length()) {
				ja = jas.getJSONArray(i);
			} else {
				ja = new JSONArray();
			}
			
			List<Node> list = new ArrayList<Node>();
			
			for (int j = 0; j < ja.length(); j++) {
				try {
					JSONObject jo = ja.getJSONObject(j);
					Node node = new Node();
					
					node.position = jo.getInt(KEY_POSITION);
					
					String id = jo.getString(KEY_HERO);
					node.hero = ScriptMgr.getInstance().getHero(id);
					
					list.add(node);
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}
			
			saveData(i, list);
		}
	}
	
	private static String data2String() {
		JSONArray jas = new JSONArray();
		
		try {
			for (int i = 0; i < DATA_NUM; i++) {
				List<Node> list = sData.get(i);
				JSONArray ja = new JSONArray();
				
				for (Node node : list) {
					JSONObject jo = new JSONObject();
					jo.put(KEY_POSITION, node.position);
					jo.put(KEY_HERO, node.hero.id);
					
					ja.put(ja.length(), jo);
				}
				
				jas.put(i, ja);
			}
			
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}

		return jas.toString();
	}
	
	public static void tryUploadData() {
		String data = data2String();
		if (data.equals(SERVER_DATA)) {
			return;
		}
		
		UploadLineup ul = new UploadLineup();
		ul.data = data;
		ul.send();
		
		SERVER_DATA = data;
	}
}
