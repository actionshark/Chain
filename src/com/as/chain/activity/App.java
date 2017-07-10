package com.as.chain.activity;

import com.stone.app.Setting;

import android.app.Application;

public class App extends Application {
	public void onCreate() {
		super.onCreate();
		
		com.stone.app.App.getInstance().onApplicationCreate(this);
		
		Setting.setInstance("setting");
	}
}
