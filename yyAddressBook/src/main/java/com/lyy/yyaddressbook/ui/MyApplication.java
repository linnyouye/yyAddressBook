package com.lyy.yyaddressbook.ui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Constant;
import com.lyy.yyaddressbook.entity.Attribution;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.local.AccountManager;
import com.lyy.yyaddressbook.local.AttributionManager;
import com.lyy.yyaddressbook.local.GroupManager;
import com.lyy.yyaddressbook.utils.AvatarsUtils;
import com.lyy.yyaddressbook.utils.ContactUtils;
import com.lyy.yyaddressbook.utils.FileUtils;
import com.lyy.yyaddressbook.utils.HtmlParser;
import com.lyy.yyaddressbook.utils.HttpUtils;

public class MyApplication extends Application {

	private static final String TAG = "lyy-Application";
	private static final boolean D = true;

	// �̳߳�����߳���
	private static final int ATTRIBUTION_THREAD_COUNT = 2;
	private static final int AVATARS_THREAD_COUNT = 1;

	private AccountManager accountManager;
	private GroupManager groupManager;
	private AttributionManager attributionManager;

	private AvatarsUtils avatarsUtils;

	private Bitmap defaultAvatarsBitmap;

	private ExecutorService attributionExecutor, avatarsExecutor;

	// ������ϵ��ͷ��λͼ��map
	private Map<String, Bitmap> avatarsMap;

	private LruCache<String, Bitmap> avatarsCache;

	// �����������ص�map
	private Map<String, String> attributionMap;
	// ������ϵ�˷����map
	private Map<String, String> groupMap;
	// ���������������map
	private Map<String, Integer> numberMap;
	// ����������ϵ��
	private TreeSet<Contact<?>> contactSet;
	// �������з���
	private List<Group> groupList;

	public List<Group> getGroupList() {
		return groupList;
	}

	public TreeSet<Contact<?>> getContactSet() {
		return contactSet;
	}

	public void addGroup(String groupName) {
		String groupId = groupManager.addGroup(groupName);
		Group group = new Group();
		group.id = groupId;
		group.name = groupName;
		groupList.add(group);
	}

	public boolean deleteGroup(Group group) {
		groupList.remove(group);
		// �Ѹ÷����µ����ƶ���Ĭ�Ϸ���
		for (String key : groupMap.keySet()) {
			if (group.id.equals(groupMap.get(key))) {
				groupMap.put(key, GroupManager.DEFAULT_GROUP_ID);
			}
		}

		// �Ѹ÷���ӱ�����ȥ��
		groupManager.deleteGroup(group);

		updateNumberOfGroup();

		return true;
	}

	public boolean updateGroup(Group group) {

		for (Group tmp : groupList) {
			if (tmp.id.equals(group.id)) {
				tmp.name = group.name;
				break;
			}
		}

		return groupManager.updateGroup(group);
	}

	public void addConatctToGroup(Contact<?> contact, String groupId) {
		groupManager.addContactToGroup(contact, groupId);
		groupMap.put(contact.name + contact.phone, groupId);

		updateNumberOfGroup();
	}

	public void addConatctToGroup(final List<Contact<?>> contactList,
			final String groupId) {
		new Thread() {
			public void run() {
				for (Contact<?> contact : contactList) {
					groupManager.addContactToGroup(contact, groupId);
					groupMap.put(contact.name + contact.phone, groupId);

				}
				updateNumberOfGroup();
				sendBroadcast(new Intent(Constant.CHANGE_CONTACTS_GROUP));
			}
		}.start();

	}

	public void updateGroupSequence() {
		groupManager.updateGroupSequence(groupList);
	}

	public void addContact(final Contact<?> contact, final Bitmap avatars) {
		new Thread() {
			public void run() {

				ContactUtils.addContact(MyApplication.this, contact.name,
						contact.phone, avatars);
				updateContactSet();

				avatarsUtils.saveContactAvatars(contact, avatars);

				sendBroadcast(new Intent(Constant.ADD_CONTACT));
			}
		}.start();

	}

	public void deleteContact(final Contact<?> contact) {
		if (D)
			Log.i(TAG, "ɾ���ͻ���" + contact);
		new Thread() {
			public void run() {
				ContactUtils.deleteContact(MyApplication.this, contact);
				boolean result = contactSet.remove(contact);
				if (D)
					Log.i(TAG, "�Ƿ�ɹ�ɾ��:" + result);
				sendBroadcast(new Intent(Constant.DELETE_SINGLE_CONTACT));

				updateNumberOfGroup();
			}
		}.start();

	}

