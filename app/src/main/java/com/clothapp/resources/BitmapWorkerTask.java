package com.clothapp.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.lang.ref.WeakReference;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by Roberto on 20/01/16.
 */

class BitmapWorkerTask extends AsyncTask<ParseObject, Void, Bitmap> {
    private final WeakReference<ImageView> imageViewReference;

    ParseObject pic;


    public BitmapWorkerTask(ImageView imageView) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference<ImageView>(imageView);
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(ParseObject... params) {
        //TODO check parameters of the following method
        return decodeSampledBitmap(params[0], 600, 600);

    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (isCancelled()) {
            bitmap = null;
        }

        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            final BitmapWorkerTask bitmapWorkerTask =
                    GalleryUtil.getBitmapWorkerTask(imageView);
            if (this == bitmapWorkerTask && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }

    }




    //private static byte[] bit;
    public Bitmap decodeSampledBitmap(ParseObject photo, int reqWidth, int reqHeight) {

        byte[] bit = null;
        ParseFile imageFile = (ParseFile) photo.get("photo");
        try {
            bit = imageFile.getData();

        } catch (ParseException e) {
            //errore nel reperire gli oggetti Photo dal database
           // check(e.getCode(), view, e.getMessage());
            // TODO CHECK ON ERRORS
            // TODO va passata la view per controllare gli errori di parse
        }

        Bitmap bm = null;
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(path, options);
        BitmapFactory.decodeByteArray(bit, 0, bit.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        //bm = BitmapFactory.decodeFile(path, options);
        bm = BitmapFactory.decodeByteArray(bit, 0, bit.length, options);

        return bm;
    }

    public int calculateInSampleSize(

            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float)height / (float)reqHeight);
            } else {
                inSampleSize = Math.round((float)width / (float)reqWidth);
            }
        }

        return inSampleSize;
    }

}