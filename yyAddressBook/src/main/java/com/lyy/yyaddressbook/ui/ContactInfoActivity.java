package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Constant;
import com.lyy.yyaddressbook.constant.Key;
import com.lyy.yyaddressbook.constant.RequestCode;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.local.GroupManager;
import com.lyy.yyaddressbook.ui.ConfirmDialog.OnConfirmListener;
import com.lyy.yyaddressbook.utils.AvatarsUtils;
import com.lyy.yyaddressbook.utils.BitmapUtils;
import com.lyy.yyaddressbook.utils.ResHelper;

public class ContactInfoActivity extends BaseActivity {

    private static final String TAG = "lyy-ContactInfoActivity";
    private static final boolean D = true;

    private MyApplication application;

    private Button btnTitle, btnDelete, btnAd;
    private View nameView, phoneView;
    private TextView tvName, tvPhone;
    private StringArraySpinner spGroup;
    private InputDialog inputDialog;
    private Contact<?> contact;
    private ConfirmDialog deleteDialog;
    private ImageButton ibtnOk, ibtnAvatars, ibtnSms, ibtnCall;

    private MyProgressDialog progressDialog;

    private AvatarsUtils avatarsutils;

    private Bitmap showingBitmap;

    private BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_info_activity);

        findAllViewsById();
        initSpinner();
        setOnClickListener();
        initDialog();
        initUI();
        initBroadcastReceiver();
    }

    private void initBroadcastReceiver() {
        receiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                // TODO Auto-generated method stub
                String action = intent.getAction();
                if (Constant.ADD_CONTACT.equals(action)) {
                    // 添加联系人
                    UIHelper.showToast(ContactInfoActivity.this,
                            R.string.toast_add_contact);
                } else if (Constant.EDIT_CONTACT.equals(action)) {
                    // 编辑联系人
                    UIHelper.showToast(ContactInfoActivity.this,
                            R.string.toast_edit_contact);
                } else if (Constant.DELETE_SINGLE_CONTACT.equals(action)) {
                    // 删除联系人
                    UIHelper.showToast(ContactInfoActivity.this,
                            R.string.toast_delete_contact);
                }

                progressDialog.dismiss();
                finish();

            }

        };

        IntentFilter filter = new IntentFilter();

        filter.addAction(Constant.ADD_CONTACT);
        filter.addAction(Constant.EDIT_CONTACT);
        filter.addAction(Constant.DELETE_SINGLE_CONTACT);

        registerReceiver(receiver, filter);
    }

    private void initDialog() {
        // TODO Auto-generated method stub
        deleteDialog = new ConfirmDialog(this, ResHelper.getString(this,
                R.string.confirm_delete_contact), new OnConfirmListener() {

            @Override
            public void onConfirm() {
                // TODO Auto-generated method stub
                progressDialog.show(R.string.pm_deleting);
                application.deleteContact(contact);
                finish();
                UIHelper.showToast(ContactInfoActivity.this,
                        R.string.toast_delete_contact);
            }
        });
    }

    private void initSpinner() {
        // TODO Auto-generated method stub
        application = (MyApplication) getApplication();
        List<String> groupNameList = new ArrayList<String>();
        Map<String, String> map = new HashMap<String, String>();
        List<Group> groupList = application.getGroupList();
        groupNameList.add(GroupManager.DEFAULT_GROUP_ID);
        map.put(GroupManager.DEFAULT_GROUP_ID,
                ResHelper.getString(this, R.string.default_group_name));
        for (int i = 0; i < groupList.size(); i++) {
            String id = groupList.get(i).id;
            String name = groupList.get(i).name;
            groupNameList.add(id);
            map.put(id, name);
        }

        spGroup.setData(groupNameList);
        spGroup.setMap(map);
    }

    private void initUI() {
        // TODO Auto-generated method stub

        progressDialog = new MyProgressDialog(this);

        BadgeView bv = new BadgeView(this, ibtnAvatars);
        bv.setBackgroundResource(R.drawable.avatars_badge);
        bv.setBadgePosition(BadgeView.POSITION_BOTTOM_RIGHT);
        bv.setBadgeMargin(1);
        bv.setWidth(25);
        bv.setHeight(25);
        bv.show();

        avatarsutils = new AvatarsUtils(this);

        inputDialog = new InputDialog(this);

        getMsgFromIntent();
    }

    private void getMsgFromIntent() {
        Intent intent = getIntent();
        contact = (Contact<?>) intent.getSerializableExtra(Key.CONTACT);
        if (contact == null) {

            String phone = intent.getStringExtra("phone");
            tvPhone.setText(phone == null ? "" : phone);

            btnTitle.setText(R.string.add_contact);

            spGroup.setSelectedItem(ResHelper.getString(this,
                    R.string.default_group_name));

            btnDelete.setVisibility(View.GONE);
            ibtnCall.setVisibility(View.GONE);
            ibtnSms.setVisibility(View.GONE);

        } else {
            btnTitle.setText(R.string.contact_info);
            tvName.setText(contact.name);
            tvPhone.setText(contact.phone);
            spGroup.setSelectedItem(application.getGroupId(contact));
            showingBitmap = avatarsutils.getSrcAvatars(contact);
            if (showingBitmap != null) {
                ibtnAvatars.setImageBitmap(showingBitmap);
            }
            btnDelete.setVisibility(View.VISIBLE);
            ibtnCall.setVisibility(View.VISIBLE);
            ibtnSms.setVisibility(View.VISIBLE);
        }
    }

    private void setOnClickListener() {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = tvName.getText().toString();
                String phone = tvPhone.getText().toString();
                Contact<?> newContact = new Contact();
                newContact.name = name;
                newContact.phone = phone;

                switch (v.getId()) {
                    case R.id.btnTitle:
                        finish();
                        break;
                    case R.id.btnDelete:
                        if (contact != null) {
                            deleteDialog.show();
                        }
                        break;
                    case R.id.nameView:
                        inputDialog.setTargetTv(tvName);
                        inputDialog.setTitle(getResources().getString(
                                R.string.contact_name));
                        inputDialog.setAcceptedContent(null);
                        inputDialog.show();
                        break;
                    case R.id.phoneView:
                        inputDialog.setTargetTv(tvPhone);
                        inputDialog.setTitle(getResources().getString(
                                R.string.contact_phone));
                        inputDialog
                                .setAcceptedContent(BaseInputDialog.NUMBER + "+");
                        inputDialog.show();
                        break;
                    case R.id.ibtnOk:
                        // 姓名或号码为空，则弹出提示
                        if (TextUtils.isEmpty(name)) {
                            UIHelper.showToast(ContactInfoActivity.this,
                                    R.string.toast_name_empty);
                            return;
                        } else if (TextUtils.isEmpty(phone)) {
                            UIHelper.showToast(ContactInfoActivity.this,
                                    R.string.toast_phone_empty);
                            return;
                        }

                        application.addConatctToGroup(newContact,
                                spGroup.getSelectedItem());

                        if (contact == null) {
                            // 添加联系人
                            progressDialog.show(R.string.pm_adding);
                            application.addContact(newContact, showingBitmap);

                        } else {
                            // 编辑联系人
                            progressDialog.show(R.string.pm_saving);
                            application.updateConatct(contact, newContact,
                                    spGroup.getSelectedItem(), showingBitmap);
                        }

                        break;
                    case R.id.ibtnCall:
                        UIHelper.call(ContactInfoActivity.this, phone);
                        break;
                    case R.id.ibtnSms:
                        UIHelper.sendSms(ContactInfoActivity.this, phone);
                        break;
                    case R.id.ibtnAvatars:
                        Intent intent = new Intent(ContactInfoActivity.this,
                                PickImageActivity.class);
                        intent.putExtra(Key.IMAGE_WHRATIO, 1f);
                        startActivityForResult(intent, RequestCode.PICK_IMAGE);
                        break;
                    case R.id.btnAd:
                        // SpotManager.getInstance(ContactInfoActivity.this)
                        // .showSpotAds(ContactInfoActivity.this);
                        // QuMiConnect
                        // .getQumiConnectInstance(ContactInfoActivity.this)
                        // .showPopUpAd(ContactInfoActivity.this);
                        break;
                }
            }
        };

        btnTitle.setOnClickListener(listener);
        nameView.setOnClickListener(listener);
        phoneView.setOnClickListener(listener);
        ibtnOk.setOnClickListener(listener);
        ibtnAvatars.setOnClickListener(listener);
        ibtnCall.setOnClickListener(listener);
        ibtnSms.setOnClickListener(listener);
        btnDelete.setOnClickListener(listener);
        btnAd.setOnClickListener(listener);
    }

    private void findAllViewsById() {
        btnTitle = (Button) findViewById(R.id.btnTitle);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnAd = (Button) findViewById(R.id.btnAd);
        nameView = (LinearLayout) findViewById(R.id.nameView);
        tvName = (TextView) findViewById(R.id.tvName);
        phoneView = (LinearLayout) findViewById(R.id.phoneView);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        ibtnOk = (ImageButton) findViewById(R.id.ibtnOk);
        ibtnAvatars = (ImageButton) findViewById(R.id.ibtnAvatars);
        ibtnSms = (ImageButton) findViewById(R.id.ibtnSms);
        ibtnCall = (ImageButton) findViewById(R.id.ibtnCall);
        spGroup = (StringArraySpinner) findViewById(R.id.spGroup);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.PICK_IMAGE && resultCode == RESULT_OK
                && data != null) {
            String imgPath = data.getStringExtra(Key.IMAGE_PATH);
            BitmapUtils.releaseBitmap(showingBitmap);
            showingBitmap = BitmapUtils.decodeFile(imgPath, 1000 * 1000);
            ibtnAvatars.setImageBitmap(showingBitmap);

        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(receiver);
    }

}
