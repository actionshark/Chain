package com.as.chain.chat;

import org.json.JSONObject;

import com.as.app.App;
import com.as.chain.chat.req.Login;
import com.js.event.BrcstMgr;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.talk.ITalkClientListener;
import com.js.talk.TalkClient;

public class ChatClient {
	public static final String TAG = ChatClient.class.getSimpleName();
	
	public static enum Status {
		Offline, Connecting, Online,
	}
	
	private TalkClient mClient;
	
	private Status mStatus = Status.Offline;
	
	public void setClient(TalkClient client) {
		mClient = client;
	}
	
	public synchronized Status getStatus() {
		return mStatus;
	}
	
	public void send(JSONObject jo) {
		String str = jo.toString();
		byte[] bs = str.getBytes();
		mClient.send(bs);
		
		Logger.getInstance().print(TAG, Level.D, str);
	}
	
	public void start() {
		mClient.start();
		mClient.setListener(new ITalkClientListener() {
			@Override
			public void onConnected(TalkClient client) {
				Logger.getInstance().print(TAG, Level.D);
				
				synchronized (ChatClient.this) {
					if (mStatus != Status.Connecting) {
						mStatus = Status.Connecting;
						BrcstMgr.getInstance().send(TAG, mStatus);
					}
				}
				
				Login login = new Login();
				login.account = App.getDeviceId();
				login.send();
			}

			@Override
			public void onConnecting(TalkClient client) {
				synchronized (ChatClient.this) {
					if (mStatus != Status.Connecting) {
						mStatus = Status.Connecting;
						BrcstMgr.getInstance().send(TAG, mStatus);
					}
				}
			}
			
			@Override
			public void onReceived(TalkClient client,
					byte[] data, int offset, int length) {
				
				synchronized (ChatClient.this) {
					if (mStatus != Status.Online) {
						mStatus = Status.Online;
						BrcstMgr.getInstance().send(TAG, mStatus);
					}
				}

				RspMsg msg = MsgParser.parse(data, offset, length);
				if (msg == null) {
					return;
				}
				
				try {
					msg.perform(ChatClient.this);
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}

			@Override
			public void onOffline(TalkClient client) {
				synchronized (ChatClient.this) {
					if (mStatus != Status.Offline) {
						mStatus = Status.Offline;
						BrcstMgr.getInstance().send(TAG, mStatus);
					}
				}
			}
		});
	}
}
