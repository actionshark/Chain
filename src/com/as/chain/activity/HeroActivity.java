package com.as.chain.activity;

import java.util.ArrayList;
import java.util.List;

import com.as.app.Res;
import com.as.app.Setting;
import com.as.chain.R;
import com.as.chain.adapter.HeroesAdapter;
import com.as.chain.adapter.HeroesAdapter.IOnClickListener;
import com.as.chain.game.Hero;
import com.as.chain.game.Lineup;
import com.as.chain.game.Lineup.Node;
import com.as.chain.game.ScriptMgr;
import com.as.chain.game.Skill;
import com.as.chain.ui.ProgressBar;
import com.as.chain.game.Define.Country;
import com.as.chain.game.Define.Sex;
import com.as.chain.game.Define.SkillType;
import com.as.chain.util.Const;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class HeroActivity extends BaseActivity {
	public static final String TAG = HeroActivity.class.getSimpleName();
	
	private TextView mTvHeroName;
	private TextView mTvCountry;
	private TextView mTvSex;
	private TextView mTvHeroDesc;
	
	private View mViewHealth;
	private View mViewAttack;
	
	private final View[] mViewSkills = new View[4];
	
	private HeroesAdapter mHeroesAdapter;
	
	private View mViewDragHero;
	private TextView mTvDragHero;
	private Hero mDragHero;
	
	private final TextView[] mTvLineup = new TextView[Lineup.GRID_NUM];
	private final View[] mViewLineup = new View[Lineup.DATA_NUM];
	
	@SuppressLint("ClickableViewAccessibility")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hero);
		
		GridView lvHeroes = (GridView) findViewById(R.id.gv_heroes);
		mHeroesAdapter = new HeroesAdapter(this);
		mHeroesAdapter.setOnClickListener(new IOnClickListener() {
			@Override
			public void onTouch(Hero hero, MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
					if (mHeroesAdapter.isSelected(hero) == false) {
						mDragHero = hero;
					}
				}
			}
			
			@Override
			public void onClick(Hero hero) {
				showHeroInfo(hero);
			}
		});
		lvHeroes.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				
				if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					onDragEnd(event);
				} else {
					onDragMove(event);
				}
				
				return false;
			}
		});
		lvHeroes.setAdapter(mHeroesAdapter);
		List<Hero> heroes = ScriptMgr.getInstance().getHeroes();
		mHeroesAdapter.setDataList(heroes);
		mHeroesAdapter.notifyDataSetChanged();
		
		View detail = findViewById(R.id.ml_detail);
		
		View parent = detail.findViewById(R.id.ml_info);
		mTvHeroName = (TextView) parent.findViewById(R.id.tv_name);
		mTvCountry = (TextView) parent.findViewById(R.id.tv_country);
		mTvSex = (TextView) parent.findViewById(R.id.tv_sex);
		mTvHeroDesc = (TextView) parent.findViewById(R.id.tv_desc);
		
		mViewHealth = detail.findViewById(R.id.ll_health);
		mViewAttack = detail.findViewById(R.id.ll_attack);
		
		for (int i = 0; i < mViewSkills.length; i++) {
			int resId = Res.getIdId("ml_skill_" + (i + 1));
			mViewSkills[i] = detail.findViewById(resId);
		}
		
		String lastHero = Setting.getInstance().getString(Const.KEY_LATEST_SHOW_HERO);
		if (lastHero == null) {
			lastHero = Hero.DEF_HERO;
		}
		Hero hero = ScriptMgr.getInstance().getHero(lastHero);
		if (hero == null) {
			ScriptMgr.getInstance().getHero(Hero.DEF_HERO);
		}
		showHeroInfo(hero);
		
		mViewDragHero = findViewById(R.id.al_drag);
		mTvDragHero = (TextView) mViewDragHero.findViewById(R.id.tv_drag);
		
		parent = findViewById(R.id.ml_lineup);
		parent.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getActionMasked();
				
				if (action == MotionEvent.ACTION_DOWN) {
					TextView grid = findLineupGrid(event.getRawX(), event.getRawY());
					if (grid != null) {
						mDragHero = (Hero) grid.getTag();
					}
				} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
					onDragEnd(event);
				} else {
					onDragMove(event);
				}
				
				return true;
			}
		});
		
		for (int i = 0; i < mTvLineup.length; i++) {
			int id = Res.getIdId("tv_grid_" + (i + 1));
			mTvLineup[i] = (TextView) parent.findViewById(id);
		}
		
		for (int i = 0; i < mViewLineup.length; i++) {
			int id = Res.getIdId("ml_lineup_" + (i + 1));
			mViewLineup[i] = findViewById(id);
			final int index = i + 1;
			mViewLineup[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					showLineup(index);
				}
			});
		}
		
		showLineup(Lineup.loadIndex());
	}
	
	private void showHeroInfo(Hero hero) {
		Setting.getInstance().setString(Const.KEY_LATEST_SHOW_HERO, hero.id);
		
		mTvHeroName.setText(hero.name);
		
		Country country = Country.values()[hero.country];
		mTvCountry.setText(country.text);
		
		Sex sex = Sex.values()[hero.sex];
		mTvSex.setText(sex.text);
		
		mTvHeroDesc.setText(hero.desc);
		
		((TextView) mViewHealth.findViewById(R.id.tv_value))
			.setText(String.valueOf(hero.health));
		((ProgressBar) mViewHealth.findViewById(R.id.pb_line))
			.setProgress(hero.health / Hero.getHealthMax());
		
		((TextView) mViewAttack.findViewById(R.id.tv_value))
			.setText(String.valueOf(hero.attack));
		((ProgressBar) mViewAttack.findViewById(R.id.pb_line))
			.setProgress(hero.attack / Hero.getAttackMax());
		
		for (int i = 0; i < mViewSkills.length; i++) {
			View view = mViewSkills[i];
			Skill skill = hero.skills.get(i);
			
			SkillType type = SkillType.values()[skill.type];
			((TextView) view.findViewById(R.id.tv_name))
				.setText(type.text);
			
			if (skill.magicCost != -1) {
				((TextView) view.findViewById(R.id.tv_cost))
					.setText(getString(R.string.msg_cost, skill.magicCost));
			}
			
			if (skill.firstCd != -1) {
				((TextView) view.findViewById(R.id.tv_first_cd))
					.setText(getString(R.string.msg_first_cd, skill.firstCd));
			}
			
			if (skill.repeatCd != -1) {
				((TextView) view.findViewById(R.id.tv_repeat_cd))
					.setText(getString(R.string.msg_repeat_cd, skill.repeatCd));
			}
			
			((TextView) view.findViewById(R.id.tv_desc)).setText(skill.desc);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void onDragMove(MotionEvent event) {
		if (mDragHero == null) {
			mViewDragHero.setVisibility(View.GONE);
			return;
		}
		
		mTvDragHero.setText(mDragHero.name);
		Country country = Country.values()[mDragHero.country];
		mTvDragHero.setTextColor(country.color);
		
		android.widget.AbsoluteLayout.LayoutParams lp =
			(android.widget.AbsoluteLayout.LayoutParams)
			mTvDragHero.getLayoutParams();
		int[] pos = new int[2];
		mViewDragHero.getLocationInWindow(pos);
		lp.x = (int) event.getRawX() - pos[0] - mTvDragHero.getWidth();
		lp.y = (int) event.getRawY() - pos[1] - mTvDragHero.getHeight();
		
		mViewDragHero.setVisibility(View.VISIBLE);
	}
	
	private void onDragEnd(MotionEvent event) {
		mViewDragHero.setVisibility(View.GONE);
		if (mDragHero == null) {
			return;
		}
		
		int num = 0;
		TextView oldGrid = null;
		for (int i = 0; i < mTvLineup.length; i++) {
			TextView grid = mTvLineup[i];
			
			Hero hero = (Hero) grid.getTag();
			if (hero != null) {
				num++;
				
				if (mDragHero.id.equals(hero.id)) {
					oldGrid = grid;
				}
			}
		}
		
		TextView newGrid = findLineupGrid(event.getRawX(), event.getRawY());
		
		if (oldGrid != null && newGrid == null) {
			if (num <= Lineup.MIN_HERO_NUM) {
				Toast.makeText(this, getString(R.string.err_lineup_hero_num_limit,
					Lineup.MIN_HERO_NUM, Lineup.MAX_HERO_NUM),  Toast.LENGTH_SHORT).show();
				return;
			}
			
			mHeroesAdapter.unselect(mDragHero);
			updateLineupGrid(null, oldGrid);
		} else if (oldGrid == null && newGrid != null) {
			if (num >= Lineup.MAX_HERO_NUM && newGrid.getTag() == null) {
				Toast.makeText(this, getString(
					R.string.err_lineup_hero_num_limit,
					Lineup.MIN_HERO_NUM, Lineup.MAX_HERO_NUM), 
					Toast.LENGTH_SHORT).show();
				return;
			}

			Hero newHero = (Hero) newGrid.getTag();
			if (newHero != null) {
				mHeroesAdapter.unselect(newHero);
			}

			mHeroesAdapter.select(mDragHero);
			updateLineupGrid(mDragHero, newGrid);
		} else if (oldGrid != null && newGrid != null) {
			Hero newHero = (Hero) newGrid.getTag();
			
			if (oldGrid == newGrid) {
				showHeroInfo(mDragHero);
			} else if (newHero == null) {
				updateLineupGrid(null, oldGrid);
				updateLineupGrid(mDragHero, newGrid);
			} else {
				updateLineupGrid(newHero, oldGrid);
				updateLineupGrid(mDragHero, newGrid);
			}
		}
			
		mDragHero = null;
		
		List<Node> nodes = new ArrayList<Node>();
		for (int i = 0; i < mTvLineup.length; i++) {
			Hero hero = (Hero) mTvLineup[i].getTag();
			if (hero != null) {
				Node node = new Node();
				node.position = i + 1;
				node.hero = hero;
				nodes.add(node);
			}
		}
		Lineup.saveData(nodes);
	}
	
	private TextView findLineupGrid(float rx, float ry) {
		for (int i = 0; i < mTvLineup.length; i++) {
			TextView grid = mTvLineup[i];
			int[] pos = new int[2];
			grid.getLocationInWindow(pos);
			float x = rx - pos[0];
			float y = ry - pos[1];
			
			if (x >= 0 && x <= grid.getWidth() &&
				 y >= 0 && y <= grid.getHeight()) {
				
				return grid;
			}
		}
		
		return null;
	}
	
	private void updateLineupGrid(Hero hero, TextView grid) {
		grid.setTag(hero);
		
		if (hero == null) {
			grid.setText("");
		} else {
			grid.setText(hero.name);
		}
	}
	
	private void showLineup(int index) {
		Lineup.saveIndex(index);
		List<Node> nodes = Lineup.loadData();
		
		mHeroesAdapter.unselect(null);
		for (TextView grid : mTvLineup) {
			updateLineupGrid(null, grid);
		}
		
		for (int i = 0; i < mViewLineup.length; i++) {
			TextView text = (TextView) mViewLineup[i]
				.findViewById(R.id.tv_text);
			
			if (i + 1 == index) {
				text.setTextColor(0xffff0000);
			} else {
				text.setTextColor(0xff000000);
			}
		}
		
		for (Node node : nodes) {
			updateLineupGrid(node.hero, mTvLineup[node.position - 1]);
			mHeroesAdapter.select(node.hero);
		}
	}
}
