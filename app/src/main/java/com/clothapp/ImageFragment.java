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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);
        photos = (ApplicationSupport) getApplicationContext();
        mPager = (ViewPager) findViewById(R.id.pager);
        //creo adattatore da pasare al ViewPager
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(), photos.getPhotos().size());
        mPager.setAdapter(mPagerAdapter);

        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra("position", -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private int npages;

        public ScreenSlidePagerAdapter(FragmentManager fm, int npages) {
            super(fm);
            this.npages = npages;
        }

        @Override
        public Fragment getItem(int position) {
            return new ImageDetailFragment().newInstance(photos.getId(position));
        }

        @Override
        public int getCount() {
            return npages;
        }
    }
}
