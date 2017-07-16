package com.as.chain.chat.rsp;

import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.as.chain.chat.UserInfor;
import com.as.chain.util.DataMgr;
import com.js.event.BrcstMgr;

public class UserInfo extends RspMsg {
	public static final String TAG = UserInfo.class.getSimpleName();
	
	@Override
	public void perform(ChatClient server) throws Exception {
		UserInfor ui = new UserInfor();
		
		ui.account = mParams.getString(UserInfor.KEY_ACCOUNT);
		ui.id = mParams.getInt(UserInfor.KEY_ID);
		ui.createTime = mParams.getLong(UserInfor.KEY_CREATE_TIME);
		ui.latestLogin = mParams.getLong(UserInfor.KEY_LATEST_LOGIN);
		ui.loginTimes = mParams.getInt(UserInfor.KEY_LOGIN_TIMES);
		
		if (mParams.has(UserInfor.KEY_NICKNAME)) {
			ui.nickname = mParams.getString(UserInfor.KEY_NICKNAME);
		}
		
		DataMgr.setUserInfo(ui);
		BrcstMgr.getInstance().send(TAG, ui);
	}
}
