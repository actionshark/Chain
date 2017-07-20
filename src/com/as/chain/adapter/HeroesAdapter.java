package com.as.chain.adapter;

import java.util.List;

import org.luaj.vm2.LuaValue;

import com.as.app.BasicAdapter;
import com.as.chain.R;
import com.as.chain.game.Hero;
import com.as.chain.game.ScriptMgr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HeroesAdapter extends BasicAdapter {
	private final Context mContext;
	private List<Hero> mDataList;
	
	public HeroesAdapter(Context context) {
		mContext = context;
	}
	
	public void setDataList(List<Hero> dataList) {
		mDataList = dataList;
	}
	
	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@SuppressLint("InflateParams")
	@Override
	protected View createView(int position) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.grid_hero, null);
		ViewHolder vh = new ViewHolder();
		view.setTag(vh);
		
		vh.name = (TextView) view.findViewById(R.id.tv_name);
		
		return view;
	}

	@Override
	protected void updateView(int position, View view) {
		ViewHolder vh = (ViewHolder) view.getTag();
		Hero hero = mDataList.get(position);
		
		vh.name.setText(hero.name);
		
		LuaValue countryType = ScriptMgr.getInstance().getCountryType(hero.country);
		vh.name.setTextColor(countryType.get("color").toint());
	}
	
	private static class ViewHolder {
		public TextView name;
	}
}
