package com.as.chain.activity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.as.chain.R;
import com.as.chain.util.DataMgr;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadUtil;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class UpdateActivity extends BaseActivity {
	public static final String TAG = UpdateActivity.class.getSimpleName();
	
	private TextView mTvText;
	
	private String mMajorServion;
	private final List<String> mDeleteList = new ArrayList<String>();
	private final List<String> mDownloadList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update);
		
		mTvText = (TextView) findViewById(R.id.tv_text);
		
		mTvText.setText(R.string.msg_checking_update);
		
		ThreadUtil.getVice().run(new Runnable() {
			public void run() {
				try {
					boolean ret = checkMajorVersion();
					if (ret) {
						ThreadUtil.getMain().run(new Runnable() {
							@Override
							public void run() {
								mTvText.setText(R.string.msg_update_to_latest_version);
								
								Intent intent = new Intent(UpdateActivity.this, MainActivity.class);
								startActivity(intent);
							}
						});
						
						return;
					}
					
					
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
	
	private boolean checkMajorVersion() throws Exception {
		File file = new File(DataMgr.FILE_DIR, "file_version.txt");
		if (file.exists() == false) {
			return false;
		}
		
		String client = null;
		
		Scanner scanner = new Scanner(file);
		if (scanner.hasNext()) {
			client = scanner.next();
		}
		scanner.close();
		
		if (client == null) {
			return false;
		}
		
		URL url = new URL(DataMgr.UPDATE_URL + "file_version.txt");
		URLConnection connection = url.openConnection();
		connection.connect();
		
		String server = null;
		
		InputStream is = connection.getInputStream();
		scanner = new Scanner(is);
		if (scanner.hasNext()) {
			server = scanner.next();
		}
		
		scanner.close();
		
		if (server == null) {
			throw new Exception("read file_version.txt failed");
		}
		
		return client.equals(server);
	}
	
	private boolean checkMinorVersion() throws Exception {
		URL url = new URL(DataMgr.UPDATE_URL + "file_list.txt");
		URLConnection connection = url.openConnection();
		connection.connect();
		
		Map<String, String> server = new HashMap<String, String>();
		InputStream is = connection.getInputStream();
		Scanner scanner = new Scanner(is);
		
		while (scanner.hasNext()) {
			String name = scanner.next();
			String md5 = scanner.next();
			server.put(name, md5);
		}
		
		scanner.close();
		
		Map<String, String> client = new HashMap<String, String>();
		File file = new File(DataMgr.FILE_DIR, "file_list.txt");
		if (file.exists()) {
			scanner = new Scanner(file);
			
			while (scanner.hasNext()) {
				String name = scanner.next();
				String md5 = scanner.next();
				client.put(name, md5);
			}
			
			scanner.close();
		}
		
		
		
		return false;
	}
}
