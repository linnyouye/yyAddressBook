package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.CallRecord;

public class SearchResultAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<CallRecord> list;
	private Set<CallRecord> set;
	private String searchText;

	public SearchResultAdapter(Context context, Set<CallRecord> set) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.set = set;
		list = new ArrayList<CallRecord>();
		list.addAll(set);
	}

	public void setSearchText(String text) {
		this.searchText = text;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (list == null) {
			return 0;
		} else {
			return list.size();
		}
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
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.search_result_item, null);
			holder = new ViewHolder();
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
			holder.ibtnCall = (ImageButton) convertView
					.findViewById(R.id.ibtnCall);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CallRecord record = list.get(position);
		if (TextUtils.isEmpty(record.name)) {
			holder.tvPhone.setText(record.location);
		} else {
			holder.tvPhone.setText(record.name);
		}
		holder.tvName.setText(UIHelper.markText(record.number, searchText));
		holder.ibtnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHelper.call(context, record.number);
			}
		});
		return convertView;
	}

	static class ViewHolder {
		TextView tvName;
		TextView tvPhone;
		ImageButton ibtnCall;
	}

	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		list.clear();
		list.addAll(set);
		super.notifyDataSetChanged();
	}

}
