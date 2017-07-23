package com.as.chain.chat.rsp;

import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.as.chain.chat.UserInfo;
import com.as.chain.util.DataMgr;
import com.js.event.BrcstMgr;

public class PushUserInfo extends RspMsg {
	public static final String TAG = PushUserInfo.class.getSimpleName();
	
	@Override
	public void perform(ChatClient server) throws Exception {
		UserInfo ui = new UserInfo();
		
		ui.account = mParams.getString(UserInfo.KEY_ACCOUNT);
		ui.id = mParams.getInt(UserInfo.KEY_ID);
		ui.latestLogin = mParams.getLong(UserInfo.KEY_LATEST_LOGIN);
		
		if (mParams.has(UserInfo.KEY_NICKNAME)) {
			ui.nickname = mParams.getString(UserInfo.KEY_NICKNAME);
		}
		
		DataMgr.setUserInfo(ui);
		BrcstMgr.getInstance().send(TAG, ui);
	}
}
