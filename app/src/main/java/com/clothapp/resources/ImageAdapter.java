package com.clothapp.resources;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.clothapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by Roberto on 20/01/16.
 */

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    private View view;

    List<ParseObject> pics;

    public ImageAdapter(Context c, List<ParseObject> list, View v) {
        mContext = c;
        view = v;
        pics = list;
    }

    public int getCount() {
        return pics.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(600, 600));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap bm = decodeSampledBitmap(pics.get(position), 600, 600);

        imageView.setImageBitmap(bm);
        return imageView;
    }


    //private static byte[] bit;
    public Bitmap decodeSampledBitmap(ParseObject photo, int reqWidth, int reqHeight) {

        byte[] bit = null;
        ParseFile imageFile = (ParseFile) photo.get("photo");
        try {
            bit = imageFile.getData();

        } catch (ParseException e) {
            //errore nel reperire gli oggetti Photo dal database
            check(e.getCode(), view, e.getMessage());
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
