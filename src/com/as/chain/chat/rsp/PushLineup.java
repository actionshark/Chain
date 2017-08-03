package com.as.chain.chat.rsp;

import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.as.chain.game.Lineup;

public class PushLineup extends RspMsg {
	public static final String TAG = PushLineup.class.getSimpleName();
	
	@Override
	public void perform(ChatClient server) throws Exception {
		String data = mParams.getString("data");
		
		Lineup.onPushData(data);
	}
}
