package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.CallRecord;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.utils.CallRecordsUtils;
import com.lyy.yyaddressbook.utils.ResHelper;

public class DialingFragment extends Fragment implements Refreshable {

	private static final String TAG = "lyy-DialingFragment";
	private static final boolean D = true;

	private Activity activity;
	private MyApplication application;

	private ListView listView, searchResultLv;
	private Set<CallRecord> resultSet;
	private List<CallRecord> dialingList;
	private DialingRecordAdapter recordAdapter;
	private SearchResultAdapter resultAdapter;

	private static final int[] BTN_ID = { R.id.btn0, R.id.btn1, R.id.btn2,
			R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8,
			R.id.btn9, R.id.btn10, R.id.btn11 };
	private static final String[] BTN_CONTENT = { "0", "1", "2", "3", "4", "5",
			"6", "7", "8", "9", "*", "#" };
	private Button[] buttons = new Button[12];
	private Button btnCall;
	private ImageButton ibtnKeyboardDown, ibtnKeyboardUp, ibtnBackspace;
	private View dialingKeyboard, bottomBar;

	private MenuDialog menuDialog;

	private SoundPool soundPool;
	private int soundID;

	private Handler handler = new Handler();

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = activity;
		application = (MyApplication) activity.getApplication();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_dialing, null);
		findAllViewsById(view);
		setOnClickListener();
		initRecordListView();
		initResultListView();
		initDialog();
		initSoundPool();

		activity.getContentResolver().registerContentObserver(
				CallLog.Calls.CONTENT_URI, true, new ContentObserver(handler) {

					@Override
					public void onChange(boolean selfChange) {
						// TODO Auto-generated method stub
						super.onChange(selfChange);
						if (D)
							Log.i(TAG, "ͨ����¼�����仯");
						handler.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								dialingList = CallRecordsUtils
										.getCallRecordList(activity);
								recordAdapter.setData(dialingList);
							}
						});

					}

				});

		// ����ͬʱ��������
		btnCall.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.toString().length() == 0) {

					listView.setVisibility(View.VISIBLE);
					searchResultLv.setVisibility(View.GONE);

				} else {
					listView.setVisibility(View.GONE);
					searchResultLv.setVisibility(View.VISIBLE);

					resultAdapter.setSearchText(s.toString());
					resultSet.clear();
					for (CallRecord record : dialingList) {
						if (record.number.contains(s.toString())) {
							resultSet.add(record);
						}
					}
					for (Contact<?> contact : application.getContactSet()) {
						if (contact.phone.contains(s.toString())) {
							CallRecord record = new CallRecord();
							record.name = contact.name;
							record.number = contact.phone;
							resultSet.add(record);
						}
					}
					resultAdapter.notifyDataSetChanged();
				}

			}
		});

		return view;
	}

	private void initResultListView() {
		// TODO Auto-generated method stub
		resultSet = new HashSet<CallRecord>();
		resultAdapter = new SearchResultAdapter(activity, resultSet);
		searchResultLv.setAdapter(resultAdapter);

	}

	private void initSoundPool() {
		soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
		soundID = soundPool.load(activity, R.raw.fallbackring, 0);
	}

	private void initDialog() {
		// �˵��Ի���
		menuDialog = new MenuDialog(activity);
		menuDialog.setOnItemClickListener(new MenuDialog.OnItemClickListener() {

			@Override
			public void onItemClick(Object obj, int position) {
				// TODO Auto-generated method stub
				CallRecord callRecord = (CallRecord) obj;
				String phone = callRecord.number;

				switch (position) {
				case 0:
					UIHelper.call(activity, phone);
					break;
				case 1:
					UIHelper.sendSms(activity, phone);
					break;
				case 2:
					Intent intent = new Intent(activity,
							ContactInfoActivity.class);
					intent.putExtra("phone", phone);
					startActivity(intent);
					break;
				}
			}
		});

	}

	private void setOnClickListener() {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String inputSrc = btnCall.getText().toString();
				switch (v.getId()) {
				case R.id.ibtnBackspace:
					if (inputSrc.length() >= 1) {
						btnCall.setText(inputSrc.substring(0,
								inputSrc.length() - 1));
					}
					soundPool.play(soundID, 1, 1, 1, 0, 1);
					break;
				case R.id.ibtnKeyboardDown:
					setKeyboardVisable(false);
					break;
				case R.id.ibtnKeyboardUp:
					setKeyboardVisable(true);
					break;
				case R.id.btnCall:
					if (D)
						Log.i(TAG, "btnCall clicked");
					if (TextUtils.isEmpty(inputSrc)) {
						UIHelper.showToast(activity, R.string.toast_phone_empty);
					} else {
						UIHelper.call(activity, inputSrc);
					}
					break;
				default:
					for (int i = 0; i < BTN_ID.length; i++) {
						if (BTN_ID[i] == v.getId()) {
							btnCall.setText(inputSrc + BTN_CONTENT[i]);
						}
					}
					soundPool.play(soundID, 1, 1, 1, 0, 1);
				}
			}

		};
		ibtnBackspace.setOnClickListener(listener);
		ibtnKeyboardDown.setOnClickListener(listener);
		ibtnKeyboardUp.setOnClickListener(listener);
		btnCall.setOnClickListener(listener);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setOnClickListener(listener);
		}
		dialingKeyboard.setOnClickListener(listener);

		ibtnBackspace.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				btnCall.setText("");
				return false;
			}
		});
	}

	private void findAllViewsById(View view) {

		bottomBar = view.findViewById(R.id.bottomBar);

		listView = (ListView) view.findViewById(R.id.listView);
		searchResultLv = (ListView) view.findViewById(R.id.searchResultLv);

		btnCall = (Button) view.findViewById(R.id.btnCall);
		ibtnBackspace = (ImageButton) view.findViewById(R.id.ibtnBackspace);
		ibtnKeyboardDown = (ImageButton) view
				.findViewById(R.id.ibtnKeyboardDown);
		ibtnKeyboardUp = (ImageButton) view.findViewById(R.id.ibtnKeyboardUp);
		dialingKeyboard = view.findViewById(R.id.dialingKeyboard);
		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = (Button) view.findViewById(BTN_ID[i]);
		}

	}

	private void initRecordListView() {

		// addYouMiBanner();

		dialingList = CallRecordsUtils.getCallRecordList(activity);
		recordAdapter = new DialingRecordAdapter(activity, dialingList);
		listView.setAdapter(recordAdapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				CallRecord callRecord = dialingList.get(position);

				List<String> data = new ArrayList<String>();
				data.add(ResHelper.getString(activity, R.string.dialing_call));
				data.add(ResHelper.getString(activity, R.string.dialing_sms));
				if (TextUtils.isEmpty(callRecord.name)) {
					data.add(ResHelper.getString(activity,
							R.string.dialing_add_contact));
				}
				menuDialog.setData(data);
				data.clear();
				data = null;

				String title = "";
				if (TextUtils.isEmpty(callRecord.name)) {
					title = callRecord.number;
				} else {
					title = callRecord.name + " " + callRecord.number;
				}
				menuDialog.show(title, callRecord);
			}
		});
	}

	private void setKeyboardVisable(final boolean isVisable) {
		// TODO Auto-generated method stub

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
				if (isVisable) {
					ibtnKeyboardUp.setVisibility(View.GONE);
				} else {
					dialingKeyboard.setVisibility(View.GONE);
				}
			}
		});

		if (isVisable) {
			dialingKeyboard.setVisibility(View.VISIBLE);
			dialingKeyboard.startAnimation(appearAnim);
			ibtnKeyboardUp.startAnimation(disappearAnim);

		} else {
			// btnCall.setText("");
			ibtnKeyboardUp.setVisibility(View.VISIBLE);
			dialingKeyboard.startAnimation(disappearAnim);
			ibtnKeyboardUp.startAnimation(appearAnim);
		}
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

}
