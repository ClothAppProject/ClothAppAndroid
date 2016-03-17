package com.clothapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URI;

/**
 * Created by jack1 on 17/03/2016.
 */
public class ZoomPhoto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomphoto);

        Intent i=getIntent();
        Uri uri=i.getData();
        System.out.println(uri);
        ImageView photo= (ImageView) findViewById(R.id.photo);
        Glide.with(getBaseContext())
                .load(uri)
                .into(photo);


    }

}


