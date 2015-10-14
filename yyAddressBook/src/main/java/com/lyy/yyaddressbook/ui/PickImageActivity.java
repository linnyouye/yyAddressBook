package com.lyy.yyaddressbook.ui;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.lyy.yyaddressbook.R;
import com.lyy.yyaddressbook.utils.FileUtils;

public class PickImageActivity extends Activity {

    private static final String TAG = "lyy-PickImageActivity";
    private static final boolean D = true;

    private static final int SELECT_IMAGE = 0x123;
    private static final int CROP_IMAGE = 0x234;
    private static final int TAKE_PHOTO = 0x456;

    private Button btnSelect;
    private Button btnPhoto;
    private Button btnCancel;

    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.pick_image_activity);

        findAllViewsById();

        setOnClickListener();

        photoPath = FileUtils.getPhotoTmp() + "/photo.jpg";

        System.out.println("比例：" + getIntent().getFloatExtra("whRatio", 0));

    }

    private void setOnClickListener() {
        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnSelect:
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, SELECT_IMAGE);
                        break;
                    case R.id.btnPhoto:

                        new File(photoPath).delete();

                        Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        it.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(photoPath)));
                        startActivityForResult(it, TAKE_PHOTO);

                        break;
                    case R.id.btnCancel:
                        finish();
                        break;
                }
            }
        };

        btnSelect.setOnClickListener(listener);
        btnPhoto.setOnClickListener(listener);
        btnCancel.setOnClickListener(listener);
    }

    private void findAllViewsById() {
        btnSelect = (Button) findViewById(R.id.btnSelect);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnCancel = (Button) findViewById(R.id.btnCancel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SELECT_IMAGE:
                getImageFromUri(data.getData());
                break;
            case TAKE_PHOTO:

                if (new File(photoPath).exists()) {
                    Intent intent = new Intent(this, ClipImageActivity.class);
                    intent.putExtra("path", photoPath);
                    intent.putExtra("whRatio",
                            getIntent().getFloatExtra("whRatio", 0));
                    startActivityForResult(intent, CROP_IMAGE);
                    return;
                }

                Uri uri = data.getData();
                if (uri != null) {
                    getImageFromUri(uri);
                } else {
                    String photoPath = FileUtils.getPhotoTmp() + "/"
                            + System.currentTimeMillis() + ".jpg";

                    try {
                        FileOutputStream fos = new FileOutputStream(photoPath);
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);// 把数据写入文件
                        fos.flush();
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(this, ClipImageActivity.class);
                    intent.putExtra("path", photoPath);
                    intent.putExtra("size", (int) new File(photoPath).length());
                    intent.putExtra("whRatio",
                            getIntent().getFloatExtra("whRatio", 0));
                    startActivityForResult(intent, CROP_IMAGE);
                }
                break;
            case CROP_IMAGE:

                setResult(RESULT_OK, data);
                finish();

                break;

        }
    }

    private void getImageFromUri(Uri uri) {

        if (!TextUtils.isEmpty(uri.getAuthority())) {

            if (D)
                Log.i(TAG, "image uri:" + uri.toString());

            Cursor cursor = getContentResolver().query(uri, null, null, null,
                    null);
            if (null == cursor) {
                Toast.makeText(this, "no found", Toast.LENGTH_SHORT).show();
                return;
            }
            cursor.moveToFirst();
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Images.Media.DATA));
            int size = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Images.Media.SIZE));
            // int width = cursor.getInt(cursor
            // .getColumnIndex(MediaStore.Images.Media.WIDTH));
            // int height = cursor.getInt(cursor
            // .getColumnIndex(MediaStore.Images.Media.HEIGHT));
            Intent intent = new Intent(this, ClipImageActivity.class);
            // if (D)
            // Log.i(TAG, "图片大小：" + width + "X" + height);
            // intent.putExtra("width", width);
            // intent.putExtra("height", height);
            intent.putExtra("path", path);
            intent.putExtra("size", size);
            intent.putExtra("whRatio", getIntent().getFloatExtra("whRatio", 0));
            startActivityForResult(intent, CROP_IMAGE);

            cursor.close();
            cursor = null;
        }
    }

}
