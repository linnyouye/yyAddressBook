package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class IndicativeViewPager extends RelativeLayout {

	private static final String TAG = "lyy-ViewPager";
	private static final boolean D = true;

	private static final int RADIOGROUP_ID = R.id.radio_group;
	private static final int VIEWPAGER_ID = R.id.view_pager;

	private Context context;
	private RadioGroup radioGroup;
	private ViewPager viewPager;
	private ViewPagerAdapter adapter;
	private TextView tvIndicator;

	private List<Fragment> pageList;
	private List<RadioButton> rbList;

	private static final int INDICATOR_HEIGHT = 6;
	private static final int INDICATOR_BG = R.color.tab_indicator;

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		if (D)
			Log.i(TAG, "onWindowFocusChanged");
		if (hasWindowFocus) {
			tvIndicator.setWidth(getMeasuredWidth() / pageList.size());
			tvIndicator.setX(viewPager.getCurrentItem() * getMeasuredWidth()
					/ pageList.size());
		}

	}

	public IndicativeViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public IndicativeViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

		init();
	}

	public IndicativeViewPager(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub

		init();

	}

	private void init() {
		context = getContext();
		addRadioGroup();
		// addDivider();
		addIndicator();
		addViewPager();

		radioGroup.bringToFront();
		tvIndicator.bringToFront();
	}

	private void addViewPager() {
		viewPager = new ViewPager(context);

		LayoutParams vpParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		vpParams.addRule(BELOW, RADIOGROUP_ID);
		viewPager.setLayoutParams(vpParams);
		addView(viewPager);

		pageList = new ArrayList<Fragment>();
		rbList = new ArrayList<RadioButton>();
		adapter = new ViewPagerAdapter(
				((FragmentActivity) context).getSupportFragmentManager());
		viewPager.setId(VIEWPAGER_ID);
		viewPager.setAdapter(adapter);

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				rbList.get(arg0).setChecked(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				tvIndicator.setX(arg0 * tvIndicator.getWidth() + arg2 * 1.0f
						/ rbList.size());
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void addIndicator() {
		// ����ָʾ������������
		tvIndicator = new TextView(context);
		LayoutParams tvParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
				INDICATOR_HEIGHT);
		tvParams.addRule(ALIGN_BOTTOM, RADIOGROUP_ID);
		tvIndicator.setBackgroundColor(getResources().getColor(INDICATOR_BG));
		tvIndicator.setLayoutParams(tvParams);
		addView(tvIndicator);
	}

	private void addRadioGroup() {
		radioGroup = new RadioGroup(context);
		LayoutParams rgParams = new LayoutParams(LayoutParams.MATCH_PARENT,
				(int) getResources().getDimension(R.dimen.title_bar_height));
		radioGroup.setLayoutParams(rgParams);
		radioGroup.setId(RADIOGROUP_ID);
		// radioGroup.setBackgroundColor(getResources().getColor(HEADER_BG));
		radioGroup.setBackgroundResource(R.drawable.title_bar_bg);
		radioGroup.setOrientation(RadioGroup.HORIZONTAL);
		addView(radioGroup);

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				for (int i = 0; i < rbList.size(); i++) {
					if (rbList.get(i).getId() == checkedId) {
						viewPager.setCurrentItem(i, true);
						break;
					}
				}
			}
		});
	}

	public void addPage(String title, Fragment fragment) {

		RadioButton rb = (RadioButton) inflate(context, R.layout.rb_tab, null);

		rb.setText(title);
		android.widget.RadioGroup.LayoutParams params = new android.widget.RadioGroup.LayoutParams(
				0, LayoutParams.MATCH_PARENT);
		params.weight = 1;
		rb.setLayoutParams(params);
		radioGroup.addView(rb);
		rbList.add(rb);
		pageList.add(fragment);
		tvIndicator.setWidth(getMeasuredWidth() / pageList.size());
		adapter.notifyDataSetChanged();

		rbList.get(0).setChecked(true);

	}

	public void setCurrentPage(int page) {
		if (page > 0 && page != viewPager.getCurrentItem()) {
			if (D)
				Log.i(TAG, "viewpager.setcurrentitem:" + page);
			viewPager.setCurrentItem(page, false);
		}
	}

	class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return pageList.get(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if (pageList == null) {
				return 0;
			} else {
				return pageList.size();
			}

		}

	}

}
