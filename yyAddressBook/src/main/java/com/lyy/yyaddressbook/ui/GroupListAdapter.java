package com.lyy.yyaddressbook.ui;

import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.Group;

public class GroupListAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<Group> groupList;

	private MyApplication application;

	public GroupListAdapter(Activity activity, List<Group> groupList) {
		inflater = LayoutInflater.from(activity);
		this.groupList = groupList;
		application = (MyApplication) activity.getApplication();
	}

	public void setData(List<Group> groupList) {
		this.groupList = groupList;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (groupList == null) {
			return 0;
		} else {
			return groupList.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return groupList.get(position);
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
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.group_list_item, null);
			holder.tvGroup = (TextView) convertView.findViewById(R.id.tvGroup);
			holder.tvNum = (TextView) convertView.findViewById(R.id.tvNum);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Group group = groupList.get(position);
		holder.tvGroup.setText(group.name);
		holder.tvNum.setText(application.getNumberOfGroup(group.id) + "");

		return convertView;
	}

	static class ViewHolder {
		TextView tvGroup;
		TextView tvNum;
	}
}
