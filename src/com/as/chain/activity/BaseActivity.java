package com.as.chain.activity;

import com.as.chain.R;
import com.stone.metro.MetroAnimation;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseActivity extends Activity {
	protected MetroAnimation mMetroAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mMetroAnimation = new MetroAnimation();
	}

	@Override
	protected void onResume() {
		super.onResume();

		View root = findViewById(R.id.vg_root);
		mMetroAnimation.runStarting((ViewGroup) root);
	}
}
