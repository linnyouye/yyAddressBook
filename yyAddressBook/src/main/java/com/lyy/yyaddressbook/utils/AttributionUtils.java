package com.lyy.yyaddressbook.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * 归属地信息查询，根据号码得到归属地，可以是手机也可以是有区号的固定电话。 采用单例模式，内部采用两个HashMap来实现。
 *
 * @author lyy, 2014-4-20
 *         <p/>
 *         由于归属地信息文件较大，暂时不用此种方式
 */

public class AttributionUtils {

    private static final String FILE_NAME = "attribution.txt";
    private static final String DIVIDER = ":";
    private static AttributionUtils utils;

    private Map<String, String> attributionMap;

    private AttributionUtils() {
    }

    private AttributionUtils(Context context) {
        attributionMap = new HashMap<String, String>();
        AssetManager am = null;
        am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open(FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = null;
            while ((line = br.readLine()) != null) {
                String[] tmp = line.split(DIVIDER);
                if (tmp != null && tmp.length >= 2) {
                    attributionMap.put(tmp[0], tmp[1]);
                }

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                is.close();
                is = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    ;

    public static AttributionUtils getInstance(Context context) {
        if (utils == null) {
            utils = new AttributionUtils(context);
        }
        return utils;
    }

    public String getAttribution(String number) {
        return attributionMap.get(getKeyFromNumber(number));
    }

    private static String getKeyFromNumber(String number) {
        String tmp = number.replaceAll(" ", "").replaceAll("/+86", "");
        if (tmp.length() > 7) {
            return tmp.substring(0, 7);
        } else {
            return "";
        }
    }
}
