package com.trevor.showcase.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by trevormillner on 1/11/18.
 */

public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, int targetWidth, int targetHeight) {
        // Read in the dimensions of the image 
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale by
        int sampleSize = 1;
        if (srcHeight > targetHeight || srcWidth > targetWidth) {
            // This way preserves the scale, just shrinks it
            if (srcWidth > srcHeight) {
                sampleSize = Math.round(srcHeight / targetHeight);
            } else {
                sampleSize = Math.round(srcWidth / targetWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    // Alternative to this is to wait until layout pass happens to get exact
    // params, but this is more straightforward (conservatively big estimate)
    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaledBitmap(path, size.x, size.y);
    }
}
