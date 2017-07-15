package com.as.chain.activity;

import com.as.chain.R;

import android.os.Bundle;

public class MainActivity extends BaseActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
}
