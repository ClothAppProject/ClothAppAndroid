package com.clothapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.clothapp.resources.ApplicationSupport;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class ImageFragment extends FragmentActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */

    public static final String position = "extra_image";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;
    private ApplicationSupport photos;
    private View vi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);
        photos = (ApplicationSupport) getApplicationContext();
        ParseQuery<ParseObject> query= new ParseQuery<ParseObject>("Photo");
        query.orderByAscending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    photos.setParseObject(objects);
                    // Instantiate a ViewPager and a PagerAdapter.
                    mPager = (ViewPager) findViewById(R.id.pager);
                    mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),objects.size());
                    mPager.setAdapter(mPagerAdapter);

                    // Set the current item based on the extra passed in to this activity
                   final int extraCurrentItem = getIntent().getIntExtra("position",-1);
                    if (extraCurrentItem != -1) {
                        mPager.setCurrentItem(extraCurrentItem);
                    }

                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });



    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int npages;
        public ScreenSlidePagerAdapter(FragmentManager fm,int npages) {
            super(fm);
            this.npages=npages;
        }

        @Override
        public Fragment getItem(int position) {
            return new ImageDetailFragment().newInstance(photos.getParseObject(position));
        }

        @Override
        public int getCount() {
            return npages;
        }
    }
}

/*
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.ExceptionCheck;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

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
*/