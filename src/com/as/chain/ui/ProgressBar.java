package com.as.chain.ui;

import com.as.chain.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ProgressBar extends View {
	public static enum Direct {
		Left, Right, Top, Bottom,
	}
	
	private Paint mPaint = new Paint();
	
	private Direct mDirect = Direct.Left;
	
	private int mStartColor = 0xff000000;
	private int mEndColor = 0xff000000;
	
	private float mProgress = 0f;
	
	public ProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}
	
	public ProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context, attrs);
	}
	
	private void init(Context context, AttributeSet attrs) {
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.chain);
		
		int direct = ta.getInt(R.styleable.chain_direct, 0);
		mDirect = Direct.values()[direct];
		
		mStartColor = ta.getColor(R.styleable.chain_start_color, 0xff000000);
		mEndColor = ta.getColor(R.styleable.chain_end_color, mStartColor);
		
		ta.recycle();
		
		mPaint.setAntiAlias(true);
		mPaint.setColor(calcColor());
	}
	
	public void setProgress(float progress) {
		mProgress = progress;
		postInvalidate();
	}
	
	private int calcMask(int start, int end, int mask) {
		start = start & mask;
		end = end & mask;
		return (int) (start + (end - start) * mProgress) & mask;
	}
	
	private int calcColor() {
		// it is wrong!
		return (mStartColor & 0xff000000)
			| calcMask(mStartColor, mEndColor, 0xff0000)
			| calcMask(mStartColor, mEndColor, 0xff00)
			| calcMask(mStartColor, mEndColor, 0xff);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int pl = getPaddingLeft();
		int pt = getPaddingTop();
		int pr = getPaddingRight();
		int pb = getPaddingBottom();
		int fw = getWidth();
		int fh = getHeight();
		int cw = fw - pl - pr;
		int ch = fh - pt - pb;
		
		float lf = mDirect == Direct.Right ? cw * (1 - mProgress) + pl : pl;
		float tp = mDirect == Direct.Bottom ? ch * (1 - mProgress) + pt : pt;
		float rt = mDirect == Direct.Left ? cw * mProgress + pl : fw - pr;
		float bt = mDirect == Direct.Top ? ch * mProgress + pt : fh - pb;
		
		canvas.drawRect(lf, tp, rt, bt, mPaint);
	}
}
