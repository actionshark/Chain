package com.as.chain.ui;

import com.as.chain.R;
import com.js.thread.ThreadUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BattleHero extends LinearLayout {
	public static final String TAG = BattleHero.class.getSimpleName();
	
	public static enum Status {
		Normal(0xff000000),
		Active(0xff0000ff),
		Dead(0xffffffff);
		
		private final int mColor;
		
		private Status(int color) {
			mColor = color;
		}
		
		public int getColor() {
			return mColor;
		}
	}
	
	private TextView mTvBuffer;
	
	private ProgressBar mPbHealth;
	private ProgressBar mPbShield;
	
	private TextView mTvName;
	
	private TextView mTvDisplay;
	private Object mDisplayMark;
	
	private Status mStatus = Status.Normal;
	
	public BattleHero(Context context) {
		super(context);
		
		LayoutInflater.from(context).inflate(R.layout.battle_hero, this);
	}
	
	public void init(int width, int height) {
		android.view.ViewGroup.LayoutParams lp = getLayoutParams();
		lp.width = width * 8 / 10;
		lp.height = height * 12 / 10;
		
		mTvBuffer = (TextView) findViewById(R.id.tv_buffer);
		
		mPbHealth = (ProgressBar) findViewById(R.id.pb_health);
		mPbShield = (ProgressBar) findViewById(R.id.pb_shield);
		
		mTvName = (TextView) findViewById(R.id.tv_name);
		mTvDisplay = (TextView) findViewById(R.id.tv_display);
	}
	
	public void setBuffer(String buffer) {
		mTvBuffer.setText(buffer);
	}
	
	public void setHealth(int cur, int max) {
		mPbHealth.setProgress(cur / (float) max);
	}
	
	public void setShield(int cur, int max) {
		if (cur > 0) {
			mPbShield.setProgress(cur / (float) max);
			mPbShield.setVisibility(View.VISIBLE);
		} else {
			mPbShield.setVisibility(View.INVISIBLE);
		}
	}
	
	public void setName(String name) {
		StringBuilder sb = new StringBuilder();
		int len = name.length();
		for (int i = 0; i < len; i++) {
			sb.append(name.charAt(i));
			
			if (i < len - 1) {
				sb.append('\n');
			}
		}
		mTvName.setText(sb.toString());
	}
	
	public void setStatus(Status status) {
		mStatus = status;
		mTvName.setTextColor(mStatus.getColor());
	}
	
	public void display(String text, int color) {
		mTvDisplay.setText(text);
		mTvDisplay.setTextColor(color);
		mTvDisplay.setVisibility(View.VISIBLE);
		
		final Object mark = new Object();
		mDisplayMark = mark;
		
		ThreadUtil.getMain().run(new Runnable() {
			public void run() {
				if (mDisplayMark == mark) {
					mTvDisplay.setVisibility(View.INVISIBLE);
				}
			}
		}, 1000);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setX(float x) {
		android.widget.AbsoluteLayout.LayoutParams lp =
			(android.widget.AbsoluteLayout.LayoutParams) getLayoutParams();
		lp.x = (int) x - lp.width / 2;
		
		super.setX(lp.x);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void setY(float y) {
		android.widget.AbsoluteLayout.LayoutParams lp =
			(android.widget.AbsoluteLayout.LayoutParams) getLayoutParams();
		lp.y = (int) y - lp.height * 3 / 5;
		
		super.setY(lp.y);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getX() {
		android.widget.AbsoluteLayout.LayoutParams lp =
				(android.widget.AbsoluteLayout.LayoutParams) getLayoutParams();
		return lp.x + lp.width / 2;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getY() {
		android.widget.AbsoluteLayout.LayoutParams lp =
				(android.widget.AbsoluteLayout.LayoutParams) getLayoutParams();
		return lp.y + lp.height * 3 / 5;
	}
}
