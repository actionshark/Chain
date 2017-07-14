package com.as.chain.util;

import java.io.File;

import com.as.app.App;

import android.content.Context;

public class DataMgr {
	public static final String TAG = DataMgr.class.getSimpleName();
	
	public static final File FILE_DIR;
	public static final String FILE_PATH;
	
	public static final String UPDATE_URL = "http://101.200.59.142/chain/";
	
	static {
		Context context = App.getInstance().getContext();
		FILE_DIR = context.getFilesDir();
		FILE_PATH = FILE_DIR.getPath() + "/";
	}
}
