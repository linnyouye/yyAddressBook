package com.lyy.widget;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public class PinnedHeaderListView extends ExpandableListView implements
		OnScrollListener {

	private static final String TAG = "lyy-PinnedHeaderListView";
	private static final boolean D = false;

	private OnScrollListener mOnScroListener;

	private ExpandableListAdapter mAdapter;
	private int mCurrentFloatItem = -1;
	private int mNextFloatItem = -1;

	// ͨ��ListView.getChildAt()�õ����ã����ʵ�ʱ�����View.setVisibility()����
	private View mCurrentFloatView;
	// ͨ��ListView.getChildAt()�õ����ã�������getTop()�ж�mFloatView��ƫ����
	private View mNextFloatView;
	// ͨ��ExpandableListAdapter.getGroupView()�õ���ǰ���������dispatchDraw()�н��л���
	private View mFloatView;

	private int mHeaderOffset;

	private int mWidthMode;

	private List<View> mInvisibleItems;

	public PinnedHeaderListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();

	}

	private void init() {
		// TODO Auto-generated method stub

		super.setOnScrollListener(this);

		setGroupIndicator(null);

		mInvisibleItems = new LinkedList<View>();
	}

	@Override
	public boolean collapseGroup(int groupPos) {
		// TODO Auto-generated method stub
		// return super.collapseGroup(groupPos);
		expandGroup(groupPos);
		return true;
	}

	@Override
	public void setAdapter(ExpandableListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
		mAdapter = adapter;
		mAdapter.registerDataSetObserver(new DataSetObserver() {

			@Override
			public void onChanged() {
				// TODO Auto-generated method stub
				super.onChanged();
				// �Զ�չ��������
				for (int i = 0; i < mAdapter.getGroupCount(); i++) {
					expandGroup(i);
				}
			}

			@Override
			public void onInvalidated() {
				// TODO Auto-generated method stub
				super.onInvalidated();
			}

		});
	}

	public PinnedHeaderListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public PinnedHeaderListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (mFloatView == null) {
			return;
		}

		if (mNextFloatView == null) {
			mHeaderOffset = 0;
		} else {
			mHeaderOffset = mNextFloatView.getTop() - mFloatView.getHeight();
		}

		if (D)
			Log.i(TAG, "HeaderOffset:" + mHeaderOffset);

		int saveCount = canvas.save();

		canvas.translate(0, Math.min(mHeaderOffset, 0));
		canvas.clipRect(0, 0, getWidth(), mFloatView.getMeasuredHeight()); // needed

		mFloatView.draw(canvas);
		canvas.restoreToCount(saveCount);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		if (mOnScroListener != null) {
			mOnScroListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		// TODO Auto-generated method stub
		this.mOnScroListener = l;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (mOnScroListener != null) {
			mOnScroListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}

		if (mAdapter == null) {
			return;
		}

		int currentFoatItem = 0;
		int nextFloatItem = 0;

		for (int i = 0; i < mAdapter.getGroupCount(); i++) {
			currentFoatItem = nextFloatItem;
			if (isGroupExpanded(i)) {
				nextFloatItem = currentFoatItem + mAdapter.getChildrenCount(i)
						+ 1;
			} else {
				nextFloatItem = currentFoatItem + 1;
			}

			if (nextFloatItem > firstVisibleItem) {
				mFloatView = mAdapter.getGroupView(i, true, null, null);
				ensurePinnedHeaderLayout(mFloatView);
				break;
			}

		}

		try {

			if (currentFoatItem == firstVisibleItem) {
				View v = getChildAt(0);
				v.setVisibility(View.INVISIBLE);
				mInvisibleItems.add(v);
			} else {
				for (View v : mInvisibleItems) {
					v.setVisibility(View.VISIBLE);
				}
				mInvisibleItems.clear();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (mCurrentFloatItem != currentFoatItem) {
			// Toast.makeText(getContext(),
			// "curr:" + currentFoatItem + ",next:" + nextFloatItem,
			// Toast.LENGTH_SHORT).show();
			mCurrentFloatItem = currentFoatItem;
			mNextFloatItem = nextFloatItem;
		}
		mNextFloatView = getChildAt(mNextFloatItem - firstVisibleItem);

	}

	private void ensurePinnedHeaderLayout(View header) {

		if (header.isLayoutRequested()) {
			int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
					mWidthMode);

			int heightSpec;
			ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
			if (layoutParams != null && layoutParams.height > 0) {
				heightSpec = MeasureSpec.makeMeasureSpec(layoutParams.height,
						MeasureSpec.EXACTLY);
			} else {
				heightSpec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
			}
			header.measure(widthSpec, heightSpec);
			header.layout(0, 0, header.getMeasuredWidth(),
					header.getMeasuredHeight());
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		mWidthMode = MeasureSpec.getMode(widthMeasureSpec);
	}

}
