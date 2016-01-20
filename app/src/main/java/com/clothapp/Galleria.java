package com.clothapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 19/01/16.
 */
public class Galleria extends BaseAdapter {

    private Context mContext;
    List<ParseObject> itemList;
    View vi;

    public Galleria(Context c, List<ParseObject> arr,View v) {
        mContext = c;
        itemList = arr;
        vi = v;
    }

    /*void add(List<ParseObject> photo){
        itemList= List<ParseObject> photo;
    }*/

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(700, 700));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap bm = decodeSampledBitmap(itemList.get(position), 700, 700);

        imageView.setImageBitmap(bm);
        return imageView;
    }

    //private static byte[] bit;
    public Bitmap decodeSampledBitmap(ParseObject photo, int reqWidth, int reqHeight) {

        byte[] bit = null;
        ParseFile imageFile = (ParseFile) photo.get("photo");
        try {
            bit = imageFile.getData();
            /*imageFile.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e==null) {
                        bit = data;
                        Log.d("Query","Immagine ottenuta");
                    }else{
                        Log.d("Query","Eccezione nel richiedere l'immagine");
                    }
                }
            });*/
        } catch (ParseException e) {
            //errore nel reperire gli oggetti Photo dal database
            check(e.getCode(), vi, e.getMessage());
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