package com.clothapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clothapp.resources.ExceptionCheck;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 23/01/16.
 */
public class ImageFragment extends FragmentActivity {
    DonutProgress donutProgress;
    ImageView imageView;
    TextView username;
    Context mContext;
    View vi;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);
        mContext=this;
        vi = new View(this);
        //prendo id della foto
        String objectId = getIntent().getExtras().getString("objectId");
        imageView = (ImageView) findViewById(R.id.image_view_fragment);
        donutProgress = (DonutProgress) findViewById(R.id.donut_progress);
        username = (TextView) findViewById(R.id.username_photo);

        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> foto, ParseException e) {
                if (e == null) {
                    ParseObject obj = foto.get(0);
                    final String user = obj.getString("user");
                    username.setText(user);
                    username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            i.putExtra("user", user);
                            startActivity(i);
                            finish();
                        }
                    });
                    obj.getParseFile("photo").getFileInBackground(new GetFileCallback() {
                        @Override
                        public void done(File file, ParseException e) {
                            if (e==null)    {
                                donutProgress.setVisibility(View.INVISIBLE);
                                imageView.setVisibility(View.VISIBLE);
                                Glide.with(mContext)
                                        .load(file)
                                        //.fit()
                                        //      .resize(700,700)
                                        //.centerCrop()
                                        //    .placeholder(R.mipmap.gallery_icon)
                                        //     .transform(new CircleTransform())
                                        //    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                        .into(imageView);
                            }else{
                                ExceptionCheck.check(e.getCode(), vi, e.getMessage());
                            }
                        }
                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            donutProgress.setProgress(percentDone);
                        }
                    });
                }
            }
        });

    }
}
