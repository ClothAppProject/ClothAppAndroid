package com.clothapp.resources;

/**
 * Created by jack1 on 28/01/2016.
 */
import android.graphics.Bitmap;
import com.squareup.picasso.Transformation;
public class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        return BitmapUtil.getCircularBitmapImage(source);
    }
    @Override
    public String key() {
        return "circle-image";
    }
}