package com.as.chain.activity;

import java.util.List;

import com.as.chain.R;
import com.as.chain.adapter.HeroesAdapter;
import com.as.chain.game.Hero;
import com.as.chain.game.ScriptMgr;

import android.os.Bundle;
import android.widget.GridView;

public class HeroActivity extends BaseActivity {
	public static final String TAG = HeroActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hero);
		
		GridView lvHeroes = (GridView) findViewById(R.id.gv_heroes);
		HeroesAdapter adapter = new HeroesAdapter(this);
		lvHeroes.setAdapter(adapter);
		List<Hero> heroes = ScriptMgr.getInstance().getHeroes();
		adapter.setDataList(heroes);
		adapter.notifyDataSetChanged();
	}
}
