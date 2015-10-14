package com.lyy.yyaddressbook.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.lyy.clipimage.ClipImageView;
import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.constant.Key;
import com.lyy.yyaddressbook.utils.BitmapUtils;
import com.lyy.yyaddressbook.utils.FileUtils;

public class ClipImageActivity extends BaseActivity {

	private ClipImageView mClipImageView;
	private ImageButton mIbtnOk;
	private Button mBtnBack;
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.clip_image_activity);

		mClipImageView = (ClipImageView) findViewById(R.id.clipImageView);
		mIbtnOk = (ImageButton) findViewById(R.id.ibtnOk);
		mBtnBack = (Button) findViewById(R.id.btnTitle);

		Intent intent = getIntent();
		String path = intent.getStringExtra(Key.IMAGE_PATH);
		float whRatio = intent.getFloatExtra(Key.IMAGE_WHRATIO, 0);

		mClipImageView.setRatio(whRatio);

		mBitmap = BitmapUtils.decodeFile(path, 2 * 1000 * 1000);

		mClipImageView.setImageBitmap(mBitmap);

		mIbtnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FileUtils.deleteAllTmp();
				Bitmap clipBitmap = mClipImageView.clip();

				String imagePath = FileUtils.getPhotoTmp() + "/"
						+ System.currentTimeMillis() + ".jpg";

				BitmapUtils.saveBitmap(clipBitmap, imagePath);
				Intent intent = new Intent();
				intent.putExtra(Key.IMAGE_PATH, imagePath);
				setResult(RESULT_OK, intent);

				finish();
			}
		});

		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

	}
}
