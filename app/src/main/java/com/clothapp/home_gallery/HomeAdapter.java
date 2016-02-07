package com.clothapp.home_gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by giacomoceribelli on 01/02/16.
 */

public class HomeAdapter  {



    public static Fragment getItem(int index) {

        switch (index) {
            case 0:
                // HomeMostRecentFragment
                return new HomeMostRecentFragment();
            case 1:
                // HomeRatedFragment
                return new HomeTopRatedFragment();
            case 2:
                // HomePopularShopsFragment
                return new HomePopularShopsFragment();
        }

        return null;
    }


    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }

}
