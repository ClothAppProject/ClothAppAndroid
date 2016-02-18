package com.clothapp.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clothapp.home_gallery.HomeMostRecentFragment;
import com.clothapp.home_gallery.HomePopularShopsFragment;
import com.clothapp.home_gallery.HomeTopRatedFragment;

/**
 * Created by jack1 on 18/02/2016.
 */
public class SearchAdapter extends FragmentPagerAdapter {

    String[]titles;

    public SearchAdapter(FragmentManager fm,String[] titles) {
        //passo il fragment manager e i titoli delle tab
        super(fm);
        this.titles=titles;
    }

    //do il titolo alla tab
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public int getCount() {
        return 3;
    }

    //ritorno il fragment della pagina che voglio mostrare
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                // HomeMostRecentFragment
                return new FindUserFragment();
            case 1:
                // HomeRatedFragment
                return new FindClothFragment();
            case 2:
                // HomePopularShopsFragment
                return new FindTagFragment();
        }

        return null;
    }


}
