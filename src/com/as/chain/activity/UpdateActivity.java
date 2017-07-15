package com.as.chain.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import com.as.chain.R;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.SimpleDialog;
import com.as.chain.util.DataMgr;
import com.js.app.file.FileUtil;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.security.MD5;
import com.js.thread.ThreadUtil;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;

public class UpdateActivity extends BaseActivity {
	public static final String TAG = UpdateActivity.class.getSimpleName();
	
	private TextView mTvText;
	
	private final List<String> mDeleteList = new ArrayList<String>();
	private final List<String> mDownloadList = new ArrayList<String>();
	
	private Semaphore mSemaphore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		mSemaphore = new Semaphore(0, true);
		
		mTvText = (TextView) findViewById(R.id.tv_text);
		mTvText.setText(R.string.msg_checking_update);
		
		ThreadUtil.getVice().run(new Runnable() {
			public void run() {
				try {
					checkMajorVersion();
					
					if (mDeleteList.size() == 0 && mDownloadList.size() == 0) {
						ThreadUtil.getMain().run(new Runnable() {
							@Override
							public void run() {
								entryGame();
							}
						});
						
						return;
					}
					
					ThreadUtil.getMain().run(new Runnable() {
						@Override
						public void run() {
							Context context = UpdateActivity.this;
							Resources res = context.getResources();
							String text = res.getString(R.string.msg_delete_download_hint,
								mDeleteList.size(), mDownloadList.size());
							
							final SimpleDialog sd = new SimpleDialog(context);
							sd.setMessage(text);
							sd.setButtons(R.string.wd_ok);
							sd.setClickListener(new IDialogClickListener() {
								@Override
								public void onClick(Dialog dialog, int index, ClickType type) {
									sd.dismiss();
									mSemaphore.release();
								}
							});
							sd.setCanceledOnTouchOutside(false);
							sd.show();
						}
					});
					
					mSemaphore.acquire();
					
					deleteFiles();
					downloadFiles();
					
					ThreadUtil.getMain().run(new Runnable() {
						@Override
						public void run() {
							entryGame();
						}
					});
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
					
					ThreadUtil.getMain().run(new Runnable() {
						@Override
						public void run() {
							mTvText.setText(R.string.err_update_failed);
						}
					});
				}
			}
		});
	}
	
	private void checkMajorVersion() throws Exception {
		// 计算本地版本号
		Logger.getInstance().print(TAG, Level.D, "client files :");
		
		Map<String, String> clientMap = new HashMap<String, String>();
		calcMD5(DataMgr.UPDATE_DIR, clientMap);
		
		// 获取服务器版本号
		URL url = new URL(DataMgr.UPDATE_URL + "file_list.txt");
		URLConnection connection = url.openConnection();
		connection.connect();
		
		InputStream is = connection.getInputStream();
		Scanner scanner = new Scanner(is);
		Map<String, String> serverMap = new HashMap<String, String>();
		
		Logger.getInstance().print(TAG, Level.D, "server files :");
		
		while (scanner.hasNext()) {
			String name = scanner.next();
			String md5 = scanner.next();
			serverMap.put(name, md5);
			
			Logger.getInstance().print(TAG, Level.D, name, md5);
		}
		
		scanner.close();
		
		// 得出要删除的文件
		Logger.getInstance().print(TAG, Level.D, "delete files :");
		
		mDeleteList.clear();
		for (String name : clientMap.keySet()) {
			if (serverMap.containsKey(name) == false) {
				mDeleteList.add(name);
				
				Logger.getInstance().print(TAG, Level.D, name);
			}
		}
		
		// 得出要下载的文件
		Logger.getInstance().print(TAG, Level.D, "download files :");
		mDownloadList.clear();
		for (Entry<String, String> entry : serverMap.entrySet()) {
			String name = entry.getKey();
			
			if (clientMap.containsKey(name)) {
				String server = entry.getValue();
				String client = clientMap.get(name);
				
				if (server.equals(client)) {
					continue;
				}
			}
			
			mDownloadList.add(name);
			
			Logger.getInstance().print(TAG, Level.D, name);
		}
	}
	
	private void calcMD5(File file, Map<String, String> map) throws Exception {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			for (File child : children) {
				calcMD5(child, map);
			}
		} else {
			String name = file.getAbsolutePath()
				.substring(DataMgr.UPDATE_PATH.length());
			
			InputStream is = new FileInputStream(file);
			String md5 = MD5.encode(is);
			is.close();
			
			map.put(name, md5);
			
			Logger.getInstance().print(TAG, Level.D, name, md5);
		}
	}
	
	private void deleteFiles() throws Exception {
		final int size = mDeleteList.size();
		
		for (int i = 0; i < size; i++) {
			final int idx = i;
			final String name = mDeleteList.get(i);
			
			ThreadUtil.getMain().run(new Runnable() {
				@Override
				public void run() {
					Resources res = UpdateActivity.this.getResources();
					String text = res.getString(R.string.msg_deleting_files,
						idx + 1, size, name);
					mTvText.setText(text);
				}
			});
			
			File file = new File(DataMgr.UPDATE_DIR, name);
			file.delete();
		}
	}
	
	private void downloadFiles() throws Exception {
		final int size = mDownloadList.size();
		
		for (int i = 0; i < size; i++) {
			final int idx = i;
			final String name = mDownloadList.get(i);
			
			ThreadUtil.getMain().run(new Runnable() {
				@Override
				public void run() {
					Resources res = UpdateActivity.this.getResources();
					String text = res.getString(R.string.msg_downloading_files,
						idx + 1, size, name);
					mTvText.setText(text);
				}
			});
			
			File client = new File(DataMgr.UPDATE_DIR, name);
			File parent = client.getParentFile();
			if (parent.exists() == false) {
				parent.mkdirs();
			}
			if (client.exists() == false) {
				client.createNewFile();
			}
			
			String server = DataMgr.UPDATE_FILE_URL + name;
			Logger.getInstance().print(TAG, Level.D, server, client.getAbsolutePath());
			
			URL url = new URL(server);
			URLConnection connection = url.openConnection();
			connection.connect();
			
			InputStream is = connection.getInputStream();
			OutputStream os = new FileOutputStream(client);
			FileUtil.copy(is, os);
			
			is.close();
			os.close();
		}
	}
	
	private void entryGame() {
		mTvText.setText(R.string.msg_update_to_latest_version);
		
		Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
		startActivity(intent);
	}
}
