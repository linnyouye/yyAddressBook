package com.lyy.yyaddressbook.ui;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class ConfirmDialog extends Dialog {

	private TextView tvMsg;
	private Button btnOk, btnCancel;
	private OnConfirmListener listener;

	public ConfirmDialog(Activity activity) {
		this(activity, "", null);
	}

	public ConfirmDialog(Activity activity, String msg, OnConfirmListener l) {
		super(activity, R.style.CustomDialog);
		// TODO Auto-generated constructor stub

		setContentView(R.layout.confirm_dialog);

		setCanceledOnTouchOutside(false);

		findAllViewsById();
		UIHelper.setDialogSize(activity, this);

		tvMsg.setText(msg);
		this.listener = l;
		btnOk.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onConfirm();
				}
				dismiss();
			}
		});
		btnCancel.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

	}

	private void findAllViewsById() {
		tvMsg = (TextView) findViewById(R.id.tvMsg);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
	}

	interface OnConfirmListener {
		public void onConfirm();
	}

	public void setMsg(String msg) {
		tvMsg.setText(msg);
	}

	public void setOnConfirmListener(OnConfirmListener listener) {
		this.listener = listener;
	}
}
