package com.clothapp.profile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;

import java.util.ArrayList;

// This fragment contains info about the user.
public class ProfileInfoFragment extends Fragment {

    private static final String PARSE_USERNAME = "username";

    public ProfileInfoFragment() {

    }

    public static ProfileInfoFragment newInstance(String username) {
        ProfileInfoFragment fragment = new ProfileInfoFragment();
        Bundle args = new Bundle();
        args.putString(PARSE_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_info, container, false);

        UserProfileActivity.viewProfileInfo = (RecyclerView) rootView.findViewById(R.id.profile_info_recycler_view);

        LinearLayoutManager llm = new LinearLayoutManager(UserProfileActivity.context);
        UserProfileActivity.viewProfileInfo.setLayoutManager(llm);

        ArrayList<ProfileInfoListItem> items = new ArrayList<>();

        ProfileInfoListItem itemDummy = new ProfileInfoListItem("DUMMY", "Loading...");
        ProfileInfoListItem itemName = new ProfileInfoListItem("NAME", "Loading...");
        ProfileInfoListItem itemAge = new ProfileInfoListItem("AGE", "Loading...");
        ProfileInfoListItem itemCity = new ProfileInfoListItem("CITY", "Loading...");
        ProfileInfoListItem itemEmail = new ProfileInfoListItem("EMAIL", "Loading...");
        ProfileInfoListItem itemDescription = new ProfileInfoListItem("DESCRIPTION", "Loading...");

        items.add(itemDummy);
        items.add(itemName);
        items.add(itemAge);
        items.add(itemCity);
        items.add(itemEmail);
        items.add(itemDescription);

        ProfileInfoAdapter adapter = new ProfileInfoAdapter(items);
        UserProfileActivity.viewProfileInfo.setAdapter(adapter);

        // Get user info from Parse
        ProfileUtils.getParseInfo(UserProfileActivity.context, UserProfileActivity.username);

        return rootView;
    }

}