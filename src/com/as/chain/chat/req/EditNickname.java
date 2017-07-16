package com.as.chain.chat.req;

import com.as.app.App;
import com.as.chain.R;
import com.as.chain.chat.ReqMsg;

public class EditNickname extends ReqMsg {
	public static final int MIN_LEN = 1;
	public static final int MAX_LEN = 20;
	
	public String nickname;
	
	public static String check(String nickname) {
		if (nickname == null || nickname.length() < MIN_LEN
				|| nickname.length() > MAX_LEN) {
			
			return App.getResources().getString(R.string.err_nickname_length_limit,
				MIN_LEN, MAX_LEN);
		}
		
		return null;
	}
}
