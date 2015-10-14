package com.lyy.yyaddressbook.utils;

import android.content.Context;

/**
 * 该类主要用于通过ID获取资源
 *
 * @author lyy
 */
public class ResHelper {

    public static String getString(Context context, int resId,
                                   Object... objects) {
        return context.getResources().getString(resId, objects);
    }

    public static int getColor(Context context, int resId) {
        return context.getResources().getColor(resId);
    }

}
