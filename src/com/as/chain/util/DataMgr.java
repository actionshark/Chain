package com.as.chain.util;

import java.io.File;

import com.as.app.App;

import android.content.Context;

public class DataMgr {
	public static final String TAG = DataMgr.class.getSimpleName();
	
	public static final File FILE_DIR;
	public static final String FILE_PATH;
	
	public static final File UPDATE_DIR;
	public static final String UPDATE_PATH;
	
	public static final String UPDATE_URL = "http://101.200.59.142/chain/";
	public static final String UPDATE_FILE_URL = "http://101.200.59.142/chain/file/";
	
	static {
		Context context = App.getContext();
		
		FILE_DIR = context.getFilesDir();
		FILE_PATH = FILE_DIR.getAbsolutePath() + "/";
		
		UPDATE_DIR = new File(FILE_DIR, "file");
		if (UPDATE_DIR.exists() == false) {
			UPDATE_DIR.mkdirs();
		} else if (UPDATE_DIR.isDirectory() == false) {
			UPDATE_DIR.delete();
			UPDATE_DIR.mkdirs();
		}
		UPDATE_PATH = UPDATE_DIR.getAbsolutePath() + "/";
	}
}
