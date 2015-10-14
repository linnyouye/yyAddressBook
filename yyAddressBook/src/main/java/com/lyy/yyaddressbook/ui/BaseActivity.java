package com.lyy.yyaddressbook.ui;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends Activity {

    @Override
    public void onBackPressed() {
        // 如果有需要，可以点击后退关闭插屏广告。
        // if (SpotManager.getInstance(this).disMiss(true)) {
        // return;
        // }
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        // 如果不调用此方法，则按home键的时候会出现图标无法显示的情况。
        // SpotManager.getInstance(this).disMiss(false);

        super.onStop();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
