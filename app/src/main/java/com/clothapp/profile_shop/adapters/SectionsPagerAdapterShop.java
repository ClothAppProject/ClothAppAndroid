package com.clothapp.profile_shop.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clothapp.R;
import com.clothapp.profile.fragments.PlaceholderFragment;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.profile_shop.fragments.*;

// PagerAdapter for tabs and associated fragments.
public class SectionsPagerAdapterShop extends FragmentPagerAdapter {
    private Context context;
    public SectionsPagerAdapterShop(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        switch (position) {
            case 0:
                return ProfileShopInfoFragment.newInstance(ShopProfileActivity.username);
            case 1:
                return ProfileShopUploadedPhotosFragment.newInstance(ShopProfileActivity.username);
            case 2:
                return ProfileShopFollowersFragment.newInstance(ShopProfileActivity.username);
            case 3:
                return ProfileShopFollowingFragment.newInstance(ShopProfileActivity.username);
            default:
                return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        // Number of pages.
        return 4;//7;
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