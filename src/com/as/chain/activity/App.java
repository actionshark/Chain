package com.as.chain.activity;

import com.as.app.Setting;

import android.app.Application;

public class App extends Application {
	public void onCreate() {
		super.onCreate();
		
		com.as.app.App.getInstance().onApplicationCreate(this);
		
		Setting.setInstance("setting");
	}
}
