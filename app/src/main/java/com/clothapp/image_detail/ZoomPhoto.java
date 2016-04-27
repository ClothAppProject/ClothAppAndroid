package com.clothapp.image_detail;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.clothapp.R;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.clothapp.resources.User;
import com.clothapp.search.FilterActivity;
import com.clothapp.settings.SettingsActivity;

import java.net.URI;
import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by jack1 on 17/03/2016.
 */
public class ZoomPhoto extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomphoto);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Uri uri= Uri.parse(getIntent().getStringExtra("url"));

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final ImageView photo= (ImageView) findViewById(R.id.photo);

        Glide.with(getBaseContext())
                .load(uri)
                .listener(new RequestListener<Uri, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        //image loaded, hide the progressbar
                        progressBar.setVisibility(View.INVISIBLE);
                        PhotoViewAttacher photoViewAttacher = new PhotoViewAttacher(photo);
                        return false;
                    }
                })
                .into(photo);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante indietro termino semplicemente l'activity
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


