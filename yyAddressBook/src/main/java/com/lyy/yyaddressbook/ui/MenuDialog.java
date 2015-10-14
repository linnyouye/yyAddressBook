package com.lyy.yyaddressbook.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;

public class MenuDialog extends Dialog {

	private Activity activity;
	private TextView tvTitle;
	private ListView listView;
	private List<String> data;
	private ArrayAdapter<String> adapter;
	private Object obj;

	private OnItemClickListener listener;

	public MenuDialog(Activity activity) {
		super(activity, R.style.CustomDialog);
		this.activity = activity;

		setContentView(R.layout.menu_dialog);

		findAllViewsById();

		initListView();

		UIHelper.setDialogSize(activity, this);

		// setCanceledOnTouchOutside(false);

	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}

	public void setTitle(CharSequence title) {
		tvTitle.setText(title);
	}

	public void setData(List<String> data) {
		this.data.clear();
		this.data.addAll(data);
		adapter.notifyDataSetChanged();
	}

	private void initListView() {
		// TODO Auto-generated method stub
		data = new ArrayList<String>();
		adapter = new ArrayAdapter<String>(activity, R.layout.dialog_list_text,
				data);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onItemClick(obj, position);
				}
				dismiss();
			}

		});

	}

	public void show(CharSequence title, Object obj) {
		tvTitle.setText(title);
		this.obj = obj;
		show();
	}

	private void findAllViewsById() {
		// TODO Auto-generated method stub
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		listView = (ListView) findViewById(R.id.listView);
	}

	interface OnItemClickListener {
		public void onItemClick(Object obj, int position);
	}

}
