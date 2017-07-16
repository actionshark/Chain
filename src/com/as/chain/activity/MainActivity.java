package com.as.chain.activity;

import com.as.chain.R;
import com.as.chain.chat.ChatClient;
import com.as.chain.chat.UserInfor;
import com.as.chain.chat.req.EditNickname;
import com.as.chain.chat.rsp.UserInfo;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.InputDialog;
import com.as.chain.util.DataMgr;
import com.js.event.BrcstMgr;
import com.js.event.IBrcstListener;

import android.app.Dialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private long mLatestBackTime = 0;
	
	private TextView mTvNickname;
	private boolean mNoNnHint = false;
	
	private TextView mTvStatus;
	
	private IBrcstListener mListener = new IBrcstListener() {
		@Override
		public void onBroadcast(String name, Object data) {
			if (UserInfo.TAG.equals(name)) {
				updateNickname();
			} else if (ChatClient.TAG.equals(name)) {
				updateStatus();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTvNickname = (TextView) findViewById(R.id.tv_nickname);
		mTvStatus = (TextView) findViewById(R.id.tv_status);
		findViewById(R.id.ml_userinfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editNickname();
			}
		});
		
		DataMgr.connectServer();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		BrcstMgr.getInstance().addListener(UserInfo.TAG, mListener, true);
		BrcstMgr.getInstance().addListener(ChatClient.TAG, mListener, true);
		
		updateNickname();
		updateStatus();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		BrcstMgr.getInstance().removeListener(null, mListener);
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long now = System.currentTimeMillis();
			
			if (now - mLatestBackTime > 500) {
				Toast.makeText(this, R.string.msg_press_two_times_to_exit,
					Toast.LENGTH_SHORT).show();
				mLatestBackTime = now;
			} else {
				com.as.app.App.exitApp();
			}
			
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
	
	private void updateNickname() {
		UserInfor ui = DataMgr.getUserInfo();
		
		if (ui != null && ui.nickname != null) {
			mTvNickname.setText(ui.nickname);
		} else {
			mTvNickname.setText("");
		}
		
		if (ui != null && ui.nickname == null && mNoNnHint == false) {
			mNoNnHint = true;
			editNickname();
		}
	}
	
	private void editNickname() {
		final InputDialog id = new InputDialog(this);
		id.setMessage(R.string.msg_input_nickname);
		id.setClickListener(new IDialogClickListener() {
			@Override
			public void onClick(Dialog dialog, int index, ClickType type) {
				if (index == 1) {
					String input = id.getInput();
					String err = EditNickname.check(input);
					
					if (err == null) {
						EditNickname en = new EditNickname();
						en.nickname = input;
						en.send();
						
						id.dismiss();
					} else {
						Toast.makeText(MainActivity.this, err, Toast.LENGTH_SHORT).show();
					}
				} else {
					id.dismiss();
				}
			}
		});
		id.show();
	}
	
	private void updateStatus() {
		ChatClient.Status status = DataMgr.getClient().getStatus();
		
		if (status == ChatClient.Status.Online) {
			mTvStatus.setText(R.string.wd_online);
			mTvStatus.setTextColor(0xff00bb00);
		} else if (status == ChatClient.Status.Offline) {
			mTvStatus.setText(R.string.wd_offline);
			mTvStatus.setTextColor(0xffff0000);
		} else if (status == ChatClient.Status.Connecting) {
			mTvStatus.setText(R.string.wd_connecting);
			mTvStatus.setTextColor(0xffbbbb00);
		} else {
			mTvStatus.setTag(R.string.wd_unkonwn);
			mTvStatus.setTextColor(0xff000000);
		}
	}
}
