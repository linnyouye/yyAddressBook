package com.lyy.yyaddressbook.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import com.lyy.yyaddressbook.entity.Contact;

public class AvatarsUtils {

    private String avatarsDir;
    private static final String AVATARS_DIR_NAME = "联系人头像";

    public AvatarsUtils(Context context) {
        avatarsDir = context.getFilesDir().getPath() + "/" + AVATARS_DIR_NAME;
        File file = new File(avatarsDir);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    public void saveContactAvatars(Contact<?> contact, Bitmap avatars) {
        if (avatars == null) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(avatarsDir + "/" + contact.name);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (fos != null) {
            avatars.compress(CompressFormat.JPEG, 75, fos);
        }
    }

    public Bitmap getSmallAvatars(Contact<?> contact) {
        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inSampleSize = 2;
        return BitmapFactory.decodeFile(avatarsDir + "/" + contact.name,
                options);
    }

    public Bitmap getSrcAvatars(Contact<?> contact) {
        Options options = new Options();
        options.inJustDecodeBounds = false;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inPurgeable = true;
        options.inInputShareable = true;
        return BitmapFactory.decodeFile(avatarsDir + "/" + contact.name,
                options);
    }

}
