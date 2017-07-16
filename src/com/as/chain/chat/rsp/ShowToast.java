package com.as.chain.chat.rsp;

import com.as.chain.activity.BaseActivity;
import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.js.thread.ThreadUtil;

import android.content.Context;
import android.widget.Toast;

public class ShowToast extends RspMsg {
	@Override
	public void perform(ChatClient server) throws Exception {
		final String message = mParams.getString("message");
		final Context context = BaseActivity.getInstance();
		
		ThreadUtil.getMain().run(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}
}
