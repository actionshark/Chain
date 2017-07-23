package com.as.chain.chat.rsp;

import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.js.event.BrcstMgr;

public class BattleEnd extends RspMsg {
	public static final String TAG = BattleEnd.class.getSimpleName();
	
	@Override
	public void perform(ChatClient server) throws Exception {
		BrcstMgr.getInstance().send(TAG, null);
	}
}
