package com.as.chain.chat.rsp;

import com.as.chain.R;
import com.as.chain.activity.BaseActivity;
import com.as.chain.chat.ChatClient;
import com.as.chain.chat.RspMsg;
import com.as.chain.chat.req.RandBattleCancel;
import com.as.chain.ui.IDialogClickListener;
import com.as.chain.ui.SimpleDialog;
import com.js.event.BrcstMgr;
import com.js.event.IBrcstListener;
import com.js.log.Level;
import com.js.log.Logger;
import com.js.thread.ThreadHandler;
import com.js.thread.ThreadUtil;

import android.app.Dialog;
import android.content.Context;

public class BattleWaiting extends RspMsg {
	public static final String TAG = BattleWaiting.class.getSimpleName();
	
	private SimpleDialog mDialog;
	private ThreadHandler mCountdownHandler;
	private IBrcstListener mListener = new IBrcstListener() {
		@Override
		public void onBroadcast(String name, Object data) {
			mDialog.dismiss();
			mCountdownHandler.cancel();
			BrcstMgr.getInstance().removeListener(null, mListener);
		}
	};
	
	@Override
	public void perform(ChatClient server) throws Exception {
		ThreadUtil.getMain().run(new Runnable() {
			@Override
			public void run() {
				try {
					final Context context = BaseActivity.getInstance();
					mDialog = new SimpleDialog(context);
					
					mDialog.setButtons(R.string.wd_cancel);
					mDialog.setClickListener(new IDialogClickListener() {
						@Override
						public void onClick(Dialog dialog, int index, ClickType type) {
							RandBattleCancel rbc = new RandBattleCancel();
							rbc.send();
						}
					});
					mDialog.setCanceledOnTouchOutside(false);
					mDialog.show();
					
					BrcstMgr.getInstance().addListener(BattleStart.TAG, mListener, true);
					BrcstMgr.getInstance().addListener(BattleEnd.TAG, mListener, true);
					
					final long timeoutPoint = mParams.getLong("timeoutPoint");
					mCountdownHandler = ThreadUtil.getMain().run(new Runnable() {
						@Override
						public void run() {
							if (mDialog.isShowing()) {
								long delta = timeoutPoint - System.currentTimeMillis();
								if (delta < 0) {
									delta = 0;
								}
								
								mDialog.setMessage(context.getString(
									R.string.msg_waiting_rand_battle,
									delta / 1000));
							} else {
								mDialog.dismiss();
								mCountdownHandler.cancel();
								BrcstMgr.getInstance().removeListener(null, mListener);
							}
						}
					}, 0, 1000);
				} catch (Exception e) {
					Logger.getInstance().print(TAG, Level.E, e);
				}
			}
		});
	}
}
