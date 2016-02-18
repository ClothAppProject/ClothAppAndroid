package com.clothapp.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import com.clothapp.R;
import com.clothapp.home_gallery.HomeMostRecentFragment;
import com.clothapp.home_gallery.HomePopularShopsFragment;
import com.clothapp.home_gallery.HomeTopRatedFragment;

/**
 * Created by jack1 on 18/02/2016.
 */
public class SearchAdapter extends FragmentPagerAdapter {
    FindUserFragment findUserFragment;
    FindClothFragment findClothFragment;
    FindTagFragment findTagFragment;

    String[]titles;
    String query;
    Context context;

    public SearchAdapter(FragmentManager fm,String[] titles,String query,Context context) {
        //passo il fragment manager e i titoli delle tab
        super(fm);
        this.titles=titles;
        this.query=query;
        this.context=context;
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

                return findUserFragment= (FindUserFragment) new FindUserFragment().newIstance(query,context);
            case 1:

                return findClothFragment= (FindClothFragment) new FindClothFragment().newIstance(query,context);
            case 2:

                return findTagFragment= (FindTagFragment) new FindTagFragment().newIstance(query,context);
        }

        return null;
    }

    public void setQuery(String query) {
        this.query = query;
        if(findUserFragment!=null) findUserFragment.refresh(query);
        if(findClothFragment!=null) findClothFragment.refresh(query);
        if(findTagFragment!=null) findTagFragment.refresh(query);

    }


}
