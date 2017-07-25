package com.as.chain.adapter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.as.app.BasicAdapter;
import com.as.chain.R;
import com.as.chain.game.Define.Country;
import com.as.chain.game.Hero;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.TextView;

public class HeroesAdapter extends BasicAdapter {
	public static interface IOnClickListener {
		public void onTouch(Hero hero, MotionEvent event);
		public void onClick(Hero hero);
	}
	
	private final Context mContext;
	private List<Hero> mDataList;
	private final Set<String> mSelects = new HashSet<String>();
	
	private IOnClickListener mListener;
	
	public HeroesAdapter(Context context) {
		mContext = context;
	}
	
	public void setDataList(List<Hero> dataList) {
		mDataList = dataList;
	}
	
	public boolean select(Hero hero) {
		if (mSelects.contains(hero.id)) {
			return false;
		} else {
			mSelects.add(hero.id);
			notifyDataSetChanged();
			return true;
		}
	}
	
	public boolean unselect(Hero hero) {
		if (hero == null) {
			if (mSelects.size() > 0) {
				mSelects.clear();
				notifyDataSetChanged();
				return true;
			} else {
				return false;
			}
		}
		
		if (mSelects.contains(hero.id)) {
			mSelects.remove(hero.id);
			notifyDataSetChanged();
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isSelected(Hero hero) {
		return mSelects.contains(hero.id);
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
		
		View touch = view.findViewById(R.id.vg_root);
		
		touch.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mListener != null) {
					mListener.onTouch(vh.hero, event);
				}
				
				return false;
			}
		});
		
		touch.setOnClickListener(new OnClickListener() {
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
		
		if (mSelects.contains(hero.id)) {
			vh.name.setTextColor(0xff000000);
		} else {
			Country country = Country.values()[hero.country];
			vh.name.setTextColor(country.color);
		}
	}
	
	private static class ViewHolder {
		public Hero hero;
		
		public TextView name;
	}
}
