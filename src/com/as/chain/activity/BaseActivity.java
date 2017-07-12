package com.as.chain.activity;

import com.as.chain.R;
import com.as.metro.MetroAnimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseActivity extends Activity {
	protected static BaseActivity sActivity;
	
	protected MetroAnimation mMetroAnimation;
	
	public static BaseActivity getInstance() {
		return sActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sActivity = this;

		mMetroAnimation = new MetroAnimation();
	}

	@Override
	protected void onResume() {
		super.onResume();

		View root = findViewById(R.id.vg_root);
		mMetroAnimation.runStarting((ViewGroup) root);
	}
}
