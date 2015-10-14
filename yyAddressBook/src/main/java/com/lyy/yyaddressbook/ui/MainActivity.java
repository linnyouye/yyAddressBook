package com.lyy.yyaddressbook.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.utils.ResHelper;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "lyy-MainActivity";
    private static final boolean D = true;

    private IndicativeViewPager viewPager;

    // 定时显示插屏广告
    private Timer timer;
    private ShowAdTask adTask;

    private static final int SHOW_AD = 0x123;
    private static final int CLOSE_AD = 0x234;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_AD:
                    if (D)
                        Log.i(TAG, "显示广告");
                    // SpotManager.getInstance(MainActivity.this).showSpotAds(
                    // MainActivity.this);
                    // QuMiConnect.getQumiConnectInstance(MainActivity.this)
                    // .showPopUpAd(MainActivity.this);

                    break;
                case CLOSE_AD:
                    if (D)
                        Log.i(TAG, "关闭广告");
                    // QuMiConnect.canceldialog();
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (IndicativeViewPager) findViewById(R.id.viewPager);

        viewPager.addPage(ResHelper.getString(this, R.string.tab_dialing),
                new DialingFragment());
        viewPager.addPage(ResHelper.getString(this, R.string.tab_contact),
                new ContactFragment());
        viewPager.addPage(ResHelper.getString(this, R.string.tab_group),
                new GroupFragment());

        Intent intent = getIntent();

        if ("android.intent.action.DIAL".equals(intent.getAction())) {
            viewPager.setCurrentPage(0);
        } else {
            viewPager.setCurrentPage(1);
        }

        // 定时显示广告
        timer = new Timer();
        adTask = new ShowAdTask();
        // timer.schedule(adTask, 15 * 1000, 15 * 1000);

        MobclickAgent.setDebugMode(true);

    }

    @Override
    protected void onDestroy() {
        // SpotManager.getInstance(this).unregisterSceenReceiver();
        timer.cancel();
        adTask.cancel();
        super.onDestroy();
    }

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

    class ShowAdTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            handler.obtainMessage(SHOW_AD).sendToTarget();
            handler.sendEmptyMessageDelayed(CLOSE_AD, 6000);
        }
    }
}
