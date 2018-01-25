package com.e2esp.buxlypaints.visualizer.utils;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.e2esp.buxlypaints.visualizer.R;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Zain on 1/30/2017.
 */

public class Utility {

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public static void showToast(Context context, int stringRes) {
        showToast(context, context.getString(stringRes));
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }

    public static Bitmap drawBitmapOfView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth() , v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }

    public static File saveToStorage(Context context, Bitmap bitmap, String imageName) {
        try {
            File appFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
            if (!appFolder.exists() || !appFolder.isDirectory()) {
                if (!appFolder.mkdir()) {
                    return null;
                }
            }
            String fileName = "Image-" + imageName + ".jpg";
            File file = new File(appFolder, fileName);
            if (file.exists()) file.delete();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return file;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void broadcastGalleryUpdate(Context context, File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(file);
            scanIntent.setData(contentUri);
            context.sendBroadcast(scanIntent);
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
            context.sendBroadcast(intent);
        }
    }

}
