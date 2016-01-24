package com.clothapp.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by jack1 on 11/01/2016.
 */
public class BitmapUtil {


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

}
