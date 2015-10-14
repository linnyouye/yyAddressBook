package com.lyy.yyaddressbook.ui;

import java.util.List;

import android.content.Context;
import android.provider.CallLog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.entity.CallRecord;
import com.lyy.yyaddressbook.utils.DateTimeUtils;

public class DialingRecordAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<CallRecord> data;

	public DialingRecordAdapter(Context context, List<CallRecord> data) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.data = data;
	}

	public void setData(List<CallRecord> data) {
		this.data = data;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (data == null) {
			return 0;
		} else {
			return data.size();
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.dialing_list_item, null);
			holder.ibtnCall = (ImageButton) convertView
					.findViewById(R.id.ibtnCall);
			holder.tvPhone = (TextView) convertView.findViewById(R.id.tvPhone);
			holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvState = (TextView) convertView.findViewById(R.id.tvState);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final CallRecord callRecord = data.get(position);

		holder.tvDate.setText(DateTimeUtils
				.getDateFromMillisecond(callRecord.date));

		switch (callRecord.type) {
		case CallLog.Calls.INCOMING_TYPE:
			holder.tvState.setText(R.string.type_incoming);
			holder.tvState.setBackgroundColor(0x7700ff00);
			break;
		case CallLog.Calls.OUTGOING_TYPE:
			holder.tvState.setText(R.string.type_outgoing);
			holder.tvState.setBackgroundColor(0x770000ff);
			break;
		case CallLog.Calls.MISSED_TYPE:
			holder.tvState.setText(R.string.type_missed);
			holder.tvState.setBackgroundColor(0x77ff0000);
			break;
		default:
			holder.tvState.setText(R.string.type_unknown);
			holder.tvState.setBackgroundColor(0x77000000);
			break;
		}

		if ("-1".equals(callRecord.number)) {
			callRecord.name = context.getResources().getString(
					R.string.private_number);
			callRecord.number = "";
		}

		if (callRecord.name != null) {
			holder.tvPhone.setText(callRecord.number);
			holder.tvName.setText(callRecord.name);
		} else {
			holder.tvPhone.setText(callRecord.location);
			holder.tvName.setText(callRecord.number);
		}

		holder.ibtnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UIHelper.call(context, callRecord.number);
			}
		});

		return convertView;
	}

	static class ViewHolder {
		ImageButton ibtnCall;
		TextView tvState;
		TextView tvName;
		TextView tvPhone;
		TextView tvDate;
	}

}
