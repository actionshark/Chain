package com.as.chain.activity.battle;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ZeroArgFunction;

import com.as.app.Res;
import com.as.chain.R;
import com.as.chain.activity.BaseActivity;
import com.as.chain.game.Define.HeroType;
import com.as.chain.game.Lineup;
import com.as.chain.game.ScriptMgr;
import com.as.chain.game.Lineup.Node;
import com.as.chain.ui.BattleHero;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.ProgressBar;
import com.as.chain.ui.SimpleDialog;
import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class BattleActivity extends BaseActivity {
	public static final String TAG = BattleActivity.class.getSimpleName();
	
	protected final GroupHolder[][] mGroups = new GroupHolder[2][1];
	protected ViewGroup mField;
	
	protected final SkillHolder[] mSkills = new SkillHolder[4];
	
	protected TextView mRound;
	
	protected LuaValue mBattle;
	private final Semaphore mSema = new Semaphore(0);
	protected BattleListener mBattleListener;
	
	protected int mMySideIdx = 1;
	protected int mMyGroupIdx = 1;
	
	protected final Queue<Runnable> mEventQueue = new LinkedList<Runnable>();
	
	protected TextView mTvDisplay;
	protected Object mDisplayMark;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_battle);
	}
	
	@Override
	protected void onAnimationFinish(int count) {
		if (count == 1) {
			initUi();
			initBattle();
		}
	}
	
	private void initUi() {
		View parent;
		int id;
		
		mField = (ViewGroup) findViewById(R.id.al_field);
		int[] fieldPos = new int[2];
		mField.getLocationInWindow(fieldPos);
		
		for (int side_idx = 1; side_idx <= mGroups.length; side_idx++) {
			for (int group_idx = 1; group_idx <= mGroups[side_idx - 1].length; group_idx++) {
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
					GridHolder grid = new GridHolder();
					holder.grids[i - 1] = grid;
					
					id = Res.getIdId("view_grid_" + i);
					grid.view = parent.findViewById(id);
					
					int[] pos = new int[2];
					grid.view.getLocationInWindow(pos);
					grid.width = grid.view.getWidth();
					grid.height = grid.view.getHeight();
					grid.x = pos[0] - fieldPos[0] + grid.width / 2;
					grid.y = pos[1] - fieldPos[1] + grid.height / 2;
				}
			}
		}
		
		parent = findViewById(R.id.ll_skills);
		for (int i = 1; i <= mSkills.length; i++) {
			final SkillHolder sh = new SkillHolder();
			mSkills[i - 1] = sh;
			
			id = Res.getIdId("fl_skill_" + i);
			View temp = parent.findViewById(id);
			sh.name = (TextView) temp.findViewById(R.id.tv_name);
			sh.cd = (TextView) temp.findViewById(R.id.tv_cd);
			
			temp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					postEvent(new Runnable() {
						public void run() {
							LuaTable src = LuaTable.tableOf();
							src.set("side_index", mMySideIdx);
							src.set("group_index", mMyGroupIdx);
							src.set("grid_index", sh.gridIdx);

							LuaTable dst = LuaTable.tableOf();
							dst.set("side_index", 2);
							dst.set("group_index", mMyGroupIdx);
							dst.set("grid_index", sh.gridIdx);
							
							mBattle.get("castActive").call(mBattle, src, dst);
						}
					});
				}
			});
		}
		
		mRound = (TextView) findViewById(R.id.tv_round);
		
		findViewById(R.id.tv_btn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				resumeBattle(0);
			}
		});
		
		mTvDisplay = (TextView) findViewById(R.id.tv_display);
	}
	
	private void initBattle() {
		LuaValue params = LuaValue.tableOf();
		params.set("random_seed", 0);
		params.set("is_home", LuaValue.TRUE);
		
		LuaTable listener = LuaValue.tableOf();
		params.set("listener", listener);
		
		mBattleListener = new BattleListener(this);
		listener.set("onEvent", mBattleListener);
		
		listener.set("onGap", new ZeroArgFunction() {
			@Override
			public LuaValue call() {
				Queue<Runnable> queue;
				synchronized (mEventQueue) {
					queue = new LinkedList<Runnable>(mEventQueue);
					mEventQueue.clear();
				}
				
				for (Runnable runnable : queue) {
					runnable.run();
				}
				
				return null;
			}
		});
		
		LuaValue side = LuaValue.tableOf();
		params.set("left_side", side);
		LuaValue group = LuaValue.tableOf();
		side.set(1, group);
		for (int i = 1; i <= Lineup.GRID_NUM; i++) {
			group.set(i, "");
		}
		for (Node node : Lineup.loadData(1)) {
			group.set(node.position, node.hero.id);
		}
		
		side = LuaValue.tableOf();
		params.set("right_side", side);
		group = LuaValue.tableOf();
		side.set(1, group);
		for (int i = 1; i <= Lineup.GRID_NUM; i++) {
			group.set(i, "");
		}
		for (Node node : Lineup.loadData(2)) {
			group.set(node.position, node.hero.id);
		}
		
		Globals globals = ScriptMgr.getInstance().getGlobals();
		LuaValue battle = globals.loadfile(DataMgr.UPDATE_PATH + "src/battle/battle.lua").call();
		mBattle = battle.get("new").call(params);
		
		mBattleListener.mBattle = mBattle;
		
		ThreadUtil.getVice().run(new Runnable() {
			@Override
			public void run() {
				mBattle.get("start").call(mBattle);
			}
		});
	}
	
	protected void pauseBattle() {
		try {
			mSema.acquire();
		} catch (Exception e) {
			Logger.getInstance().print(TAG, Level.E, e);
		}
	}
	
	protected void resumeBattle(long delay) {
		if (delay > 0) {
			ThreadUtil.getVice().run(new Runnable() {
				@Override
				public void run() {
					mSema.release();
				}
			}, delay);
		} else {
			mSema.release();
		}
	}
	
	protected void postEvent(Runnable runnable) {
		synchronized (mEventQueue) {
			mEventQueue.offer(runnable);
		}
	}
	
	protected void updateBase() {
		LuaValue sides = mBattle.get("side");
		
		for (int sideIdx = 1; sideIdx <= sides.length(); sideIdx++) {
			LuaValue side = sides.get(sideIdx);
			
			for (int groupIdx = 1; groupIdx <= side.length(); groupIdx++) {
				LuaValue group = side.get(groupIdx);
				GroupHolder groupHolder = mGroups[sideIdx - 1][groupIdx - 1];
				int curHealth = 0;
				int health = 0;
				
				for (int gridIdx = 1; gridIdx <= group.length(); gridIdx++) {
					LuaValue grid = group.get(gridIdx);
					LuaValue hero = grid.get("hero");
					GridHolder gridHolder = groupHolder.grids[gridIdx - 1];
					BattleHero heroBattle = gridHolder.hero;
					
					if (hero.isnil()) {
						if (heroBattle != null) {
							mField.removeView(heroBattle);
							gridHolder.hero = null;
						}
					} else {
						if (hero.get("is_dead").toboolean()) {
							heroBattle.setStatus(BattleHero.Status.Dead);
						} else if (hero.get("is_waitting").toboolean()) {
							heroBattle.setStatus(BattleHero.Status.Active);
						} else {
							heroBattle.setStatus(BattleHero.Status.Normal);
						}
						
						int chl = hero.get("cur_health").toint();
						int hl = hero.get("health").toint();
						heroBattle.setHealth(chl, hl);
						if (hero.get("type").toint() == HeroType.Normal.ordinal()) {
							curHealth += chl;
							health += hl;
						}
						
						heroBattle.setShield(hero.get("cur_shield").toint(),
								hero.get("shield").toint());
					}
				}
				
				if (health == 0) {
					groupHolder.health.setProgress(0);
				} else {
					groupHolder.health.setProgress(curHealth / (float) health);
				}
				
				int magicValue = group.get("magic_value").toint();
				for (int i = 0; i < groupHolder.magics.length; i++) {
					groupHolder.magics[i].setVisibility(i < magicValue ?
						View.VISIBLE : View.INVISIBLE);
				}
			}
		}
		
		for (int i = 0; i < mSkills.length; i++) {
			SkillHolder holder = mSkills[i];
			LuaValue hero = holder.hero;
			
			if (hero == null) {
				continue;
			}
			
			holder.name.setText(String.format("%s %d", hero.get("name").tojstring(),
					holder.skill.get("magic_cost").toint()));
			if (mBattle.get("checkActive").call(mBattle, hero.get("grid")).toboolean()) {
				holder.name.setTextColor(0xff000000);
			} else {
				holder.name.setTextColor(0xffffffff);
			}
			
			int cd = holder.skill.get("cd_count").toint();
			if (cd > 0) {
				holder.cd.setText(String.valueOf(cd));
				holder.cd.setVisibility(View.VISIBLE);
			} else {
				holder.cd.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	protected void display(Object text, long duration) {
		if (text instanceof Integer) {
			mTvDisplay.setText((Integer) text);
		} else {
			mTvDisplay.setText(String.valueOf(text));
		}
		mTvDisplay.setVisibility(View.VISIBLE);
		
		final Object mark = new Object();
		mDisplayMark = mark;
		ThreadUtil.getMain().run(new Runnable() {
			public void run() {
				if (mDisplayMark == mark) {
					mTvDisplay.setVisibility(View.INVISIBLE);
				}
			}
		}, duration);
	}
	
	@Override
	protected void onDestroy() {
		postEvent(new Runnable() {
			public void run() {
				mBattle.get("stop").call(mBattle);
			}
		});
		
		super.onDestroy();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SimpleDialog sd = new SimpleDialog(this);
			sd.setMessage(R.string.msg_exit_battle_confirm);
			sd.setButtons(R.string.wd_cancel, R.string.wd_confirm);
			sd.setClickListener(new IDialogClickListener() {
				@Override
				public void onClick(Dialog dialog, int index, ClickType type) {
					if (index == 1) {
						finish();
					}
					
					dialog.dismiss();
				}
			});
			sd.show();
			
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
