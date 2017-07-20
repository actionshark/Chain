package com.as.chain.util;

import java.io.File;

import com.as.app.App;
import com.as.chain.chat.ChatClient;
import com.as.chain.chat.UserInfo;
import com.js.network.NetClient;
import com.js.talk.TalkClient;

import android.content.Context;

public class DataMgr {
	public static final String TAG = DataMgr.class.getSimpleName();
	
	public static final File FILE_DIR;
	public static final String FILE_PATH;
	
	public static final File UPDATE_DIR;
	public static final String UPDATE_PATH;
	
	public static final String UPDATE_URL = "http://101.200.59.142/chain/";
	public static final String UPDATE_FILE_URL = "http://101.200.59.142/chain/file/";
	
	public static final String SERVER_HOST = "101.200.59.142";
	public static final int SERVER_PORT = 20001;
	
	private static NetClient mNetClient;
	private static TalkClient mTalkClient;
	private static ChatClient mChatClient;
	
	private static UserInfo sUserInfor;
	
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
	
	public static synchronized void connectServer() {
		if (mNetClient != null) {
			mNetClient.close();
		}
		
		mNetClient = new NetClient();
		mNetClient.setHost(DataMgr.SERVER_HOST);
		mNetClient.setPort(DataMgr.SERVER_PORT);
		
		mTalkClient = new TalkClient();
		mTalkClient.setClient(mNetClient);
		
		mChatClient = new ChatClient();
		mChatClient.setClient(mTalkClient);
		mChatClient.start();
	}
	
	public static synchronized ChatClient getClient() {
		return mChatClient;
	}
	
	public static synchronized void setUserInfo(UserInfo ui) {
		sUserInfor = ui;
	}
	
	public static synchronized UserInfo getUserInfo() {
		return sUserInfor;
	}
}
