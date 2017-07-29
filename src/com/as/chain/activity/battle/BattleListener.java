package com.as.chain.activity.battle;

import java.util.concurrent.atomic.AtomicInteger;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;

import com.as.chain.R;
import com.as.chain.game.Define;
import com.as.chain.game.ScriptMgr;
import com.as.chain.ui.BattleHero;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadUtil;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

public class BattleListener extends ThreeArgFunction {
	public static final String TAG = BattleListener.class.getSimpleName();
	
	protected final BattleActivity mActivity;
	
	protected LuaValue mBattle;
	
	public BattleListener(BattleActivity activity) {
		mActivity = activity;
	}
	
	@Override
	public LuaValue call(LuaValue momentType,
			LuaValue momentPhase, final LuaValue data) {
		
		final String moment = momentType.tojstring();
		final int phase = momentPhase.toint();
		
		Logger.getInstance().print(TAG, Level.D, moment, phase);
		Logger.getInstance().print(TAG, Level.D, ScriptMgr.toString(data, 3));
		
		ThreadUtil.getMain().run(new Runnable() {
			@Override
			public void run() {
				if (phase != Define.PHASE_AFTER) {
					mActivity.resumeBattle(1000);
					return;
				}
				
				if (Define.MOMENT_GAME_START.equals(moment)) {
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
							bh.setShield(sh.get("cur_sheild").toint(),
								sh.get("sheild").toint());
							bh.setName(sh.get("name").tojstring());
							bh.setStatus(BattleHero.Status.Normal);
							bh.setX(bg.x);
							bh.setY(bg.y);
							
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
					
					mActivity.resumeBattle(1000);
				} else if(Define.MOMENT_ROUND_START.equals(moment)) {
					mActivity.mRound.setText(mActivity.getString(
						R.string.msg_round_index,
						mBattle.get("round_index").toint(),
						mBattle.get("config").get("round_num_max").toint()));
					
					final AtomicInteger mv = new AtomicInteger(0);

					new Traver(mActivity) {
						@Override
						public void onGroup(LuaValue sg, GroupHolder bg,
								int sideIdx, int groupIdx) {
							
							int magicValue = sg.get("magic_value").toint();
							for (int i = 0; i < bg.magics.length; i++) {
								bg.magics[i].setVisibility(i < magicValue ?
									View.VISIBLE : View.INVISIBLE);
							}
							
							if (sideIdx == mActivity.mMySideIdx &&
									groupIdx == mActivity.mMyGroupIdx) {
								
								mv.set(magicValue);
							}
						}
					}.start();
					
					for (SkillHolder hs : mActivity.mSkills) {
						if (hs.skill == null) {
							hs.name.setText("");
							hs.cd.setVisibility(View.INVISIBLE);
						} else {
							int cd = hs.skill.get("cd_count").toint();
							
							boolean ok = hs.hero.get("is_dead").toboolean() == false
									&& cd <= 0
									&& hs.skill.get("magic_cost").toint() <= mv.get();
							
							hs.name.setTextColor(ok ? 0xff0000ff : 0xffffffff);
							
							if (cd > 0) {
								hs.cd.setText(String.valueOf(cd));
								hs.cd.setVisibility(View.VISIBLE);
							} else {
								hs.cd.setVisibility(View.INVISIBLE);
							}
						}
					}
					
					mActivity.resumeBattle(1000);
				} else if (Define.MOMENT_ATTACK.equals(moment)) {
					final LuaValue srcHero = data.get("src_hero");
					final LuaValue srcGrid = srcHero.get("grid");
					final GridHolder srcHolder = mActivity.mGroups
						[srcGrid.get("side_index").toint() - 1]
						[srcGrid.get("group_index").toint() - 1].grids
						[srcGrid.get("grid_index").toint() - 1];
					
					final LuaValue dstHeroes = data.get("dst_heroes");
					final LuaValue dstHero = dstHeroes.get(1);
					final LuaValue dstGrid = dstHero.get("grid");
					final GridHolder dstHolder = mActivity.mGroups
						[dstGrid.get("side_index").toint() - 1]
						[dstGrid.get("group_index").toint() - 1].grids
						[dstGrid.get("grid_index").toint() - 1];
					
					int dstX = dstHolder.x;
					int dstY = dstHolder.y;
					if (srcGrid.get("side_index").toint() == 1) {
						dstX -= dstHolder.width * 3 / 4;
					} else {
						dstX += dstHolder.width * 3 / 4;
					}
					
					Animation anim = new TranslateAnimation(
						srcHolder.x - srcHolder.hero.getX(),
						dstX - srcHolder.hero.getX(),
						srcHolder.y - srcHolder.hero.getY(),
						dstY - srcHolder.hero.getY());
					anim.setDuration(5000);
					anim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							Animation anim = new TranslateAnimation(
								0, srcHolder.x - srcHolder.hero.getX(),
								0, srcHolder.y - srcHolder.hero.getY());
							anim.setStartOffset(5000);
							anim.setDuration(5000);
							srcHolder.hero.clearAnimation();
							srcHolder.hero.startAnimation(anim);
							
							mActivity.resumeBattle(5000);
						}
					});
					
					srcHolder.hero.clearAnimation();
					srcHolder.hero.startAnimation(anim);
				} else if (Define.MOMENT_DAMAGE.equals(moment)) {
					LuaValue dstHeroes = data.get("dst_heroes");
					LuaValue damages = data.get("dst_damages");
					
					for (int i = 1; i <= dstHeroes.length(); i++) {
						LuaValue dstHero = dstHeroes.get(i);
						LuaValue dstGrid = dstHero.get("grid");
						GridHolder dstHolder = mActivity.mGroups
							[dstGrid.get("side_index").toint() - 1]
							[dstGrid.get("group_index").toint() - 1].grids
							[dstGrid.get("grid_index").toint() - 1];
						
						int damage = damages.get(i).toint();
						dstHolder.hero.display("-" + damage, 0xffff0000);
						
						dstHolder.hero.setHealth(dstHero.get("cur_health").toint(),
							dstHero.get("health").toint());
						dstHolder.hero.setShield(dstHero.get("cur_shield").toint(),
							dstHero.get("shield").toint());
					}
					
					mActivity.resumeBattle(900);
				} else {
					mActivity.resumeBattle(1000);
				}
			}
		});
		
		mActivity.pauseBattle();
		
		return null;
	}
}
