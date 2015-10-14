package com.lyy.yyaddressbook.utils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xmlpull.v1.XmlPullParser;

import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import com.lyy.yyaddressbook.entity.Attribution;

public class HtmlParser {

    private static final String TAG = "lyy-HtmlParser";
    private static final boolean D = true;

    /**
     * 从html代码里解析出手机归属地
     *
     * @param html
     * @return
     */
    public static Attribution parseForAttribution(String html) {
        if (TextUtils.isEmpty(html)) {
            return null;
        }

        Attribution attribution = new Attribution();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByClass("tdc2");

        try {
            String phone = elements.get(0).text().split(" ")[0];
            String city = elements.get(1).text();
            String type = elements.get(2).text();

            attribution.setPhone(phone);
            attribution.setCity(city.replaceAll("" + (char) 160, "").replace(
                    "市", ""));

            if (type.contains(Attribution.YIDONG)) {
                type = Attribution.YIDONG;
            } else if (type.contains(Attribution.LIANTONG)) {
                type = Attribution.LIANTONG;
            } else if (type.contains(Attribution.DIANXIN)) {
                type = Attribution.DIANXIN;
            } else {
                type = "";
            }
            attribution.setType(type);

            if (D)
                Log.i(TAG, phone + ":" + city + ":" + type);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attribution;
    }

}
