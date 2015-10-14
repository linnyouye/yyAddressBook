package com.lyy.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

public class SlideSwitch extends View {

	private int WIDTH;
	private int HEIGHT;
	private int POSITION_LEFT;
	private int POSITION_RIGHT;
	private int SLIDER_RADIUS;
	private int mPosition;

	private boolean mIsChecked = false;
	private boolean mIsSliding = false;

	private int COLOR_CHECKED = 0x990000ff;
	private int COLOR_UNCHECKED = 0x99000000;
	private int COLOR_SLIDER = 0xffffffff;

	private Paint mBgPaint;
	private Paint mSliderPaint;

	public SlideSwitch(Context context) {
		super(context);
		// TODO Auto-generated constructor stub

		WIDTH = getWidth();
		HEIGHT = Math.min(getHeight(), getWidth() / 2);

		mBgPaint = new Paint();
		mSliderPaint = new Paint();

	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		int midY = getHeight() / 2;
		mBgPaint.setColor(mIsChecked ? COLOR_CHECKED : COLOR_UNCHECKED);
		canvas.drawCircle(POSITION_LEFT, midY, SLIDER_RADIUS, mBgPaint);
		canvas.drawCircle(POSITION_RIGHT, midY, SLIDER_RADIUS, mBgPaint);
		canvas.drawRect(POSITION_LEFT, (getHeight() - HEIGHT) / 2,
				POSITION_RIGHT, (getHeight() + HEIGHT) / 2, mBgPaint);

		canvas.drawCircle(mPosition, midY, SLIDER_RADIUS - 1, mSliderPaint);

	}

}
