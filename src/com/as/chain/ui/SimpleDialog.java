package com.as.chain.ui;

import com.as.chain.R;
import com.as.chain.ui.IDialogClickListener.ClickType;
import com.stone.app.App;
import com.stone.app.Res;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class SimpleDialog extends Dialog {
	private TextView mTvMessage;
	private final View[] mViewButtons = new View[5]; 
	private final TextView[] mTvBtnTexts = new TextView[mViewButtons.length];

	private IDialogClickListener mClickListener;

	public SimpleDialog(Context context) {
		super(context, R.style.simple_dialog);
		init();
	}

	protected void init() {
		setContentView(R.layout.dialog_simple);

		Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		lp.width = Res.getInstance().getScreenWidth() * 5 / 10;
		lp.height = Res.getInstance().getScreenHeight() * 6 / 10;
		window.setAttributes(lp);

		mTvMessage = (TextView) findViewById(R.id.tv_message);

		for (int i = 0; i < mViewButtons.length; i++) {
			int id = Res.getInstance().getIdId("ml_btn_" + i);
			mViewButtons[i] = findViewById(id);
			mTvBtnTexts[i] = (TextView) mViewButtons[i].findViewById(R.id.tv_text);

			final int index = i;
			mViewButtons[i].setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (mClickListener != null) {
						mClickListener.onClick(SimpleDialog.this, index, ClickType.Click);
					}
				}
			});
		}

		setCanceledOnTouchOutside(true);
		setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface di) {
				if (mClickListener != null) {
					mClickListener.onClick(SimpleDialog.this, -1, ClickType.Click);
				}
			}
		});
	}

	public void setMessage(int resId) {
		mTvMessage.setText(resId);
	}

	public void setMessage(String text) {
		mTvMessage.setText(text);
	}

	public void setButtons(Object... btns) {
		for (int i = 0; i < mViewButtons.length; i++) {
			if (i < btns.length) {
				Object btn = btns[i];
				if (btn instanceof Integer) {
					btn = App.getInstance().getResources().getString((Integer) btn);
				}

				mTvBtnTexts[i].setText(String.valueOf(btn));
				mViewButtons[i].setVisibility(View.VISIBLE);
			} else {
				mViewButtons[i].setVisibility(View.GONE);
			}
		}
	}

	public void setClickListener(IDialogClickListener listener) {
		mClickListener = listener;
	}
}
