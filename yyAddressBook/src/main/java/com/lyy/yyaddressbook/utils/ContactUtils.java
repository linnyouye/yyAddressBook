package com.lyy.yyaddressbook.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import android.annotation.SuppressLint;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import com.lyy.yyaddressbook.entity.Contact;

/**
 * 联系人工具类，提供读取和修改手机联系人等方法
 *
 * @author 林佑业 2014-3-20
 *
 */
public class ContactUtils {

	private static final String TAG = "lyy-ContactUtils";
	private static final boolean D = false;

	/**
	 * 删除联系人，包括手机和SIM卡，根据姓名和号码进行匹配
	 */
	public static boolean deleteContact(Context context, Contact<?> contact) {
		deletePhoneContact(context, contact);
		deleteSimContact(context, contact);
		return true;
	}

	/**
	 * 删除手机联系人
	 */
	private static boolean deletePhoneContact(Context context,
											  Contact<?> contact) {

		if (D)
			Log.i(TAG, "delete contact:" + contact.toString());

		Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID, Phone.NUMBER },
				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { contact.name }, null);
		if (cursor == null) {
			return false;
		}

		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		while (cursor.moveToNext()) {
			long id = cursor
					.getLong(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
			String phone = cursor
					.getString(cursor.getColumnIndex(Phone.NUMBER));
			if (trimPhone(phone).equals(contact.phone)) {
				ops.add(ContentProviderOperation
						.newDelete(
								ContentUris.withAppendedId(
										RawContacts.CONTENT_URI, id)).build());
			}
		}

		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
		} catch (Exception e) {
			e.printStackTrace();
			if (D)
				Log.i(TAG, "delete exception");
			return false;
		}

		cursor.close();
		cursor = null;
		return true;

	}

	/**
	 * 删除SIM卡上的联系人
	 */
	private static boolean deleteSimContact(Context context, Contact<?> contact) {
		Uri uri = Uri.parse("content://icc/adn");
		String where = "tag='" + contact.name + "'";
		where += " AND number='" + contact.phone + "'";
		context.getContentResolver().delete(uri, where, null);
		return true;
	}

	/** 得到排序后的手机通讯录联系人列表 **/
	@SuppressLint("DefaultLocale")
	public static TreeSet<Contact<?>> getAllContacts(Context context) {

		// TreeSet可对联系人进行排序
		TreeSet<Contact<?>> contactSet = new TreeSet<Contact<?>>();

		ContentResolver resolver = context.getContentResolver();
		// 获取手机联系人
		Cursor cursor = resolver.query(Phone.CONTENT_URI, null, null, null,
				null);
		if (cursor != null) {

			if (D)
				Log.i(TAG, "phone uri:" + Phone.CONTENT_URI.toString());
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				if (D)
					Log.i(TAG, cursor.getColumnName(i));
			}

			while (cursor.moveToNext()) {
				String phone = cursor.getString(cursor
						.getColumnIndex(Phone.NUMBER));
				if (phone == null || phone.equals("")) {
					// 号码为空则跳过
					continue;
				} else {
					phone = trimPhone(phone);
				}
				// 读取ID
				long id = cursor.getLong(cursor
						.getColumnIndex(Phone.CONTACT_ID));
				// 读取姓名
				String name = cursor.getString(cursor
						.getColumnIndex(Phone.DISPLAY_NAME));
				// 读取拼音
				String sortKey = cursor.getString(cursor
						.getColumnIndex(Phone.SORT_KEY_ALTERNATIVE));
				// 读取头像URI
				String avatarsUri = ContentUris.withAppendedId(
						ContactsContract.Contacts.CONTENT_URI, id).toString();

				@SuppressWarnings("rawtypes")
				Contact<?> contact = new Contact();
				contact.id = id;
				contact.name = name;
				contact.phone = phone;
				contact.pinyin = getPinyinFromSortKey(sortKey);
				contact.avatarsUri = avatarsUri;

				if (D)
					Log.i(TAG, contact.toString());

				contactSet.add(contact);

			}
			cursor.close();
			cursor = null;
		}

		return contactSet;
	}

	/**
	 * 根据sortKey字符串得到拼音，sortKey的形式为“ 林 LIN 生 SHENG”，所以用空格分隔，再取出双数位
	 *
	 * @param sortKey
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private static String getPinyinFromSortKey(String sortKey) {
		String pinyin = "";
		if (!TextUtils.isEmpty(sortKey)) {
			String[] tmp = sortKey.split(" +");
			for (int i = 0; i < tmp.length; i++) {
				if (i % 2 == 0) {
					pinyin += tmp[i] + " ";
				}
			}
		}

		if (!pinyin.substring(0, 1).matches("[a-zA-Z]")) {
			// 若拼音不以字母开头，则在前面加上#号
			pinyin = "#" + pinyin;
		}

		return pinyin.toUpperCase();
	}

	public static void updateContact(Context context, String oldname,
									 String name, String phone, String email, String website,
									 String organization, String note) {

		Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI,
				new String[] { Data.RAW_CONTACT_ID },

				ContactsContract.Contacts.DISPLAY_NAME + "=?",
				new String[] { oldname }, null);
		cursor.moveToFirst();
		String id = cursor
				.getString(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
		cursor.close();
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(

						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?"
								+ " AND " + Phone.TYPE + "=?",
						new String[] { String.valueOf(id),
								Phone.CONTENT_ITEM_TYPE,
								String.valueOf(Phone.TYPE_HOME) })
				.withValue(Phone.NUMBER, phone).build());

		// 更新email
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?"
								+ " AND " + Email.TYPE + "=?",
						new String[] { String.valueOf(id),
								Email.CONTENT_ITEM_TYPE,
								String.valueOf(Email.TYPE_HOME) })
				.withValue(Email.DATA, email).build());

		// 更新姓名
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?",
						new String[] { String.valueOf(id),
								StructuredName.CONTENT_ITEM_TYPE })
				.withValue(StructuredName.DISPLAY_NAME, name).build());

		// 更新网站
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?",
						new String[] { String.valueOf(id),
								Website.CONTENT_ITEM_TYPE })
				.withValue(Website.URL, website).build());

		// 更新公司
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?",
						new String[] { String.valueOf(id),
								Organization.CONTENT_ITEM_TYPE })
				.withValue(Organization.COMPANY, organization).build());

		// 更新note
		ops.add(ContentProviderOperation
				.newUpdate(ContactsContract.Data.CONTENT_URI)
				.withSelection(
						Data.RAW_CONTACT_ID + "=?" + " AND "
								+ ContactsContract.Data.MIMETYPE + " = ?",
						new String[] { String.valueOf(id),
								Note.CONTENT_ITEM_TYPE })
				.withValue(Note.NOTE, note).build());

		try {
			context.getContentResolver().applyBatch(ContactsContract.AUTHORITY,
					ops);
		} catch (Exception e) {
		}
	}

	public static long addContact(Context context, String name, String phone,
								  Bitmap avator) {

		long id = addContactToPhone(context, name, phone, avator);
		addContactToSIM(context, name, phone);
		return id;

	}

	/**
	 * 添加联系人到手机
	 *
	 * @param context
	 * @param name
	 * @param phone
	 */
	private static long addContactToPhone(Context context, String name,
										  String phone, Bitmap avator) {
		// 首先插入空值，再得到rawContactsId ，用于下面插值
		ContentValues values = new ContentValues();
		// insert a null value
		Uri rawContactUri = context.getContentResolver().insert(
				RawContacts.CONTENT_URI, values);
		long rawContactsId = ContentUris.parseId(rawContactUri);

		if (D)
			Log.i(TAG, "新添加的联系人 raw_id :" + rawContactsId);
		// 往刚才的空记录中插入姓名
		values.clear();
		// A reference to the _ID that this data belongs to
		values.put(StructuredName.RAW_CONTACT_ID, rawContactsId);
		// "CONTENT_ITEM_TYPE" MIME type used when storing this in data table
		values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
		// The name that should be used to display the contact.
		values.put(StructuredName.DISPLAY_NAME, name);
		// values.put(Phone.DATA15, bitmap2Bytes(avator));
		// insert the real values
		context.getContentResolver().insert(Data.CONTENT_URI, values);
		// 插入电话
		values.clear();
		values.put(Phone.RAW_CONTACT_ID, rawContactsId);
		// String "Data.MIMETYPE":The MIME type of the item represented by this
		// row
		// String "CONTENT_ITEM_TYPE": MIME type used when storing this in data
		// table.
		values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
		values.put(Phone.NUMBER, phone);
		// values.put(Phone.DATA15, bitmap2Bytes(avator));

		context.getContentResolver().insert(Data.CONTENT_URI, values);

		// 根据raw_id得到contact_id，用于插入头像
		Cursor cursor = context.getContentResolver().query(rawContactUri, null,
				null, null, null);
		if (cursor.moveToFirst()) {
			long contactId = cursor.getLong(cursor
					.getColumnIndex(Phone.CONTACT_ID));

			// 插入头像
			// if (avator != null) {
			// Uri avatarsUri = ContentUris.withAppendedId(
			// ContactsContract.Contacts.CONTENT_URI, contactId);
			// if (D)
			// Log.i(TAG, avatarsUri.toString());
			// try {
			// OutputStream outStream = context.getContentResolver()
			// .openOutputStream(avatarsUri, "w");
			// avator.compress(Bitmap.CompressFormat.JPEG, 50, outStream);
			// outStream.close();
			// if (D)
			// Log.i(TAG, "已插入头像");
			// } catch (Exception e) {
			// Log.e(TAG, "exception while writing image", e);
			// }
			// }

			if (D)
				Log.i(TAG, "contact id :" + contactId);
		}
		cursor.close();
		// -----------------------
		return rawContactsId;

	}

	/**
	 * 添加联系人到SIM卡
	 *
	 * @param context
	 * @param name
	 * @param phone
	 */
	private static void addContactToSIM(Context context, String name,
										String phone) {
		Uri uri = Uri.parse("content://icc/adn");
		ContentValues values = new ContentValues();
		values.put("tag", name);
		values.put("number", phone);
		Uri newSimContactUri = context.getContentResolver().insert(uri, values);

		if (D)
			Log.i(TAG, "new sim contact uri, " + newSimContactUri.toString());
	}

	private static String trimPhone(String phone) {
		phone = phone.replaceAll(" ", "");
		phone = phone.replaceAll("-", "");
		phone = phone.replaceAll("\\+86", "");
		return phone;
	}

	public static String formatPhone(String phone) {
		// TODO Auto-generated method stub
		// 号码为空或长度小于7，不做处理
		if (phone == null || phone.length() < 7) {
			return phone;
		}

		// 400电话格式为400-000-0000
		if (phone.startsWith("400") && phone.length() > 8) {
			phone = phone.replaceAll("400", "400-");
			phone = phone.substring(0, 7) + "-" + phone.substring(7);
			return phone;
		}

		// 其他号码（包括固话，手机号等）
		int insertPos = 4;
		while (insertPos < phone.length()) {
			phone = phone.substring(0, phone.length() - insertPos) + "-"
					+ phone.substring(phone.length() - insertPos);
			insertPos += 5;
		}
		return phone;
	}

	/**
	 * 根据uri获取头像位图
	 *
	 * @param context
	 * @param uri
	 * @return
	 */
	public static Bitmap getContactBitmapFromURI(Context context, Uri uri) {
		InputStream input = ContactsContract.Contacts
				.openContactPhotoInputStream(context.getContentResolver(), uri);
		if (input == null) {
			return null;
		} else {

			Bitmap bitmap = BitmapFactory.decodeStream(input);
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			input = null;
			return bitmap;
		}
	}

	public static Bitmap getContactBitmapFromURI(Context context, String uri) {
		return getContactBitmapFromURI(context, Uri.parse(uri));
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		if (D)
			Log.i(TAG, "位图大小：" + bm.getByteCount());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 70, baos);
		byte[] bytes = baos.toByteArray();
		if (D)
			Log.i(TAG, "数据大小：" + bytes.length);
		return bytes;
	}
}
