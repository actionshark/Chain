package com.as.chain.activity.battle;

import org.luaj.vm2.LuaValue;

public abstract class Traver {
	private final BattleActivity mActivity;
	
	public Traver(BattleActivity activity) {
		mActivity = activity;
	}
	
	public void start() {
		LuaValue sides = mActivity.mBattle.get("side");
		
		for (int sideIdx = 1; sideIdx <= sides.length(); sideIdx++) {
			LuaValue side = sides.get(sideIdx);
			onSide(side, sideIdx);
			
			for (int groupIdx = 1; groupIdx <= side.length(); groupIdx++) {
				LuaValue group = side.get(groupIdx);
				GroupHolder gh = mActivity.mGroups[sideIdx - 1][groupIdx - 1];
				
				onGroup(group, gh, sideIdx, groupIdx);
				
				for (int gridIdx = 1; gridIdx <= group.length(); gridIdx ++) {
					LuaValue grid = group.get(gridIdx);
					onGrid(grid, gh.grids[gridIdx - 1],
						sideIdx, groupIdx, gridIdx);
				}
			}
		}
	}
	
	public void onSide(LuaValue side, int sideIdx) {
	}
	
	public void onGroup(LuaValue group, GroupHolder holder,
			int sideIdx, int groupIdx) {
	}
	
	public void onGrid(LuaValue grid, GridHolder holder,
			int sideIdx, int groupIdx, int gridIdx) {
	}
}
