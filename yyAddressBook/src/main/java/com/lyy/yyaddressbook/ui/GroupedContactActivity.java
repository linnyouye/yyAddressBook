package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.lyy.widget.PinnedHeaderListView;
import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Constant;
import com.lyy.yyaddressbook.constant.Key;
import com.lyy.yyaddressbook.constant.RequestCode;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.local.GroupManager;
import com.lyy.yyaddressbook.ui.ConfirmDialog.OnConfirmListener;
import com.lyy.yyaddressbook.ui.IndexBar.OnIndexListener;
import com.lyy.yyaddressbook.utils.ResHelper;

public class GroupedContactActivity extends BaseActivity {

	private static final String TAG = "lyy-GroupedContactActivity";
	private static final boolean D = true;

	private MyApplication application;

	private TreeSet<Contact<?>> contactSet;

	private List<Group> groupList;

	private boolean isEditing;

	private Group group;

	private BroadcastReceiver receiver;
	private IntentFilter filter;

	private Button btnTitle;
	private IndexBar indexBar;
	private TextView tvChar;
	private PinnedHeaderListView phListView;
	private ContactExListAdapter adapter;

	private MenuDialog menuDialog;
	private ConfirmDialog confirmDialog;

	private MyProgressDialog progressDialog;

	private View topBar, bottomBar, titleBar;
	private CheckBox cbAll;
	private TextView tvCheckedCount;
	private Button btnCancelEdit;
	private Button btnDelete, btnMove, btnSms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.grouped_contact_activity);

		findAllViewsById();

		application = (MyApplication) getApplication();
		groupList = application.getGroupList();
		contactSet = new TreeSet<Contact<?>>();
		getDataFromIntent();

		initIndexBar();
		initExListView();

		titleBar.bringToFront();

		initBottomBar();
		initTopBar();

		initBroadcastReceiver();
		registerReceiver(receiver, filter);

		initDialog();

		refresh();

		btnTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}

	private void initDialog() {
		progressDialog = new MyProgressDialog(this);
		menuDialog = new MenuDialog(this);
		confirmDialog = new ConfirmDialog(this, ResHelper.getString(this,
				R.string.confirm_delete_contact), new OnConfirmListener() {

			@Override
			public void onConfirm() {
				// TODO Auto-generated method stub
				application.deleteContact(adapter.getCheckedContacts());
				progressDialog.show(R.string.pm_deleting);
			}
		});
	}

	private void getDataFromIntent() {

		group = (Group) getIntent().getSerializableExtra(Key.GROUP);
		if (group == null) {
			group = new Group();
			group.id = GroupManager.DEFAULT_GROUP_ID;
			group.name = getResources().getString(R.string.default_group_name);
		}
		btnTitle.setText(group.name);

	}

	private void refresh() {
		contactSet.clear();
		for (Contact<?> contact : application.getContactSet()) {
			if (application.getGroupId(contact).equals(group.id)) {
				contactSet.add(contact);
			}
		}
		adapter.setData(contactSet);
	}

	private void findAllViewsById() {
		btnTitle = (Button) findViewById(R.id.btnTitle);
		indexBar = (IndexBar) findViewById(R.id.indexBar);
		tvChar = (TextView) findViewById(R.id.tvChar);
		phListView = (PinnedHeaderListView) findViewById(R.id.phListView);

		// �༭��
		bottomBar = findViewById(R.id.bottomBar);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnMove = (Button) findViewById(R.id.btnMove);
		btnSms = (Button) findViewById(R.id.btnSms);
		// �༭״̬��
		topBar = findViewById(R.id.topBar);
		tvCheckedCount = (TextView) findViewById(R.id.tvNum);
		cbAll = (CheckBox) findViewById(R.id.cbAll);
		btnCancelEdit = (Button) findViewById(R.id.btnCancelEdit);

		titleBar = findViewById(R.id.titleBar);
	}

	private void initIndexBar() {
		// TODO Auto-generated method stub
		indexBar.setTvChar(tvChar);
		indexBar.setOnIndexListener(new OnIndexListener() {

			@Override
			public void onIndex(int position, char indexChar) {
				// TODO Auto-generated method stub
				// ����ѡ�е���ĸ�������б��setSelection()�����������������Ӧ����
				int selection = adapter.getSelectionFromChar(indexChar);
				if (selection >= 0) {
					phListView.setSelection(selection);
				}
			}
		});
	}

	private void initBroadcastReceiver() {
		receiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if (Constant.UPDATE_AVATARS.equals(action)
						|| Constant.UPDATE_ATTRIBUTION.equals(action)) {
					adapter.updateData();
					if (D)
						Log.i(TAG, "�յ��㲥�������б�");
				} else if (Constant.ADD_CONTACT.equals(action)
						|| Constant.EDIT_CONTACT.equals(action)
						|| Constant.DELETE_SINGLE_CONTACT.equals(action)) {
					if (D)
						Log.i(TAG, "�� ����ϵ���б�ṹ");
					refresh();
				} else if (Constant.CHANGE_CONTACTS_GROUP.equals(action)
						|| Constant.DELETE_CONTACT_LIST.equals(action)) {
					progressDialog.dismiss();
					setEditing(false);
					refresh();
				}
			}

		};
		filter = new IntentFilter();
		filter.addAction(Constant.UPDATE_ATTRIBUTION);
		filter.addAction(Constant.UPDATE_AVATARS);
		filter.addAction(Constant.ADD_CONTACT);
		filter.addAction(Constant.EDIT_CONTACT);
		filter.addAction(Constant.DELETE_SINGLE_CONTACT);
		filter.addAction(Constant.DELETE_CONTACT_LIST);
		filter.addAction(Constant.CHANGE_CONTACTS_GROUP);
	}

	private void initExListView() {

		// addYouMiBanner();

		adapter = new ContactExListAdapter(this);
		phListView.setAdapter(adapter);
		adapter.setData(contactSet);

		phListView.setOnScrollListener(new OnScrollListener() {

			private int lastFirstVisibleItem = -1;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// �����б��������������ѡ�е���ĸ
				if (lastFirstVisibleItem != firstVisibleItem) {
					char indexChar = adapter
							.getIndexCharFromPosition(firstVisibleItem);
					indexBar.setSelection(indexChar);
					lastFirstVisibleItem = firstVisibleItem;
				}
			}
		});

		phListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				Contact<?> contact = (Contact<?>) adapter.getChild(
						groupPosition, childPosition);
				if (contact == null) {
					return false;
				}
				if (!isEditing) {
					Intent intent = new Intent(GroupedContactActivity.this,
							ContactInfoActivity.class);
					intent.putExtra(Key.CONTACT, contact);
					startActivityForResult(intent, RequestCode.EDIT_CONTACT);
				} else {
					adapter.changeItemStatus(groupPosition, childPosition);
					showCheckedCount();

				}
				return false;
			}

		});

		phListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (!isEditing) {
					setEditing(true);
				}

				return true;
			}
		});

	}

	private void initBottomBar() {
		// TODO Auto-generated method stub

		bottomBar.bringToFront();

		btnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				confirmDialog.show();
			}
		});
		btnMove.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showMenuDialog();
			}
		});
		btnSms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phones = "";
				for (Contact<?> contact : adapter.getCheckedContacts()) {
					phones += "," + contact.name + "<" + contact.phone + ">";
				}
				UIHelper.sendSms(GroupedContactActivity.this,
						phones.substring(1));
				setEditing(false);
			}
		});
	}

	private void initTopBar() {
		// TODO Auto-generated method stub

		topBar.bringToFront();

		cbAll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				adapter.changeAllItemsStatus(cbAll.isChecked());
				showCheckedCount();
			}
		});

		btnCancelEdit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setEditing(false);
			}
		});
	}

	private void setEditing(final boolean isEditing) {
		tvCheckedCount.setText("");
		cbAll.setChecked(false);
		adapter.setEditing(isEditing);
		if (this.isEditing == isEditing) {
			return;
		}
		this.isEditing = isEditing;

		Animation appearAnim = AnimationUtils.loadAnimation(this,
				R.anim.translate_up);
		Animation disappearAnim = AnimationUtils.loadAnimation(this,
				R.anim.translate_down);
		disappearAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				if (isEditing) {

				} else {
					topBar.setVisibility(View.GONE);
					bottomBar.setVisibility(View.GONE);
				}
			}
		});

		if (isEditing) {
			btnTitle.setVisibility(View.GONE);
			topBar.setVisibility(View.VISIBLE);
			bottomBar.setVisibility(View.VISIBLE);
			bottomBar.startAnimation(appearAnim);
		} else {
			btnTitle.setVisibility(View.VISIBLE);
			bottomBar.startAnimation(disappearAnim);
		}

	}

	private void showCheckedCount() {
		tvCheckedCount.setText(getResources().getString(R.string.num_checked,
				adapter.getCheckedContacts().size()));
		cbAll.setChecked(adapter.getCheckedContacts().size() == contactSet
				.size());
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private void showMenuDialog() {
		// TODO Auto-generated method stub

		List<String> groupNameList = new ArrayList<String>();
		for (Group group : groupList) {
			groupNameList.add(group.name);
		}
		menuDialog.setOnItemClickListener(new MenuDialog.OnItemClickListener() {

			@Override
			public void onItemClick(Object obj, int position) {
				// TODO Auto-generated method stub
				application.addConatctToGroup(adapter.getCheckedContacts(),
						groupList.get(position).id);
				progressDialog.show(R.string.pm_moving);

			}
		});
		menuDialog.setData(groupNameList);
		menuDialog.show(ResHelper.getString(this, R.string.move_contact), null);
	}

}
