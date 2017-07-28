package com.as.chain.activity;

import java.util.List;
import java.util.concurrent.Semaphore;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.as.app.Res;
import com.as.chain.R;
import com.as.chain.game.Lineup;
import com.as.chain.game.ScriptMgr;
import com.as.chain.game.Lineup.Node;
import com.as.chain.ui.ProgressBar;
import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadUtil;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BattleActivity extends BaseActivity {
	public static final String TAG = BattleActivity.class.getSimpleName();
	
	static class GroupHolder {
		public ProgressBar health;
		public final View[] magics = new View[10];
		public final View[] grids = new View[9];
	}
	
	private final GroupHolder[][] mGroups = new GroupHolder[2][1];
	
	private final TextView[] mSkills = new TextView[4];
	
	private TextView mRound;
	
	private LuaValue mBattle;
	private final Semaphore mSema = new Semaphore(0);
	
	private ThreeArgFunction mListener = new ThreeArgFunction() {
		@Override
		public LuaValue call(LuaValue memontType, LuaValue memontPhase, LuaValue data) {
			Logger.getInstance().print(TAG, Level.D, memontType, memontPhase);
			
			Logger.getInstance().print(TAG, Level.D, ScriptMgr.toString(data, 3));
			
			try {
				mSema.acquire();
			} catch (Exception e) {
				Logger.getInstance().print(TAG, Level.E, e);
			}
			
			return null;
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle);
		
		initUi();
		
		initBattle();
	}
	
	private void initUi() {
		View parent;
		int id;
		
		for (int side_idx = 1; side_idx <= mGroups.length; side_idx++) {
			for (int group_idx = 1; group_idx <= mGroups[side_idx - 1].length; group_idx++) {
				Logger.getInstance().print(TAG, Level.D, "side", side_idx, "group", group_idx);
				
				GroupHolder holder = new GroupHolder();
				mGroups[side_idx - 1][group_idx - 1] = holder;
				
				id = Res.getIdId(String.format("pb_health_%d_%d", side_idx, group_idx));
				holder.health = (ProgressBar) findViewById(id);
				
				id = Res.getIdId(String.format("ml_magic_%d_%d", side_idx, group_idx));
				parent = findViewById(id);
				for (int i = 1; i <= holder.magics.length; i++) {
					id = Res.getIdId("view_node_" + i);
					holder.magics[i - 1] = parent.findViewById(id);
					holder.magics[i - 1].setVisibility(View.INVISIBLE);
				}
				
				id = Res.getIdId(String.format("ml_grids_%d_%d", side_idx, group_idx));
				parent = findViewById(id);
				for (int i = 1; i <= holder.grids.length; i++) {
					id = Res.getIdId("view_grid_" + i);
					holder.grids[i - 1] = parent.findViewById(id);
				}
			}
		}
		
		parent = findViewById(R.id.ll_skills);
		for (int i = 1; i <= mSkills.length; i++) {
			id = Res.getIdId("tv_skill_" + i);
			mSkills[i - 1] = (TextView) parent.findViewById(id);
		}
		
		mRound = (TextView) findViewById(R.id.tv_round);
		
		findViewById(R.id.tv_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mSema.release();
			}
		});
	}
	
	private void initBattle() {
		LuaValue params = LuaValue.tableOf();
		params.set("random_seed", 0);
		params.set("is_home", LuaValue.TRUE);
		params.set("listener", mListener);
		
		LuaValue side = LuaValue.tableOf();
		params.set("left_side", side);
		LuaValue group = LuaValue.tableOf();
		side.set(1, group);
		List<Node> lp = Lineup.loadData(1);
		for (Node node : lp) {
			group.set(node.position, node.hero.id);
		}
		
		side = LuaValue.tableOf();
		params.set("right_side", side);
		group = LuaValue.tableOf();
		side.set(1, group);
		lp = Lineup.loadData(2);
		for (Node node : lp) {
			group.set(node.position, node.hero.id);
		}
		
		Globals globals = ScriptMgr.getInstance().getGlobals();
		LuaValue battle = globals.loadfile(DataMgr.UPDATE_PATH + "src/battle/battle.lua").call();
		mBattle = battle.get("new").call(params);
		
		ThreadUtil.getVice().run(new Runnable() {
			@Override
			public void run() {
				mBattle.get("start").call(mBattle);
			}
		});
	}
}
