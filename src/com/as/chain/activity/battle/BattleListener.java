package com.as.chain.activity.battle;

import java.util.concurrent.atomic.AtomicInteger;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.as.chain.R;
import com.as.chain.game.Define;
import com.as.chain.game.ScriptMgr;
import com.as.chain.game.Define.ChainType;
import com.as.chain.ui.BattleHero;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadUtil;

import android.view.View;

public class BattleListener extends ThreeArgFunction {
	public static final String TAG = BattleListener.class.getSimpleName();
	
	protected final BattleActivity mActivity;
	protected LuaValue mBattle;
	
	protected GridHolder mChainHolder;
	
	public BattleListener(BattleActivity activity) {
		mActivity = activity;
	}
	
	@Override
	public LuaValue call(LuaValue momentType,
			LuaValue momentPhase, final LuaValue data) {
		
		final String moment = momentType.tojstring();
		final int phase = momentPhase.toint();
		
		Logger.getInstance().print(TAG, Level.D, moment, phase);
		Logger.getInstance().print(TAG, Level.D, ScriptMgr.toString(data, 2));
		
		ThreadUtil.getMain().run(new Runnable() {
			@Override
			public void run() {
				if (phase == Define.PHASE_BEFORE &&
						Define.MOMENT_GAME_START.equals(moment)) {
					
					final AtomicInteger skillIdx = new AtomicInteger(0);
					
					new Traver(mActivity) {
						@Override
						public void onGrid(LuaValue sg, GridHolder bg,
								int sideIdx, int groupIdx, int gridIdx) {
							
							LuaValue sh = sg.get("hero");
							if (sh.isnil()) {
								return;
							}
							
							BattleHero bh = new BattleHero(mActivity);
							bg.hero = bh;
							mActivity.mField.addView(bh);
							
							bh.init(bg.view.getWidth(), bg.view.getHeight());
							bh.setHealth(sh.get("cur_health").toint(),
								sh.get("health").toint());
							bh.setShield(sh.get("cur_shield").toint(),
								sh.get("shield").toint());
							bh.setName(sh.get("name").tojstring());
							bh.setStatus(BattleHero.Status.Normal);
							bh.setVisibility(View.INVISIBLE);
							
							if (sideIdx == mActivity.mMySideIdx &&
									groupIdx == mActivity.mMyGroupIdx) {
								
								SkillHolder bs = mActivity.mSkills[skillIdx.getAndIncrement()];
								bs.skill = sh.get("skills").get(1);
								bs.hero = sh;
								bs.gridIdx = gridIdx;
								bs.name.setText(sh.get("name").tojstring());
							}
						}
					}.start();
					
					ThreadUtil.getMain().run(new Runnable() {
						@Override
						public void run() {
						new Traver(mActivity) {
							@Override
							public void onGrid(LuaValue sg, GridHolder bg,
									int sideIdx, int groupIdx, int gridIdx) {
								
								if (bg.hero != null) {
									bg.hero.setX(bg.x);
									bg.hero.setY(bg.y);
									bg.hero.setVisibility(View.VISIBLE);
								}
							}
						}.start();
					}});
					
					mActivity.resumeBattle(1500);
					return;
				}
				
				if (phase != Define.PHASE_AFTER) {
					mActivity.resumeBattle(0);
					return;
				}
				
				if (Define.MOMENT_GAME_START.equals(moment)) {
					mActivity.display(R.string.msg_battle_start, 1000);
					mActivity.resumeBattle(1500);
				} else if(Define.MOMENT_ROUND_START.equals(moment)) {
					mActivity.display(mActivity.getString(R.string.msg_round_index,
						mBattle.get("round_index").toint()), 1000);
					
					mActivity.mRound.setText(mActivity.getString(
						R.string.msg_round_index_max,
						mBattle.get("round_index").toint(),
						mBattle.get("config").get("round_num_max").toint()));
					
					mActivity.updateBase();
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_ROUND_END.equals(moment)) {
					if (mChainHolder != null) {
						mChainHolder.moveTo(mChainHolder.x, mChainHolder.y, 0, 300, null);
						mChainHolder = null;
					}
					
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_ATTACK.equals(moment)) {
					final LuaValue srcGrid = data.get("src_hero").get("grid");
					final GridHolder srcHolder = mActivity.mGroups
						[srcGrid.get("side_index").toint() - 1]
						[srcGrid.get("group_index").toint() - 1].grids
						[srcGrid.get("grid_index").toint() - 1];
					
					final LuaValue dstNode = data.get("dst_nodes").get(1);
					final LuaValue dstGrid = dstNode.get("hero").get("grid");
					final GridHolder dstHolder = mActivity.mGroups
						[dstGrid.get("side_index").toint() - 1]
						[dstGrid.get("group_index").toint() - 1].grids
						[dstGrid.get("grid_index").toint() - 1];
					
					int dstX;
					int dstY = dstHolder.y;
					if (srcGrid.get("side_index").toint() == 1) {
						dstX = dstHolder.x - dstHolder.width * 3 / 4;
					} else {
						dstX = dstHolder.x + dstHolder.width * 3 / 4;
					}
					
					srcHolder.moveTo(dstX, dstY, 0, 800, new Runnable() {
						@Override
						public void run() {
							srcHolder.moveTo(srcHolder.x, srcHolder.y, 500, 600, null);
						}
					});
					
					if (mChainHolder != null) {
						if (mChainHolder != srcHolder && mChainHolder != dstHolder) {
							mChainHolder.moveTo(mChainHolder.x, mChainHolder.y, 0, 300, null);
						}
						mChainHolder = null;
					}
					
					dstX = dstHolder.x;
					dstY = dstHolder.y;
					
					if (data.get("dst_chain").isnil() == false) {
						int dstChain = data.get("dst_chain").toint();
						
						if (ChainType.Fall.ordinal() == dstChain) {
							dstY += dstHolder.height * 1 / 3;
						} else if (ChainType.Back.ordinal() == dstChain) {
							if (dstGrid.get("side_index").toint() == 1) {
								dstX -= dstHolder.width * 2 / 5;
							} else {
								dstX += dstHolder.width * 2 / 5;
							}
						} else if (ChainType.Float.ordinal() == dstChain) {
							dstY -= dstHolder.height * 1 / 3;
 						} else if (ChainType.Fly.ordinal() == dstChain) {
							dstY -= dstHolder.height * 2 / 3;
 						}
						
						mChainHolder = dstHolder;
					}
						
					dstHolder.moveTo(dstX, dstY, 800, 500, null);
					
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_DAMAGE.equals(moment)) {
					LuaValue dstNodes = data.get("dst_nodes");
					
					for (int i = 1; i <= dstNodes.length(); i++) {
						LuaValue dstNode = dstNodes.get(i);
						LuaValue dstGrid = dstNode.get("hero").get("grid");
						GridHolder dstHolder = mActivity.mGroups
							[dstGrid.get("side_index").toint() - 1]
							[dstGrid.get("group_index").toint() - 1].grids
							[dstGrid.get("grid_index").toint() - 1];
						
						int damage = dstNode.get("damage").toint();
						dstHolder.hero.display("-" + damage, 0xffff0000);
					}
					
					mActivity.updateBase();
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_MAGIC_CHANGE.equals(moment)) {
					mActivity.updateBase();
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_DEATH.equals(moment)) {
					LuaValue dstNodes = data.get("dst_nodes");
					
					for (int i = 1; i <= dstNodes.length(); i++) {
						LuaValue dstNode = dstNodes.get(i);
						LuaValue dstGrid = dstNode.get("hero").get("grid");
						GridHolder dstHolder = mActivity.mGroups
							[dstGrid.get("side_index").toint() - 1]
							[dstGrid.get("group_index").toint() - 1].grids
							[dstGrid.get("grid_index").toint() - 1];
						
						dstHolder.hero.setStatus(BattleHero.Status.Dead);
					}
					
					mActivity.resumeBattle(500);
				} else if (Define.MOMENT_GAME_END.equals(moment)) {
					int result = data.get("result").toint();
					int text;
					
					if (result > 0) {
						text = R.string.wd_win;
					} else if (result < 0) {
						text = R.string.wd_lose;
					} else {
						text = R.string.wd_draw;
					}
					
					mActivity.display(text, 5000);
				} else {
					mActivity.resumeBattle(0);
				}
			}
		});
		
		mActivity.pauseBattle();
		
		return null;
	}
}