	public void deleteContact(final List<Contact<?>> contactList) {
		new Thread() {
			public void run() {
				for (Contact<?> contact : contactList) {

					ContactUtils.deleteContact(MyApplication.this, contact);
					boolean result = contactSet.remove(contact);
					if (D)
						Log.i(TAG, "�Ƿ�ɹ�ɾ��:" + result);

				}
				sendBroadcast(new Intent(Constant.DELETE_CONTACT_LIST));

				updateNumberOfGroup();
			}
		}.start();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if (D)
			Log.i(TAG, "onCreate");

		FileUtils.init(this);

		// ʵ������Ա����
		attributionExecutor = Executors
				.newFixedThreadPool(ATTRIBUTION_THREAD_COUNT);
		avatarsExecutor = Executors.newFixedThreadPool(AVATARS_THREAD_COUNT);
		accountManager = new AccountManager(this);
		attributionManager = new AttributionManager(this);
		groupManager = new GroupManager(this);
		groupList = new LinkedList<Group>();
		contactSet = new TreeSet<Contact<?>>();
		avatarsUtils = new AvatarsUtils(this);
		avatarsMap = new HashMap<String, Bitmap>();
		attributionMap = new HashMap<String, String>();
		groupMap = new HashMap<String, String>();
		numberMap = new HashMap<String, Integer>();

		defaultAvatarsBitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.icon_user);

		avatarsCache = new LruCache<String, Bitmap>(100);

		new Thread() {
			public void run() {
				try {
					Thread.sleep(5);
				} catch (Exception e) {
					e.printStackTrace();
				}
				loadData();
			}
		}.start();

		initYoumiAd();

