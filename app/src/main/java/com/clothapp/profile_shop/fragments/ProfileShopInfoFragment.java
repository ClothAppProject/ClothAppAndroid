package com.clothapp.profile_shop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.profile.adapters.ProfileInfoAdapter;
import com.clothapp.profile.utils.ProfileInfoListItem;
import com.clothapp.profile.utils.ProfileUtils;

import java.util.ArrayList;

// This fragment contains info about the user.
public class ProfileShopInfoFragment extends Fragment {

    private String username;
    private Context context;
    private RecyclerView viewProfileInfo;
    public ProfileShopInfoFragment() {

    }

    public static ProfileShopInfoFragment newInstance(String username, Context context) {
        ProfileShopInfoFragment fragment = new ProfileShopInfoFragment();
        fragment.username = username;
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment which will contain the RecyclerView
        View rootView = inflater.inflate(R.layout.fragment_profile_info, container, false);

        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        viewProfileInfo = (RecyclerView) rootView.findViewById(R.id.profile_info_recycler_view);

        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(context);
        viewProfileInfo.setLayoutManager(llm);

        // itemDummy will be replaced by a header
        ProfileInfoListItem itemDummy = new ProfileInfoListItem("DUMMY", "Loading...");

        // Create a new item for each list (recycler view) entry
        ProfileInfoListItem itemName = new ProfileInfoListItem("NAME", "Loading...");
        ProfileInfoListItem itemAddress = new ProfileInfoListItem("ADDRESS", "Loading...");
        ProfileInfoListItem itemCity = new ProfileInfoListItem("CITY", "Loading...");
        ProfileInfoListItem itemSite = new ProfileInfoListItem("SITE", "Loading...");
        ProfileInfoListItem itemEmail = new ProfileInfoListItem("EMAIL", "Loading...");

        // Create an array and add the previously created items to it.
        ArrayList<ProfileInfoListItem> items = new ArrayList<>();

        items.add(itemDummy);
        items.add(itemName);
        items.add(itemAddress);
        items.add(itemCity);
        items.add(itemEmail);
        items.add(itemSite);

        // Create a new adapter for the recycler view
        ProfileInfoAdapter adapter = new ProfileInfoAdapter(items, username);
        viewProfileInfo.setAdapter(adapter);

        // Get user info from Parse
        ProfileUtils.getParseShopInfo(context, username, viewProfileInfo);

        // Return the fragment
        return rootView;
    }

}