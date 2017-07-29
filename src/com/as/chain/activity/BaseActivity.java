package com.as.chain.activity;

import com.as.chain.R;
import com.as.metro.MetroAnimation;
import com.as.metro.MetroAnimation.IAnimationListener;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseActivity extends Activity {
	protected static BaseActivity sActivity;
	
	protected MetroAnimation mMetroAnimation;
	protected int mMetroAnimationCount = 0;
	
	public static BaseActivity getInstance() {
		return sActivity;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sActivity = this;

		mMetroAnimation = new MetroAnimation();
		mMetroAnimation.setListener(new IAnimationListener() {
			@Override
			public void onFinish() {
				onAnimationFinish(++mMetroAnimationCount);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();

		View root = findViewById(R.id.vg_root);
		mMetroAnimation.runStarting((ViewGroup) root);
	}
	
	protected void onAnimationFinish(int count) {
	}
}
