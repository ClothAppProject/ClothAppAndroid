package com.clothapp.resources;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import com.clothapp.HomepageActivity;
import com.clothapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.lang.ref.WeakReference;

/**
 * Created by Roberto on 20/01/16.
 */
public class GalleryUtil {


/*
    public void loadBitmap(ParseObject p, ImageView imageView, Resources r) {

        //Bitmap icon = BitmapFactory.decodeResource(r,
        //      R.drawable.logo);

        final String imageKey = String.valueOf(p);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else {

            if (cancelPotentialWork(p, imageView)) {
                //imageView.setImageResource(R.drawable.image_placeholder);

                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(r, null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(p);
            }
        }
    }


    static class AsyncDrawable extends BitmapDrawable {

        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(ParseObject toLoad, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final ParseObject bitmapData = bitmapWorkerTask.pic;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData != toLoad) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

*/
}
