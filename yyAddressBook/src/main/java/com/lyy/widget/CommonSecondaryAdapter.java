package com.lyy.widget;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

public abstract class CommonSecondaryAdapter<G, C> extends
		BaseExpandableListAdapter {

	private LayoutInflater mInflater;
	private Context mContext;
	private List<G> mGroupList;
	private List<List<C>> mChildLists;
	private final int mGroupLayoutId;
	private final int mChildLayoutId;

	public CommonSecondaryAdapter(Context context, List<G> groupList,
			List<List<C>> childLists, int groupLayoutId, int childLayoutId) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		mGroupList = groupList;
		mChildLists = childLists;
		mGroupLayoutId = groupLayoutId;
		mChildLayoutId = childLayoutId;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (mGroupList == null) {
			return 0;
		} else {
			return mGroupList.size();
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		try {
			return mChildLists.get(groupPosition).size();
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public G getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		try {
			return mGroupList.get(groupPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public C getChild(int groupPosition, int childPosition) {

		// TODO Auto-generated method stub
		try {
			return mChildLists.get(groupPosition).get(childPosition);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder = ViewHolder.get(mContext, convertView,
				parent, mGroupLayoutId, groupPosition);
		convertGroupItem(viewHolder, getGroup(groupPosition));
		return viewHolder.getConvertView();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final ViewHolder viewHolder = ViewHolder.get(mContext, convertView,
				parent, mChildLayoutId, groupPosition);
		convertChildItem(viewHolder, getChild(groupPosition, childPosition));
		return viewHolder.getConvertView();
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	public abstract void convertGroupItem(ViewHolder helper, G item);

	public abstract void convertChildItem(ViewHolder helper, C item);
}
