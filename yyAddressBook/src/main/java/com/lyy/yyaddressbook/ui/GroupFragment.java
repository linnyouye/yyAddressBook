package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Constant;
import com.lyy.yyaddressbook.constant.Key;
import com.lyy.yyaddressbook.dslv.DragSortListView;
import com.lyy.yyaddressbook.dslv.DragSortListView.DropListener;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.local.GroupManager;
import com.lyy.yyaddressbook.ui.BaseInputDialog.OnOkListener;
import com.lyy.yyaddressbook.ui.ConfirmDialog.OnConfirmListener;
import com.lyy.yyaddressbook.utils.ResHelper;

public class GroupFragment extends Fragment {

	private static final String TAG = "lyy-GroupFragment";
	private static final boolean D = true;

	private MyApplication application;

	private Activity activity;
	private DragSortListView dsListView;
	private GroupListAdapter adapter;

	private List<Group> groupList;

	private MenuDialog menuDialog;
	private BaseInputDialog addGroupDialog, editGroupDialog;
	private ConfirmDialog deleteGroupDialog;

	private View footerDefaultGroup;
	private TextView tvNumOfDefaultGroup;

	private ImageButton ibtnAddGroup;

	private Group editingGroup;

	private BroadcastReceiver receiver;
	private IntentFilter filter;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
		application = (MyApplication) activity.getApplication();
		initDialog();
	}

	private void initDialog() {
		// �˵��Ի���
		menuDialog = new MenuDialog(activity);
		menuDialog.setOnItemClickListener(new MenuDialog.OnItemClickListener() {

			@Override
			public void onItemClick(Object obj, int position) {
				// TODO Auto-generated method stub
				editingGroup = (Group) obj;

				if (D)
					Log.i(TAG, editingGroup.toString());

				switch (position) {
				case 0:
					if (D)
						Log.i(TAG, "�޸ķ�����");
					editGroupDialog.setSrc(editingGroup.name);
					editGroupDialog.show();
					break;
				case 1:
					if (D)
						Log.i(TAG, "ɾ������");
					deleteGroupDialog.setMsg(ResHelper.getString(activity,
							R.string.confirm_delete_group, editingGroup.name));
					deleteGroupDialog.show();

					break;
				}
			}
		});
		List<String> data = new ArrayList<String>();
		data.add(ResHelper.getString(activity, R.string.edit_group));
		data.add(ResHelper.getString(activity, R.string.delete_group));
		menuDialog.setData(data);
		data.clear();
		data = null;

		// ��ӷ���Ի���
		addGroupDialog = new BaseInputDialog(activity);
		addGroupDialog.setTitle(ResHelper.getString(activity,
				R.string.add_group));
		addGroupDialog.setOnOkListener(new OnOkListener() {

			@Override
			public void onOk(String result) {
				// TODO Auto-generated method stub
				if (!isGroupExisted(result)) {
					application.addGroup(result);
					// groupList = application.getGroupList();
					adapter.notifyDataSetChanged();
					dsListView.setSelection(adapter.getCount());
					dsListView.setSelection(adapter.getCount());
				}

			}
		});

		// �༭����Ի���
		editGroupDialog = new BaseInputDialog(activity);
		editGroupDialog.setTitle(ResHelper.getString(activity,
				R.string.edit_group));
		editGroupDialog.setOnOkListener(new OnOkListener() {

			@Override
			public void onOk(String result) {
				// TODO Auto-generated method stub
				if (result.equals(editingGroup.name)) {

				} else if (!isGroupExisted(result)) {

					editingGroup.name = result;
					application.updateGroup(editingGroup);
					adapter.notifyDataSetChanged();
				}
			}
		});

		// ɾ������Ի���
		deleteGroupDialog = new ConfirmDialog(activity);
		deleteGroupDialog.setOnConfirmListener(new OnConfirmListener() {

			@Override
			public void onConfirm() {
				// TODO Auto-generated method stub
				application.deleteGroup(editingGroup);
				adapter.notifyDataSetChanged();
			}
		});

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_group, null);

		findAllViewsById(view);
		addFooter();
		initDsListView();
		setOnClickListener();
		initBroadcastReceiver();

		activity.registerReceiver(receiver, filter);

		return view;
	}

	private void initBroadcastReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (Constant.NUMBER_OF_GROUP_CHANGED.equals(action)) {
					adapter.notifyDataSetChanged();
					tvNumOfDefaultGroup.setText(application
							.getNumberOfGroup(GroupManager.DEFAULT_GROUP_ID)
							+ "");
					if (D)
						Log.i(TAG, "���·�������");

				}
			}

		};
		filter = new IntentFilter();
		filter.addAction(Constant.NUMBER_OF_GROUP_CHANGED);
	}

	private void setOnClickListener() {
		// TODO Auto-generated method stub
		ibtnAddGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addGroupDialog.show();
			}
		});
	}

	private void findAllViewsById(View view) {
		dsListView = (DragSortListView) view.findViewById(R.id.dsListView);
		ibtnAddGroup = (ImageButton) view.findViewById(R.id.ibtnAddGroup);
	}

	private void initDsListView() {

		dsListView.setVerticalScrollBarEnabled(false);

		groupList = application.getGroupList();
		adapter = new GroupListAdapter(activity, groupList);
		dsListView.setAdapter(adapter);

		dsListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				startGroupedContactActivity(groupList.get(position));
			}
		});

		dsListView.setDropListener(new DropListener() {

			@Override
			public void drop(int from, int to) {
				// TODO Auto-generated method stub
				if (changePosition(groupList, from, to)) {
					adapter.notifyDataSetChanged();
					application.updateGroupSequence();
				}
			}
		});

		dsListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Group group = groupList.get(position);
				menuDialog.show(group.name, group);
				return true;
			}
		});

	}

	private <E> boolean changePosition(List<E> list, int from, int to) {
		if (from == to || list == null || from >= list.size()
				|| to >= list.size()) {
			return false;
		}

		E tmp = list.get(from);
		list.remove(from);
		list.add(to, tmp);
		return true;

	}

	private void addFooter() {

		footerDefaultGroup = activity.getLayoutInflater().inflate(
				R.layout.group_list_item, null);
		TextView tvDefaultGroup = (TextView) footerDefaultGroup
				.findViewById(R.id.tvGroup);
		tvNumOfDefaultGroup = (TextView) footerDefaultGroup
				.findViewById(R.id.tvNum);
		tvDefaultGroup.setText(R.string.default_group_name);
		tvNumOfDefaultGroup.setText(application
				.getNumberOfGroup(GroupManager.DEFAULT_GROUP_ID) + "");
		footerDefaultGroup.findViewById(R.id.drag_handle).setVisibility(
				View.INVISIBLE);
		dsListView.addFooterView(footerDefaultGroup);
		footerDefaultGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Group defaultGroup = new Group();
				defaultGroup.id = GroupManager.DEFAULT_GROUP_ID;
				defaultGroup.name = ResHelper.getString(activity,
						R.string.default_group_name);
				startGroupedContactActivity(defaultGroup);
			}
		});

	}

	private boolean isGroupExisted(String groupName) {
		if (groupList == null) {
			return false;
		}
		for (Group group : groupList) {
			if (group.name.equals(groupName)) {
				UIHelper.showToast(activity, R.string.toast_group_existed);
				return true;
			}
		}
		return false;
	}

	private void startGroupedContactActivity(Group group) {
		Intent intent = new Intent(activity, GroupedContactActivity.class);
		intent.putExtra(Key.GROUP, group);
		startActivity(intent);
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		activity.unregisterReceiver(receiver);
	}

}
