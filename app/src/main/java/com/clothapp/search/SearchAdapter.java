package com.clothapp.search;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by jack1 on 18/02/2016.
 */
public class SearchAdapter extends FragmentPagerAdapter {

    FindUserFragment findUserFragment;
    FindClothFragment findClothFragment;
    FindTagFragment findTagFragment;
    private boolean primo = true;
    String sex;
    Float priceFrom;
    Float priceTo;
    String order;


    String[] titles;
    String query;
    Context context;
    private FindBrandFragment findBrandFragment;

    public SearchAdapter(FragmentManager fm, String[] titles, String query, Context context, String sex, float pricefrom, float priceto, String order) {
        //passo il fragment manager e i titoli delle tab
        super(fm);
        this.titles = titles;
        this.query = query;
        this.context = context;
        this.priceFrom = pricefrom;
        this.priceTo = priceto;
        this.order = order;
        this.sex = sex;
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

                return findUserFragment = (FindUserFragment) new FindUserFragment().newIstance(query, context);
            case 1:

                return findClothFragment = (FindClothFragment) new FindClothFragment().newIstance(query, sex, priceFrom, priceTo, context, order);
            case 2:

                return findTagFragment = (FindTagFragment) new FindTagFragment().newIstance(query, sex, priceFrom, priceTo, context, order);
            case 3:
                return findBrandFragment = (FindBrandFragment) new FindBrandFragment().newIstance(query, sex, priceFrom, priceTo, context, order);

        }

        return null;
    }

    //quando faccio una seconda query faccio un refresh dei fragment
    public void setQuery(String query) {
        this.query = query;
        if (findUserFragment != null) findUserFragment.refresh(query);
        //System.out.println(findClothFragment);
        if (findClothFragment != null) findClothFragment.refresh(query);
        if (findTagFragment != null) findTagFragment.refresh(query);
        if (findBrandFragment != null) findBrandFragment.refresh(query);

    }


}
