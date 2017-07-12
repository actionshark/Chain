package com.as.chain.activity;

import com.as.chain.R;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.InputDialog;
import com.as.chain.util.Const;
import com.as.chain.util.DataMgr;
import com.as.game.ScriptMgr;
import com.as.app.App;
import com.as.app.Setting;
import com.as.network.NetworkUtil;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private TextView mTvNickname;
	
	private TextView mTvAddress;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		findViewById(R.id.ml_roleinfo).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final InputDialog id = new InputDialog(MainActivity.this);
				
				id.setMessage(R.string.msg_input_nickname);
				
				String nickname = mTvNickname.getText().toString();
				id.setInput(nickname);
				id.setSelection(nickname.length());
				
				id.setClickListener(new IDialogClickListener() {
					@Override
					public void onClick(Dialog dialog, int index, ClickType type) {
						if (index == 1) {
							String input = id.getInput();
							if (input.length() < 1 || input.length() > 10) {
								Toast.makeText(MainActivity.this,
									App.getInstance().getResources().getString(
									R.string.err_nickname_length, 1, 10),
									Toast.LENGTH_SHORT)
								.show();
								return;
							}
							
							Setting.getInstance().setString(Const.KEY_NICKNAME, input);
							mTvNickname.setText(input);
						}
						
						dialog.dismiss();
					}
				});
				
				id.show();
			}
		});
		
		mTvNickname = (TextView) findViewById(R.id.tv_nickname);
		
		mTvAddress = (TextView) findViewById(R.id.tv_address);
		
		findViewById(R.id.ml_hero).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ScriptMgr sm = ScriptMgr.getInstance();
			}
		});
		
		DataMgr.checkLocalData();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		String nickname = Setting.getInstance().getString(Const.KEY_NICKNAME);
		if (nickname == null) {
			nickname = App.getInstance().getResources().getString(R.string.def_nickname);
		}
		mTvNickname.setText(nickname);
		
		String address = NetworkUtil.getIPAddress();
		if (address == null) {
			mTvAddress.setText(R.string.err_no_network);
		} else {
			mTvAddress.setText(address);
		}
	}
}
