package com.as.chain.activity;

import java.util.List;

import org.luaj.vm2.LuaValue;

import com.as.app.Setting;
import com.as.chain.R;
import com.as.chain.adapter.HeroesAdapter;
import com.as.chain.adapter.HeroesAdapter.IOnClickListener;
import com.as.chain.game.Hero;
import com.as.chain.game.ScriptMgr;
import com.as.chain.util.Const;

import android.os.Bundle;
import android.os.IInterface;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.SeekBar;
import android.widget.TextView;

public class HeroActivity extends BaseActivity {
	public static final String TAG = HeroActivity.class.getSimpleName();
	
	static class ValueHolder {
		public TextView value;
		public SeekBar line;
	}
	
	private TextView mTvHeroName;
	private TextView mTvCountry;
	private TextView mTvSex;
	private TextView mTvHeroDesc;
	
	private ValueHolder mVhHealth = new ValueHolder();
	private ValueHolder mVhAttack = new ValueHolder();
	private ValueHolder mVhDefence = new ValueHolder();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hero);
		
		GridView lvHeroes = (GridView) findViewById(R.id.gv_heroes);
		HeroesAdapter adapter = new HeroesAdapter(this);
		adapter.setOnClickListener(new IOnClickListener() {
			@Override
			public void onClick(Hero hero) {
				updateHeroInfo(hero);
			}
		});
		lvHeroes.setAdapter(adapter);
		List<Hero> heroes = ScriptMgr.getInstance().getHeroes();
		adapter.setDataList(heroes);
		adapter.notifyDataSetChanged();
		
		View detail = findViewById(R.id.ml_detail);
		
		mTvHeroName = (TextView) detail.findViewById(R.id.tv_name);
		mTvCountry = (TextView) detail.findViewById(R.id.tv_country);
		mTvSex = (TextView) detail.findViewById(R.id.tv_sex);
		mTvHeroDesc = (TextView) detail.findViewById(R.id.tv_desc);
		
		View parent = detail.findViewById(R.id.ll_health);
		mVhHealth.value = (TextView) parent.findViewById(R.id.tv_value);
		mVhHealth.line = (SeekBar) parent.findViewById(R.id.sb_line);
		mVhHealth.line.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		parent = detail.findViewById(R.id.ll_attack);
		mVhAttack.value = (TextView) parent.findViewById(R.id.tv_value);
		mVhAttack.line = (SeekBar) parent.findViewById(R.id.sb_line);
		mVhAttack.line.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		parent = detail.findViewById(R.id.ll_defence);
		mVhDefence.value = (TextView) parent.findViewById(R.id.tv_value);
		mVhDefence.line = (SeekBar) parent.findViewById(R.id.sb_line);
		mVhDefence.line.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		String lastHero = Setting.getInstance().getString(Const.SHOW_DETAIL_HERO);
		if (lastHero == null) {
			lastHero = Hero.DEF_HERO;
		}
		Hero hero = ScriptMgr.getInstance().getHero(lastHero);
		if (hero == null) {
			ScriptMgr.getInstance().getHero(Hero.DEF_HERO);
		}
		updateHeroInfo(hero);
	}
	
	private void updateHeroInfo(Hero hero) {
		Setting.getInstance().setString(Const.SHOW_DETAIL_HERO, hero.id);
		
		mTvHeroName.setText(hero.name);
		
		LuaValue countryType = ScriptMgr.getInstance().getGlobal("CountryType").get(hero.country);
		mTvCountry.setText(countryType.get("text").tojstring());
		
		LuaValue sexType = ScriptMgr.getInstance().getGlobal("SexType").get(hero.sex);
		mTvSex.setText(sexType.get("text").tojstring());
		
		mTvHeroDesc.setText("此处是预留空间");
		
		mVhHealth.value.setText(String.valueOf(hero.health));
		mVhHealth.line.setProgress(hero.health * 100 / Hero.getHealthMax());
		
		mVhAttack.value.setText(String.valueOf(hero.attack));
		mVhAttack.line.setProgress(hero.attack * 100 / Hero.getAttackMax());
		
		mVhDefence.value.setText(String.valueOf(hero.defence));
		mVhDefence.line.setProgress(hero.defence * 100 / Hero.getDefenceMax());
	}
}
