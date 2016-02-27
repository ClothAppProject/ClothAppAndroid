package com.clothapp.home_gallery;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


// UN ADATTATORE PER DOMINARLI TUTTI!
public class HomeAdapter extends FragmentPagerAdapter {

    String[]titles;

    public HomeAdapter(FragmentManager fm,String[] titles) {
        //passo il fragment manager e i titoli delle tab
        super(fm);
        this.titles=titles;
    }

    // do il titolo alla tab
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 2;
    }

    // ritorno il fragment della pagina che voglio mostrare
    public Fragment getItem(int position) {
        switch (position) {
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


}
