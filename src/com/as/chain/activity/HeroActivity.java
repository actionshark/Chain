package com.as.chain.activity;

import com.as.chain.R;

import android.os.Bundle;

public class HeroActivity extends BaseActivity {
	public static final String TAG = HeroActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hero);
	}
}
