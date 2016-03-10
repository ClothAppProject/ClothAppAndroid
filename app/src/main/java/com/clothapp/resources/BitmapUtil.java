package com.clothapp.resources;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

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
        //Log.d("UploadCameraActivity", "photo to compress is: " + getAllocationByteCount(photo));

        if (getAllocationByteCount(photo) > mb5) return 70;
        else if (getAllocationByteCount(photo) > mb3) return 80;
        else if (getAllocationByteCount(photo) > mb1) return 90;
        else return 100;
    }
    // Funzione che controlla se ruotare l'immagine o no
    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage)  {
        // Prendo i dati exif della foto (comprendono data, orientamento, geolocalizzazione della foto ecc...)
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(selectedImage.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    // Funzione che ruota l'immagine
    public static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();

        return rotatedImg;
    }

    //funzione che ruota l'immagine presa dalla gallery
    public static Bitmap rotateGalleryImage(String picturePath,Bitmap imageBitmap)   {
        try {
            ExifInterface exif = new ExifInterface(picturePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true); // rotating bitmap
        }
        catch (Exception e) {
            //errore nel ruotare l'immagine
        }
        return imageBitmap;
    }

    public static Bitmap getCircularBitmapImage(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;
        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        squaredBitmap.recycle();
        return bitmap;
    }
}
