package com.clothapp.profile_shop.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clothapp.R;
import com.clothapp.profile.fragments.PlaceholderFragment;
import com.clothapp.profile_shop.fragments.*;

// PagerAdapter for tabs and associated fragments.
public class SectionsPagerAdapterShop extends FragmentPagerAdapter {
    private Context context;
    private String username;
    private int numberPage = 5;

    public SectionsPagerAdapterShop(FragmentManager fm, Context context, String username) {
        super(fm);
        this.context = context;
        this.username = username;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        switch (position) {
            case 0:
                return ProfileShopInfoFragment.newInstance(username, context);
            case 1:
                return ProfileShopUploadedPhotosFragment.newInstance(username, context);
            case 2:
                return ProfileShopFollowersFragment.newInstance(username, context);
            case 3:
                return ProfileShopFollowingFragment.newInstance(username, context);
            case 4:
                return ProfileShopMapFragment.newInstance(username, context);
            default:
                return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        // Number of pages.
        return this.numberPage;
    }

    public void setCount(int n) {
        this.numberPage = n;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return context.getString(R.string.info_profilo);
            case 1:
                return context.getString(R.string.photo);
            case 2:
                return context.getString(R.string.followers);
            case 3:
                return context.getString(R.string.following);
            case 4:
                return context.getString(R.string.map);
            /*case 4:
                return "FAVORITE PHOTOS";
            case 5:
                return "FAVORITE BRANDS";
            case 6:
                return "FAVORITE SHOPS";*/
        }
        return null;
    }
}