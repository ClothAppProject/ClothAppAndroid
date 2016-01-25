package com.clothapp.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import javax.microedition.khronos.opengles.GL10;

import static android.support.v4.graphics.BitmapCompat.getAllocationByteCount;

/**
 * Created by jack1 on 11/01/2016.
 */
public class BitmapUtil {

    // static fields
    /* --------------------------------------- */
    final static long mb5 = (long) (5 * 10e6);
    final static long mb3 = (long) (3 * 10e6);
    final static long mb1 = (long) (1 * 10e6);


    public static Bitmap scala(Bitmap imageBitmap){
        int w=imageBitmap.getWidth();
        int h=imageBitmap.getHeight();
        int maxsize=GL10.GL_MAX_TEXTURE_SIZE;
        System.out.println("w="+w+" h="+h+" maxsize="+maxsize);
        Bitmap scaledBitmap=imageBitmap;

        if (imageBitmap.getWidth() > GL10.GL_MAX_TEXTURE_SIZE ) {

            System.out.println("1");
            float aspect_ratio = ((float) imageBitmap.getWidth()) / ((float) imageBitmap.getHeight());

            int wout=maxsize-1;
            int hout= (int) (wout/aspect_ratio);
            System.out.println("aspect_ratio"+aspect_ratio+" wout"+wout+" hout"+hout);
            scaledBitmap = Bitmap.createScaledBitmap(imageBitmap,(int)wout-1, (int)hout-1,false);

        }

        if(imageBitmap.getHeight()>GL10.GL_MAX_TEXTURE_SIZE){
            System.out.println("2");
            float aspect_ratio = ((float) imageBitmap.getWidth()) / ((float) imageBitmap.getHeight());

            int hout=maxsize-1;
            int wout= (int) (aspect_ratio*hout);
            System.out.println("aspect_ratio"+aspect_ratio+" wout"+wout+" hout"+hout);
            scaledBitmap = Bitmap.createScaledBitmap(imageBitmap,(int)wout-1, (int)hout-1,false);
            return scaledBitmap;
        }

        return scaledBitmap;

    }

    public static int checkToCompress(Bitmap photo) {
        // TODO: getAllocationByteCount non riporta il peso della foto preciso, dice che pesa 64mb quando invece pesa 4,5mb
        Log.d("UploadCameraActivity", "photo to compress is: " + getAllocationByteCount(photo));

        if (getAllocationByteCount(photo) > mb5) return 70;
        else if (getAllocationByteCount(photo) > mb3) return 80;
        else if (getAllocationByteCount(photo) > mb1) return 90;
        else return 100;
    }

}
