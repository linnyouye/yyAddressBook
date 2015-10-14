package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lyy.widget.PinnedHeaderListView;
import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Constant;
import com.lyy.yyaddressbook.constant.Key;
import com.lyy.yyaddressbook.constant.RequestCode;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.ui.ConfirmDialog.OnConfirmListener;
import com.lyy.yyaddressbook.ui.IndexBar.OnIndexListener;
import com.lyy.yyaddressbook.utils.ResHelper;

public class ContactFragment extends Fragment implements Refreshable {

    private static final String TAG = "lyy-ContactsFragment";
    private static final boolean D = true;

    private MyApplication application;

    private BroadcastReceiver receiver;
    private IntentFilter filter;

    private Activity activity;
    private PinnedHeaderListView phListView;
    private ContactExListAdapter adapter;
    private TreeSet<Contact<?>> contactSet;
    private IndexBar indexBar;

    private List<Group> groupList;

    private TextView tvChar, tvTotalCount;

    private MenuDialog menuDialog;
    private ConfirmDialog confirmDialog;

    private View editBar, bottomBar, topBar;
    private TextView tvCheckedCount;
    private CheckBox cbAll;
    private Button btnCancelEdit;
    private Button btnDelete, btnMove, btnSms;
    private ImageButton ibtnSearch, ibtnAdd;

    private MyProgressDialog progressDialog;

    private boolean isEditing = false;

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        this.activity = activity;
        application = (MyApplication) activity.getApplication();
        contactSet = application.getContactSet();
        groupList = application.getGroupList();
        progressDialog = new MyProgressDialog(activity);
        menuDialog = new MenuDialog(activity);
        confirmDialog = new ConfirmDialog(activity, ResHelper.getString(
                activity, R.string.confirm_delete_contact),
                new OnConfirmListener() {

                    @Override
                    public void onConfirm() {
                        // TODO Auto-generated method stub
                        application.deleteContact(adapter.getCheckedContacts());
                        progressDialog.show(R.string.pm_deleting);
                    }
                });

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
        menuDialog.show(ResHelper.getString(activity, R.string.move_contact),
                null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.fragment_contact, null);
        findAllViewsById(view);
        addFooter();

        initTopBar();
        initBottomBar();
        initEditBar();

        initExListView();
        initIndexBar();
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
                } else if (Constant.DELETE_CONTACT_LIST.equals(action)) {
                    progressDialog.dismiss();
                    setEditing(false);
                    refresh();
                } else if (Constant.CHANGE_CONTACTS_GROUP.equals(action)) {
                    progressDialog.dismiss();
                    setEditing(false);
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

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

    }

    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
        activity.unregisterReceiver(receiver);
    }

    private void initTopBar() {
        // TODO Auto-generated method stub

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

    private void initBottomBar() {
        // TODO Auto-generated method stub

        ibtnAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                startActivityForResult(new Intent(activity,
                        ContactInfoActivity.class), RequestCode.ADD_CONTACT);
            }
        });

        ibtnSearch.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

            }
        });
    }

    private void initEditBar() {
        // TODO Auto-generated method stub

        editBar.bringToFront();

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
                UIHelper.sendSms(activity, phones.substring(1));
                setEditing(false);
            }
        });
    }

    private void addFooter() {
        tvTotalCount = new TextView(activity);
        tvTotalCount.setHeight((int) activity.getResources().getDimension(
                R.dimen.bottom_bar_height));
        tvTotalCount.setGravity(Gravity.CENTER);
        tvTotalCount.setTextSize(17);
        tvTotalCount
                .setTextColor(getResources().getColor(R.color.list_divider));
        tvTotalCount.setFocusable(false);
        tvTotalCount.setClickable(false);
        tvTotalCount.setOnClickListener(null);
        phListView.addFooterView(tvTotalCount);
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

    private void findAllViewsById(View view) {
        phListView = (PinnedHeaderListView) view.findViewById(R.id.phListView);
        indexBar = (IndexBar) view.findViewById(R.id.indexBar);
        tvChar = (TextView) view.findViewById(R.id.tvChar);
        bottomBar = view.findViewById(R.id.bottomActionBar);
        // �༭��
        editBar = view.findViewById(R.id.editBar);
        btnDelete = (Button) view.findViewById(R.id.btnDelete);
        btnMove = (Button) view.findViewById(R.id.btnMove);
        btnSms = (Button) view.findViewById(R.id.btnSms);
        //
        ibtnAdd = (ImageButton) view.findViewById(R.id.ibtnAdd);
        ibtnSearch = (ImageButton) view.findViewById(R.id.ibtnSearch);
        // �༭״̬��
        topBar = view.findViewById(R.id.topBar);
        tvCheckedCount = (TextView) view.findViewById(R.id.tvNum);
        cbAll = (CheckBox) view.findViewById(R.id.cbAll);
        btnCancelEdit = (Button) view.findViewById(R.id.btnCancelEdit);

    }

    private void initExListView() {

        // addYouMiBanner();

        adapter = new ContactExListAdapter(activity);
        tvTotalCount.setText(activity.getResources().getString(
                R.string.contact_num, contactSet.size()));
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
                    Intent intent = new Intent(activity,
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

    public void refresh() {
        // TODO Auto-generated method stub
        tvTotalCount.setText(activity.getResources().getString(
                R.string.contact_num, contactSet.size()));

        contactSet = application.getContactSet();
        adapter.setData(contactSet);
    }

    private void setEditing(final boolean isEditing) {
        tvCheckedCount.setText("");
        cbAll.setChecked(false);
        adapter.setEditing(isEditing);
        if (this.isEditing == isEditing) {
            return;
        }
        this.isEditing = isEditing;

        Animation appearAnim = AnimationUtils.loadAnimation(activity,
                R.anim.translate_up);
        Animation disappearAnim = AnimationUtils.loadAnimation(activity,
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
                    ibtnAdd.setVisibility(View.GONE);
                    ibtnSearch.setVisibility(View.GONE);
                } else {
                    editBar.setVisibility(View.GONE);
                }
            }
        });

        if (isEditing) {

            topBar.setVisibility(View.VISIBLE);

            editBar.setVisibility(View.VISIBLE);
            editBar.startAnimation(appearAnim);
            ibtnAdd.startAnimation(disappearAnim);
            ibtnSearch.startAnimation(disappearAnim);

        } else {

            topBar.setVisibility(View.GONE);

            ibtnAdd.setVisibility(View.VISIBLE);
            ibtnSearch.setVisibility(View.VISIBLE);
            editBar.startAnimation(disappearAnim);
            ibtnAdd.startAnimation(appearAnim);
            ibtnSearch.startAnimation(appearAnim);
        }

    }

    private void showCheckedCount() {
        tvCheckedCount.setText(getResources().getString(R.string.num_checked,
                adapter.getCheckedContacts().size()));
        cbAll.setChecked(adapter.getCheckedContacts().size() == contactSet
                .size());
    }

}
