package com.lyy.yyaddressbook.entity;

import java.io.Serializable;

import android.net.Uri;
import android.util.Log;

/**
 * 联系人数据类
 *
 * @param <T>
 * @author 林佑业 2014-3-20
 */
public class Contact<T> implements Comparable<T>, Serializable {

    private static final long serialVersionUID = 650561524;
    private static final String TAG = "lyy-Contact";
    private static final boolean D = false;

    public long id;
    public String name;
    public String phone;
    public String pinyin;
    public String avatarsUri;

    public Contact() {

    }

    @Override
    public int compareTo(T another) {
        if (another instanceof Contact) {
            Contact<?> ano = (Contact<?>) another;

            if (ano.name.equals(name) && isPhoneEquals(phone, ano.phone)) {
                // 姓名和号码相同，则认为是重复了
                if (D)
                    Log.i(TAG, name + ":" + phone + ":重复了");
                return 0;
            } else {
                // 通过拼音比较大小，直接使用String类的比较方法，忽略大小写，#号比字母大
                int result = pinyin.compareToIgnoreCase(ano.pinyin);
                if (result == 0) {
                    result = 1;
                }
                return result;
            }

        } else {
            return 0;
        }

    }

    /**
     * 判断两个号码是否相同
     */
    private boolean isPhoneEquals(String phone1, String phone2) {
        // 去掉空格
        phone1 = phone1.replaceAll(" +", "");
        phone2 = phone2.replaceAll(" +", "");
        // 去掉开头的+86
        if (phone1.startsWith("+86")) {
            phone1.replace("\\+86", "");
        }
        if (phone2.startsWith("+86")) {
            phone2.replace("\\+86", "");
        }

        if (phone1.equals(phone2)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", name=" + name + ", phone=" + phone
                + ", avatarsUri=" + avatarsUri + "]";
    }

}
