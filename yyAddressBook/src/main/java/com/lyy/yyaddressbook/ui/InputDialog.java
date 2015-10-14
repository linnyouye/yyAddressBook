package com.lyy.yyaddressbook.ui;

import android.app.Activity;
import android.widget.TextView;

public class InputDialog extends BaseInputDialog {

	private TextView tvTarget;

	public InputDialog(Activity activity) {
		super(activity);

	}

	public void setTargetTv(TextView tv) {
		tvTarget = tv;
		setSrc(tvTarget.getText().toString());
		setOnOkListener(new OnOkListener() {

			@Override
			public void onOk(String result) {
				// TODO Auto-generated method stub
				tvTarget.setText(result);
			}
		});
	}

}
