package com.lyy.yyaddressbook.ui;

import com.lyy.yyaddressbook.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class IndexBar extends View {

	private static final int COLOR_TEXT_NORMAL = 0xFF666666;// 普通字符颜色
	private static final int COLOR_TEXT_SELECTED = 0xFF0000dd;// 选中字符颜色
	private static final int COLOR_BG_NORMAL = 0x00000000;// 索引条背景
	private static final int COLOR_BG_PRESSED = 0x11999999;// 被按下时的背景

	// 由于之前的排序算法中认为#号大于字母，所以把#号放在前面
	private static final char[] DEFAULT_INDEX_CHARS = { '#', 'A', 'B', 'C',
			'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
			'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

	private OnIndexListener onIndexListener;

	private Paint textPaint;

	private TextView tvChar;

	// 用于标记当前选中的位置
	private int curSelection;

	public IndexBar(Context context) {
		super(context);
		init();
	}

	public IndexBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public IndexBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setTvChar(TextView tvChar) {
		this.tvChar = tvChar;
	}

	public boolean onTouchEvent(MotionEvent event) {

		// 根据y坐标判断处于哪一个字符
		int newSelection = (int) (event.getY() * DEFAULT_INDEX_CHARS.length / getMeasuredHeight());
		// 防止越界
		newSelection = Math.max(newSelection, 0);
		newSelection = Math.min(newSelection, DEFAULT_INDEX_CHARS.length - 1);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// setBackgroundColor(COLOR_BG_PRESSED);
			setBackgroundResource(R.drawable.index_bar_bg);
			showChar(DEFAULT_INDEX_CHARS[newSelection]);
			invalidate();
		case MotionEvent.ACTION_MOVE:
			if (newSelection != curSelection) {
				// 新字符与原字符不同时才进行重绘
				curSelection = newSelection;
				if (onIndexListener != null) {
					onIndexListener.onIndex(curSelection,
							DEFAULT_INDEX_CHARS[curSelection]);
				}
				showChar(DEFAULT_INDEX_CHARS[curSelection]);
				invalidate();
			}
			break;
		default:
			// setBackgroundColor(COLOR_BG_NORMAL);
			setBackgroundResource(0);
			if (tvChar != null) {
				tvChar.setVisibility(View.GONE);
			}
			break;
		}

		return true;
	}

	protected void onDraw(Canvas canvas) {

		// 根据高度设置字体大小
		float textSize = getMeasuredHeight() / 35f;
		textSize = Math.min(textSize, 22f);
		textPaint.setTextSize(textSize);

		float widthCenter = getMeasuredWidth() * 1f / 2;
		float dtHeight = getMeasuredHeight()
				/ (DEFAULT_INDEX_CHARS.length + 0.5f);

		for (int i = 0; i < DEFAULT_INDEX_CHARS.length; i++) {

			if (i == curSelection) {
				textPaint.setColor(COLOR_TEXT_SELECTED);
			} else {
				textPaint.setColor(COLOR_TEXT_NORMAL);
			}

			canvas.drawText(String.valueOf(DEFAULT_INDEX_CHARS[i]),
					widthCenter, (i + 1) * dtHeight, textPaint);

		}
		super.onDraw(canvas);

	}

	public void setOnIndexListener(OnIndexListener onIndexListener) {
		this.onIndexListener = onIndexListener;
	}

	private void init() {
		textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.CENTER);

	}

	public void setSelection(char selectionChar) {
		for (int i = 0; i < DEFAULT_INDEX_CHARS.length; i++) {
			if (selectionChar == DEFAULT_INDEX_CHARS[i] && i != curSelection) {
				curSelection = i;
				invalidate();
				break;
			}
		}
	}

	interface OnIndexListener {
		public void onIndex(int position, char indexChar);
	}

	// 在TextView中显示当前选中的字母
	private void showChar(char c) {
		if (tvChar != null) {
			tvChar.setText(c + "");
			tvChar.setVisibility(View.VISIBLE);
		}
	}
}