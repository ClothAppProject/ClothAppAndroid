package com.clothapp.resources;

/**
 * Created by jack1 on 29/01/2016.
 */
import android.content.Context;
import android.graphics.Bitmap;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
public class CircleTransform extends BitmapTransformation {
    public CircleTransform(Context context) {
        super(context);
    }
    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap source, int outWidth, int outHeight) {
        return BitmapUtil.getCircularBitmapImage(source);
    }
    @Override
    public String getId() {
        return "Glide_Circle_Transformation";
    }
}