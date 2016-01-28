package com.clothapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.clothapp.resources.BitmapUtil;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.SquaredImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 23/01/16.
 */
public class ImageFragment extends FragmentActivity {

    ImageView imageView;
    Bitmap imageBitmap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);


        //prendo id della foto
        String url = getIntent().getExtras().getString("url");
        System.out.println(url);
        imageView = (ImageView) findViewById(R.id.image_view_fragment);
        System.out.println(url);
        Picasso
                .with(this)
                .load(url)
                .fit()
          //      .resize(700,700)
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
           //     .transform(new CircleTransform())
                .into(imageView);



    }
}
