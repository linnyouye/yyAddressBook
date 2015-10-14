package com.lyy.yyaddressbook.ui;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.NumberKeyListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class BaseInputDialog extends Dialog {

	public static final String LETTER = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String NUMBER = "1234567890.";

	private TextView tvTitle, tvTip;
	private Button btnOk, btnCancel;
	private EditText etInput;

	private KeyListener defaultKeyListener;
	private InputDetector inputDetector;
	private OnOkListener onOkListener;

	private Activity activity;
	private String acceptedContent;
	private String src = "" + "";

	public BaseInputDialog(Activity activity) {
		super(activity, R.style.CustomDialog);
		this.activity = activity;

		setContentView(R.layout.input_dialog);

		findAllViewsById();
		initEtInput();
		setOnClickListener();

		defaultKeyListener = etInput.getKeyListener();

		setCanceledOnTouchOutside(false);

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		etInput.setText(src);

		etInput.setSelection(src.length());

		UIHelper.setDialogSize(activity, this);

		UIHelper.setEditTextFocused(etInput);

		if (!TextUtils.isEmpty(acceptedContent)) {
			etInput.setKeyListener(new NumberKeyListener() {

				@Override
				public int getInputType() {
					// TODO Auto-generated method stub
					return InputType.TYPE_CLASS_TEXT;
				}

				@Override
				protected char[] getAcceptedChars() {
					// TODO Auto-generated method stub
					return acceptedContent.toCharArray();
				}

			});
		} else {
			etInput.setKeyListener(defaultKeyListener);
		}

	}

	public BaseInputDialog setInputDetector(InputDetector inputDetector) {
		this.inputDetector = inputDetector;
		return this;
	}

	public BaseInputDialog setOnOkListener(OnOkListener onOkListener) {
		this.onOkListener = onOkListener;
		return this;
	}

	private void setOnClickListener() {
		android.view.View.OnClickListener listener = new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				switch (v.getId()) {
				case R.id.btnOk:
					if (onOkListener != null) {
						onOkListener.onOk(etInput.getText().toString());
					}
					break;
				}
				cancel();
			}

		};
		btnOk.setOnClickListener(listener);
		btnCancel.setOnClickListener(listener);
	}

	private void initEtInput() {
		etInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String input = etInput.getText().toString();
				boolean isInputValid = input.length() > 0;
				if (inputDetector != null) {
					isInputValid = isInputValid
							&& inputDetector.isInputValid(input);
				}
				// btnOk.setPressed(!isInputValid);
				btnOk.setClickable(isInputValid);

				btnOk.setAlpha(isInputValid ? 1f : 0.2f);
			}
		});

	}

	public BaseInputDialog setAcceptedContent(String acceptedContent) {
		this.acceptedContent = acceptedContent;
		return this;
	}

	public void setTitle(CharSequence title) {
		// TODO Auto-generated method stub
		if (TextUtils.isEmpty(title)) {
			tvTitle.setVisibility(View.GONE);
		} else {
			tvTitle.setVisibility(View.VISIBLE);
			tvTitle.setText(title);
		}
	}

	public BaseInputDialog setHint(CharSequence hint) {
		etInput.setHint(hint);
		return this;
	}

	public BaseInputDialog setSrc(String src) {
		this.src = src;
		return this;
	}

	private void findAllViewsById() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTip = (TextView) findViewById(R.id.tvTip);
		btnOk = (Button) findViewById(R.id.btnOk);
		btnCancel = (Button) findViewById(R.id.btnCancel);
		etInput = (EditText) findViewById(R.id.etInput);
	}

	public BaseInputDialog reset() {
		setTitle(null);
		src = "";
		inputDetector = null;
		onOkListener = null;
		acceptedContent = null;
		tvTip.setVisibility(View.GONE);
		return this;
	}

	public BaseInputDialog setTipText(String tip) {
		if (TextUtils.isEmpty(tip)) {
			tvTip.setVisibility(View.GONE);
		} else {
			tvTip.setText(tip);
			tvTip.setVisibility(View.VISIBLE);
		}
		return this;
	}

	interface InputDetector {
		public boolean isInputValid(String input);
	}

	interface OnOkListener {
		public void onOk(String result);
	}

}
