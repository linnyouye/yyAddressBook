package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.utils.ContactUtils;

public class ContactExListAdapter extends BaseExpandableListAdapter {

	private static final String TAG = "lyy-SectionedAdapter";
	private static final boolean D = true;

	private static final char NONEXISTENT_CHAR = 2000;

	private Context context;
	private LayoutInflater inflater;
	private MyApplication application;

	private List<List<Contact<?>>> contactLists;
	private List<Character> charList;
	private List<Contact<?>> checkedList;

	private boolean isNotifing = false;

	private boolean isEditing = false;

	public ContactExListAdapter(Context context) {
		// this.context = context;
		this.inflater = LayoutInflater.from(context);
		application = (MyApplication) ((Activity) context).getApplication();
		contactLists = new ArrayList<List<Contact<?>>>();
		charList = new ArrayList<Character>();
		checkedList = new LinkedList<Contact<?>>();

	}

	public void setData(TreeSet<Contact<?>> contactSet) {

		contactLists.clear();
		charList.clear();
		// ��������ĸ����һ���б�ת��Ϊ�����б�
		char lastChar = NONEXISTENT_CHAR;
		List<Contact<?>> contactList = null;
		for (Contact<?> contact : contactSet) {
			char curChar = contact.pinyin.charAt(0);
			if (lastChar != curChar) {
				if (contactList != null) {
					contactLists.add(contactList);
				}
				charList.add(curChar);
				lastChar = curChar;
				contactList = new ArrayList<Contact<?>>();
			}
			contactList.add(contact);
		}

		if (contactList != null) {
			contactLists.add(contactList);
		}

		notifyDataSetChanged();
	}

	static class GroupViewHolder {
		TextView tvGroup;
	}

	static class ChildViewHolder {
		ImageView imgAvatars;
		ImageView imgCheck;
		TextView tvName;
		TextView tvPhone;
	}

	public int getSelectionFromChar(char indexChar) {
		// TODO Auto-generated method stub
		int selection = 0;
		for (int i = 0; i < charList.size(); i++) {
			if (charList.get(i) == indexChar) {
				return selection;
			} else {
				selection = selection + 1 + contactLists.get(i).size();
			}
		}
		return -1;
	}

	public char getIndexCharFromPosition(int position) {
		for (int i = 0; i < charList.size(); i++) {
			position = position - 1 - contactLists.get(i).size();
			if (position < 0) {
				return charList.get(i);
			}
		}
		return NONEXISTENT_CHAR;
	}

	public int getPosition(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		int position = 0;
		for (int i = 0; i < groupPosition; i++) {
			position = position + 1 + contactLists.get(i).size();
		}
		position = position + 1 + childPosition;
		return position;
	}

	public int getTotalItemCount() {
		int totalCount = 0;
		for (int i = 0; i < getGroupCount(); i++) {
			totalCount += getChildrenCount(i);
		}
		totalCount += getGroupCount();
		return totalCount;
	}

	public void setEditing(boolean isEditing) {
		this.isEditing = isEditing;
		checkedList.clear();
		notifyDataSetChanged();
	}

	/**
	 * �ı�ĳһ���ѡ��״̬
	 */
	public void changeItemStatus(int groupPos, int childPos) {
		Contact<?> contact = contactLists.get(groupPos).get(childPos);
		if (checkedList.contains(contact)) {
			checkedList.remove(contact);
		} else {
			checkedList.add(contact);
		}
		notifyDataSetChanged();
	}

	/**
	 * ȫѡ��ȫ��ȡ��
	 */
	public void changeAllItemsStatus(boolean isChecked) {
		checkedList.clear();
		if (isChecked) {
			for (int i = 0; i < contactLists.size(); i++) {
				checkedList.addAll(contactLists.get(i));
			}
		}
		notifyDataSetChanged();
	}

	/**
	 * ��ȡѡ�е���ϵ���б�
	 */
	public List<Contact<?>> getCheckedContacts() {
		return checkedList;
	}

	/**
	 * �ӳٵ��� notifyDataSetChanged() ����
	 */
	public void updateData() {

		if (!isNotifing) {
			isNotifing = true;

			new AsyncTask<Void, Integer, Integer>() {

				@Override
				protected void onPostExecute(Integer result) {
					// TODO Auto-generated method stub
					super.onPostExecute(result);

					notifyDataSetChanged();
					isNotifing = false;
				}

				@Override
				protected Integer doInBackground(Void... params) {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
				}

			}.execute();
		}

	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		if (contactLists == null) {
			return 0;
		} else {
			return contactLists.size();
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		if (contactLists == null) {
			return 0;
		} else if (contactLists.get(groupPosition) == null) {
			return 0;
		} else {
			return contactLists.get(groupPosition).size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		if (contactLists == null || contactLists.size() <= groupPosition) {
			return null;
		}
		if (contactLists.get(groupPosition) == null
				|| contactLists.get(groupPosition).size() <= childPosition) {
			return null;
		}
		return contactLists.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		GroupViewHolder holder = null;
		if (convertView == null) {
			holder = new GroupViewHolder();
			convertView = inflater.inflate(R.layout.group_view, null);
			holder.tvGroup = (TextView) convertView.findViewById(R.id.textView);
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		holder.tvGroup.setText(charList.get(groupPosition) + "");
		convertView.setClickable(false);
		convertView.setOnClickListener(null);

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();

		ChildViewHolder holder = null;
		if (convertView == null) {
			holder = new ChildViewHolder();
			convertView = inflater.inflate(R.layout.child_view, null);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
			holder.imgAvatars = (ImageView) convertView
					.findViewById(R.id.imgAvatars);
			holder.imgCheck = (ImageView) convertView
					.findViewById(R.id.imgCheck);
			convertView.setTag(holder);
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		}

		Contact<?> contact = contactLists.get(groupPosition).get(childPosition);
		// ��ʾ���ֺͺ��루�����أ�
		holder.tvName.setText(contact.name);
		String phone = ContactUtils.formatPhone(contact.phone);

		// String attribution = contact.attribution;
		// if (!TextUtils.isEmpty(attribution)) {
		// phone += "  " + attribution;
		// }
		holder.tvPhone.setText(phone + "   "
				+ application.getAttribution(contact.phone));

		// ��ʾͷ��

		Bitmap photo = application.getContactAvatars(contact);

		holder.imgAvatars.setBackgroundDrawable(new BitmapDrawable(photo));

		// holder.imgAvatars.setImageResource(AVATARS[new
		// Random().nextInt(10)]);

		// if (photo == null || photo.getByteCount() < 10) {
		// holder.imgAvatars.setImageResource(R.drawable.icon_avatars);
		// } else {
		// holder.imgAvatars.setImageBitmap(photo);
		// }

		// ����ѡ��״̬
		if (isEditing) {
			holder.imgCheck.setVisibility(View.VISIBLE);
			holder.imgCheck
					.setImageResource(checkedList.contains(contact) ? R.drawable.round_cb_checked
							: R.drawable.round_cb_normal);
		} else {
			holder.imgCheck.setVisibility(View.GONE);
		}

		long duration = System.currentTimeMillis() - startTime;
		if (duration > 10) {

			if (D)
				Log.i(TAG, "getView time:" + groupPosition + ":"
						+ childPosition + ":" + duration);
		}

		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

}
