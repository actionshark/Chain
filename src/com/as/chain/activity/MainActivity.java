package com.as.chain.activity;

import com.as.chain.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	public static final String TAG = MainActivity.class.getSimpleName();
	
	private long mLatestBackTime = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
				finish();
				com.as.app.App.exitApp();
			}
			
			return true;
		}
		
		return super.onKeyUp(keyCode, event);
	}
}
