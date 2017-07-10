package com.as.chain.ui;

import com.as.chain.R;
import com.as.chain.ui.IDialogClickListener.ClickType;
import com.stone.app.Res;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;

public class InputDialog extends Dialog {
	private TextView mTvMessage;
	private EditText mEtInput;

	private IDialogClickListener mClickListener;

	public InputDialog(Context context) {
		super(context, R.style.simple_dialog);
		init();
	}

	protected void init() {
		setContentView(R.layout.dialog_input);

		Window window = getWindow();
		LayoutParams lp = window.getAttributes();
		lp.width = Res.getInstance().getScreenWidth() * 5 / 10;
		lp.height = Res.getInstance().getScreenHeight() * 6 / 10;
		window.setAttributes(lp);

		mTvMessage = (TextView) findViewById(R.id.tv_message);

		mEtInput = (EditText) findViewById(R.id.et_input);

		findViewById(R.id.ml_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mClickListener != null) {
					mClickListener.onClick(InputDialog.this, 0, ClickType.Click);
				}
			}
		});

		findViewById(R.id.ml_confirm).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (mClickListener != null) {
					mClickListener.onClick(InputDialog.this, 1, ClickType.Click);
				}
			}
		});

		setCanceledOnTouchOutside(true);
		setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface di) {
				if (mClickListener != null) {
					mClickListener.onClick(InputDialog.this, -1, ClickType.Click);
				}
			}
		});

		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
			| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
	}

	public void setMessage(int resId) {
		mTvMessage.setText(resId);
	}

	public void setMessage(String text) {
		mTvMessage.setText(text);
	}

	public void setInput(String text) {
		mEtInput.setText(text);
	}

	public String getInput() {
		return mEtInput.getText().toString();
	}
	
	public void setInputType(int type) {
		mEtInput.setInputType(type);
	}

	public void setSelection(int index) {
		mEtInput.setSelection(index);
	}

	public void setSelection(int start, int stop) {
		mEtInput.setSelection(start, stop);
	}

	public void setClickListener(IDialogClickListener listener) {
		mClickListener = listener;
	}
}