		initQumiAd();

	}

	private void initQumiAd() {
		// TODO Auto-generated method stub
		// QuMiConnect.ConnectQuMi(this, "4b278f011c8745ba",
		// "0c3e30594d431bad");
		// QuMiConnect.getQumiConnectInstance(this).initPopAd(this);
	}

	private void initYoumiAd() {

		// AdManager.getInstance(this).init("1a91d578139e79f4",
		// "8ebd68a9f9f007ed", false);
		// // �ռ��û�����
		// AdManager.getInstance(this).setUserDataCollect(true);
		// // ��ʼ���������
		// SpotManager.getInstance(this).loadSpotAds();
		// // 5����Լ��رչ��
		// SpotManager.getInstance(this).setAutoCloseSpot(true);//
		// �����Զ��رղ�������
		// SpotManager.getInstance(this).setCloseTime(5000); // ���ùرղ���ʱ��
	}

	protected void loadData() {
		// TODO Auto-generated method stub
		// ��ȡ�����б�
		groupManager.getGroupList(groupList);
		// ��ȡ��ϵ���б�
		updateContactSet();
		// ��ȡ�����������
		updateNumberOfGroup();
		// ��ȡ����ϵ�����ڷ���
		for (Contact<?> contact : contactSet) {
			String groupId = groupManager.getGroupId(contact);
			groupMap.put(contact.name + contact.phone, groupId);
		}
		// ����Ĭ�Ϸ���
		createDefaultGroups();
	}

	private void createDefaultGroups() {
		// TODO Auto-generated method stub
		if (accountManager.isFirstBoot()) {
			addGroup("同学");
			addGroup("ͬ朋友");
			addGroup("亲人");
			accountManager.setFirstBoot(false);
		}
	}

	private void updateNumberOfGroup() {
		numberMap.clear();
		for (Contact<?> contact : contactSet) {
			String groupId = groupManager.getGroupId(contact);
			numberMap.put(groupId, getNumberOfGroup(groupId) + 1);

			if (D)
				Log.i(TAG, "groupId:" + groupId + ",num:"
						+ getNumberOfGroup(groupId));
		}
		sendBroadcast(new Intent(Constant.NUMBER_OF_GROUP_CHANGED));
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		if (D)
			Log.i(TAG, "onTerminate");
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
		if (D)
			Log.i(TAG, "onLowMemory");
	}

	class AttributionRunnable implements Runnable {

		private String phone;

		public AttributionRunnable(String phone) {
			this.phone = phone;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// �ӱ��ض�ȡ��������Ϣ
			String attributionStr = attributionManager.getAttribution(phone);
			if (!TextUtils.isEmpty(attributionStr)) {
				attributionMap.put(phone, attributionStr);
				sendBroadcast(new Intent(Constant.UPDATE_ATTRIBUTION));
				return;
			}

			// ����û�У���������϶�ȡ�����ѽ�����浽����
			String result = HttpUtils.httpGet(Constant.ATTRIBUTION_URL + phone);
			Attribution attribution = HtmlParser.parseForAttribution(result);

			if (attribution != null
					&& !TextUtils.isEmpty(attribution.getCity())) {
				attributionMap.put(phone,
						attribution.getCity() + attribution.getType());
				attributionManager.addAttribution(phone, attribution.getCity()
						+ attribution.getType());
				sendBroadcast(new Intent(Constant.UPDATE_ATTRIBUTION));
			}
		}
	}

	class AvatarsRunnable implements Runnable {

		private Contact<?> contact;

		public AvatarsRunnable(Contact<?> contact) {
			this.contact = contact;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Bitmap bitmap = avatarsUtils.getSmallAvatars(contact);
			if (bitmap == null) {
				if (D)
					Log.i(TAG, "����ͷ��Ϊ��");
				bitmap = ContactUtils.getContactBitmapFromURI(
						MyApplication.this, contact.avatarsUri);
				if (bitmap != null) {
					if (D)
						Log.i(TAG, contact.name + "--���ݿ�ͷ��Ϊ��");
					avatarsUtils.saveContactAvatars(contact, bitmap);
				}

			}
			avatarsMap.put(contact.name, bitmap);

			avatarsCache.put(contact.name,
					bitmap == null ? defaultAvatarsBitmap : bitmap);

			sendBroadcast(new Intent(Constant.UPDATE_AVATARS));
		}

	}

	private void updateContactSet() {

		contactSet.clear();
		TreeSet<Contact<?>> tmpSet = ContactUtils.getAllContacts(this);
		contactSet.addAll(tmpSet);
		tmpSet.clear();
		tmpSet = null;

		updateNumberOfGroup();

		sendBroadcast(new Intent(Constant.ADD_CONTACT));

	}

	public Bitmap getContactAvatars(Contact<?> contact) {
		String name = contact.name;
		// if (!avatarsMap.containsKey(name)) {
		// avatarsMap.put(name, null);
		// avatarsExecutor.execute(new AvatarsRunnable(contact));
		// }
		// return avatarsMap.get(name);

		if (avatarsCache.get(contact.name) == null) {
			avatarsCache.put(contact.name, defaultAvatarsBitmap);
			avatarsExecutor.execute(new AvatarsRunnable(contact));
		}
		return avatarsCache.get(name);

	}

	private static void recycleBitmaps(Map<String, Bitmap> map) {
		for (String key : map.keySet()) {
			Bitmap bitmap = map.get(key);
			if (bitmap != null && !bitmap.isRecycled()) {
				bitmap.recycle();
				bitmap = null;
			}
		}
	}

	/**
	 * ������ϵ����Ϣ
	 */
	public void updateConatct(final Contact<?> contact,
			final Contact<?> newContact, final String groupId,
			final Bitmap avatars) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				if (!newContact.name.equals(contact.name)
						|| !newContact.phone.equals(contact.phone)) {
					// ��������벻ͬʱ����Ҫ����ɾ�����
					ContactUtils.deleteContact(MyApplication.this, contact);
					ContactUtils.addContact(MyApplication.this,
							newContact.name, newContact.phone, avatars);
					updateContactSet();
				}

				groupManager.addContactToGroup(newContact, groupId);
				groupMap.put(contact.name + contact.phone, groupId);

				avatarsUtils.saveContactAvatars(newContact, avatars);
				avatarsMap.remove(contact.name);
				avatarsCache.remove(contact.name);

				// Bitmap tmp = avatarsMap.get(contact.name);
				Bitmap tmp = avatarsCache.get(contact.name);
				if (tmp != null && !tmp.isRecycled()) {
					tmp.recycle();
					tmp = null;
				}
				sendBroadcast(new Intent(Constant.EDIT_CONTACT));
			}
		}.start();

	}

	/**
	 * ��ȡ��������Ϣ
	 */
	public String getAttribution(String phone) {
		if (!attributionMap.containsKey(phone)) {
			attributionMap.put(phone, "");
			attributionExecutor.execute(new AttributionRunnable(phone));
		}
		return attributionMap.get(phone);
	}

	public String getGroupId(Contact<?> contact) {
		return groupMap.get(contact.name + contact.phone);
	}

	public int getNumberOfGroup(String groupId) {
		Integer num = numberMap.get(groupId);
		if (num == null) {
			return 0;
		} else {
			return num;
		}
	}

}
