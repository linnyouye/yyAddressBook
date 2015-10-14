package com.lyy.yyaddressbook.local;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.Contact;
import com.lyy.yyaddressbook.entity.Group;
import com.lyy.yyaddressbook.utils.DESUtils;
import com.lyy.yyaddressbook.utils.ResHelper;

public class GroupManager {

    private static final String FILE_NAME = "contact_group";

    public static final String DEFAULT_GROUP_ID = "111111";
    private String defaultGroupName;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String KEY_MAX_ID = DESUtils.encrypt("max_id");
    private static final String KEY_GROUP_IDS = DESUtils.encrypt("group_ids");
    private static final String EMPTY_STR = "#&%";
    private static final String ENCRYPTED_EMPTY_STR = DESUtils
            .encrypt(EMPTY_STR);
    private static final String GROUP_DIVIDER = "#@#";

    private int maxId;

    public GroupManager(Context context) {
        preferences = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        defaultGroupName = ResHelper.getString(context,
                R.string.default_group_name);
        editor = preferences.edit();
        editor.apply();
        maxId = preferences.getInt(KEY_MAX_ID, 125814);

    }

    /**
     * 添加分组
     *
     * @param groupName
     * @return 组的id
     */
    public String addGroup(String groupName) {
        maxId++;
        String groupIds = DESUtils.decrypt(preferences.getString(KEY_GROUP_IDS,
                ENCRYPTED_EMPTY_STR));
        if (EMPTY_STR.equals(groupIds)) {
            groupIds = "";
        }
        groupIds = groupIds + maxId + GROUP_DIVIDER;
        editor.putString(KEY_GROUP_IDS, DESUtils.encrypt(groupIds));
        editor.putString(DESUtils.encrypt(maxId + ""),
                DESUtils.encrypt(groupName));
        editor.putInt(KEY_MAX_ID, maxId);
        editor.commit();
        return maxId + "";
    }

    public void addContactToGroup(Contact<?> contact, String groupId) {
        editor.putString(DESUtils.encrypt(contact.name + contact.phone),
                DESUtils.encrypt(groupId));
        editor.commit();
    }

    public String getGroupId(Contact<?> contact) {
        String groupId = preferences.getString(
                DESUtils.encrypt(contact.name + contact.phone),
                DESUtils.encrypt(DEFAULT_GROUP_ID));
        groupId = DESUtils.decrypt(groupId);
        if (DESUtils.decrypt(
                preferences.getString(KEY_GROUP_IDS, ENCRYPTED_EMPTY_STR))
                .contains(groupId + GROUP_DIVIDER)) {
            return groupId;
        } else {
            return DEFAULT_GROUP_ID;
        }
    }

    public String getGroupName(String groupId) {
        String groupName = preferences.getString(DESUtils.encrypt(groupId),
                DESUtils.encrypt(defaultGroupName));

        return DESUtils.decrypt(groupName);
    }

    public boolean getGroupList(List<Group> list) {

        if (list == null) {
            return false;
        }

        String groupIds = DESUtils.decrypt(preferences.getString(KEY_GROUP_IDS,
                ENCRYPTED_EMPTY_STR));
        if (groupIds.equals(EMPTY_STR)) {
            return true;
        }

        String[] groupArray = groupIds.split(GROUP_DIVIDER);

        for (int i = 0; i < groupArray.length
                && !TextUtils.isEmpty(groupArray[0]); i++) {
            Group group = new Group();
            group.id = groupArray[i];
            group.name = DESUtils.decrypt(preferences.getString(
                    DESUtils.encrypt(group.id), ENCRYPTED_EMPTY_STR));
            list.add(group);
        }
        return true;
    }

    public void updateGroupSequence(List<Group> list) {
        // TODO Auto-generated method stub
        String groupIds = "";
        for (int i = 0; i < list.size(); i++) {
            String id = list.get(i).id;
            if (!id.equals(DEFAULT_GROUP_ID)) {
                groupIds += list.get(i).id + GROUP_DIVIDER;
            }
        }
        editor.putString(KEY_GROUP_IDS, DESUtils.encrypt(groupIds));
        editor.commit();
    }

    public boolean updateGroup(Group group) {
        // TODO Auto-generated method stub
        editor.putString(DESUtils.encrypt(group.id),
                DESUtils.encrypt(group.name));
        editor.commit();
        return true;
    }

    public boolean deleteGroup(Group group) {
        // TODO Auto-generated method stub
        String groupIds = DESUtils.decrypt(preferences.getString(KEY_GROUP_IDS,
                ENCRYPTED_EMPTY_STR));
        if (!groupIds.contains(group.id)) {
            return false;
        } else {
            groupIds = deleteSubString(groupIds, group.id + GROUP_DIVIDER);

            editor.putString(KEY_GROUP_IDS, DESUtils.encrypt(groupIds));
            editor.putString(DESUtils.encrypt(group.id), ENCRYPTED_EMPTY_STR);
            editor.commit();
        }
        return true;
    }

    private String deleteSubString(String src, String sub) {
        if (src.contains(sub)) {
            return src.substring(0, src.indexOf(sub))
                    + src.substring(src.indexOf(sub) + sub.length());
        } else {
            return src;
        }
    }
}
