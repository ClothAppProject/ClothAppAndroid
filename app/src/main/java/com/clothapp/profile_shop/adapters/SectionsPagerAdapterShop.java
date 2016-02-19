package com.clothapp.profile_shop.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.clothapp.profile.fragments.PlaceholderFragment;
import com.clothapp.profile.fragments.ProfileInfoFragment;
import com.clothapp.profile.fragments.ProfileUploadedPhotosFragment;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.profile_shop.fragments.ProfileShopInfoFragment;
import com.clothapp.profile_shop.fragments.ProfileShopUploadedPhotosFragment;

// PagerAdapter for tabs and associated fragments.
public class SectionsPagerAdapterShop extends FragmentPagerAdapter {

    public SectionsPagerAdapterShop(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.

        switch (position) {
            case 0:
                return ProfileShopInfoFragment.newInstance(ShopProfileActivity.username);
            case 1:
                return ProfileShopUploadedPhotosFragment.newInstance(ShopProfileActivity.username);
            default:
                return PlaceholderFragment.newInstance(position + 1);
        }
    }

    @Override
    public int getCount() {
        // Number of pages.
        return 7;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "INFO";
            case 1:
                return "UPLOADED PHOTOS";
            case 2:
                return "FOLLOWERS";
            case 3:
                return "FOLLOWING";
            case 4:
                return "FAVORITE PHOTOS";
            case 5:
                return "FAVORITE BRANDS";
            case 6:
                return "FAVORITE SHOPS";
        }
        return null;
    }
}