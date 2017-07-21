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
import android.view.View.OnClickListener;
import android.widget.TextView;

public class HeroesAdapter extends BasicAdapter {
	public static interface IOnClickListener {
		public void onClick(Hero hero);
	}
	
	private final Context mContext;
	private List<Hero> mDataList;
	
	private IOnClickListener mListener;
	
	public HeroesAdapter(Context context) {
		mContext = context;
	}
	
	public void setDataList(List<Hero> dataList) {
		mDataList = dataList;
	}
	
	public void setOnClickListener(IOnClickListener listener) {
		mListener = listener;
	}
	
	@Override
	public int getCount() {
		return mDataList == null ? 0 : mDataList.size();
	}

	@SuppressLint("InflateParams")
	@Override
	protected View createView(int position) {
		View view = LayoutInflater.from(mContext).inflate(R.layout.grid_hero, null);
		final ViewHolder vh = new ViewHolder();
		view.setTag(vh);
		
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onClick(vh.hero);
				}
			}
		});
		
		vh.name = (TextView) view.findViewById(R.id.tv_name);
		
		return view;
	}

	@Override
	protected void updateView(int position, View view) {
		ViewHolder vh = (ViewHolder) view.getTag();
		Hero hero = mDataList.get(position);
		
		vh.hero = hero;
		
		vh.name.setText(hero.name);
		
		LuaValue countryType = ScriptMgr.getInstance().getGlobal("CountryType").get(hero.country);
		vh.name.setTextColor(countryType.get("color").toint());
	}
	
	private static class ViewHolder {
		public Hero hero;
		
		public TextView name;
	}
}
