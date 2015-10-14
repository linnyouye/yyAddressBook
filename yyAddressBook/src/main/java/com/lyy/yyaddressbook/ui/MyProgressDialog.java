package com.lyy.yyaddressbook.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class MyProgressDialog extends ProgressDialog {

    private static final String TAG = "koa--progressDialog";
    private static final boolean D = true;

    private TextView text;
    private String message;

    public MyProgressDialog(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_dialog);

        text = (TextView) findViewById(R.id.progress_dialog_text);
        setContent();
    }

    public void show(String msg) {
        if (isShowing()) {
            dismiss();
        }
        message = msg;
        if (text != null) {
            setContent();
        }

        if (D)
            Log.i(TAG, "显示进度框：" + msg);

        getWindow().setGravity(Gravity.CENTER);

        super.show();
    }

    @Override
    public void show() {
        // TODO Auto-generated method stub
        show(null);
    }

    private void setContent() {
        if (message == null) {
            message = "加载中";
        }

        text.setText(message);
    }

    @Override
    public void dismiss() {
        // TODO Auto-generated method stub
        if (isShowing()) {
            if (D)
                Log.i(TAG, "取消对话框");
            super.dismiss();
        }

    }

    public void show(int msgId) {
        show(getContext().getResources().getString(msgId));
    }

}
