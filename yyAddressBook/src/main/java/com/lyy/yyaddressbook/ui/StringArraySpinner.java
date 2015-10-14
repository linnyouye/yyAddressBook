package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class StringArraySpinner extends Spinner {

	private static final String TAG = "lyy--StringArraySpinner";
	private static final boolean D = false;

	private Context context;
	private MyAdapter adapter;
	private List<String> list;
	private Map<String, String> textToShow;

	// private int selection = -1;

	public void setMap(Map<String, String> map) {
		textToShow = map;
		adapter.notifyDataSetChanged();
	}

	public StringArraySpinner(Context context, int mode) {
		super(context, mode);
		// TODO Auto-generated constructor stub
		this.context = context;
		initSpinner();

	}

	public StringArraySpinner(Context context, AttributeSet attrs,
			int defStyle, int mode) {
		super(context, attrs, defStyle, mode);
		// TODO Auto-generated constructor stub
		this.context = context;
		initSpinner();
	}

	public StringArraySpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		this.context = context;
		initSpinner();
	}

	public StringArraySpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context = context;
		initSpinner();
	}

	public StringArraySpinner(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		initSpinner();
	}

	private void initSpinner() {
		list = new ArrayList<String>();
		adapter = new MyAdapter();
		setAdapter(adapter);
	}

	public String getSelectedItem() {
		return list.get(getSelectedItemPosition());
	}

	public int setSelectedItem(String item) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equals(item)) {
				setSelection(i);
//				selection = i;
				return i;
			}
		}
		// selection = -1;
		return -1;
	}

	public void setData(List<String> list) {
		this.list = list;
		adapter.notifyDataSetChanged();
	}

	public void setData(String[] array) {
		list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		adapter.notifyDataSetChanged();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (D)
				Log.i(TAG, "getView��" + list.get(position));

			convertView = LayoutInflater.from(context).inflate(
					R.layout.spinner_item, null);
			TextView text = (TextView) convertView.findViewById(R.id.textView);
			String showText = list.get(position);
			if (textToShow != null && textToShow.get(showText) != null) {
				showText = textToShow.get(showText);
			}
			text.setText(showText);
			if (position == getSelectedItemPosition()) {
				text.setTextColor(getResources().getColor(
						R.color.item_right_text));
			} else {
				text.setTextColor(getResources().getColor(
						R.color.item_left_text));
			}
			return convertView;
		}

	}

}
