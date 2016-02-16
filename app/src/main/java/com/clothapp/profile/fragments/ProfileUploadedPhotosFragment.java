package com.clothapp.profile.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileInfoAdapter;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.utils.ProfileInfoListItem;
import com.clothapp.profile.utils.ProfileUploadedPhotosListItem;
import com.clothapp.profile.utils.ProfileUtils;

import java.util.ArrayList;

public class ProfileUploadedPhotosFragment extends Fragment {

    private static final String PARSE_USERNAME = "username";

    public ProfileUploadedPhotosFragment() {

    }

    public static ProfileUploadedPhotosFragment newInstance(String username) {
        ProfileUploadedPhotosFragment fragment = new ProfileUploadedPhotosFragment();
        Bundle args = new Bundle();
        args.putString(PARSE_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment which will contain the RecyclerView
        View rootView = inflater.inflate(R.layout.fragment_profile_uploaded_photos, container, false);

        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        UserProfileActivity.viewProfileUploadedPhotos = (RecyclerView) rootView.findViewById(R.id.profile_uploaded_photos_recycler_view);

        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(UserProfileActivity.context);
        UserProfileActivity.viewProfileUploadedPhotos.setLayoutManager(llm);

        ProfileUploadedPhotosListItem itemTest1 = new ProfileUploadedPhotosListItem("Hello!");
        ProfileUploadedPhotosListItem itemTest2 = new ProfileUploadedPhotosListItem("Hello!");
        ProfileUploadedPhotosListItem itemTest3 = new ProfileUploadedPhotosListItem("Hello!");

        // Create an array and add the previously created items to it.
        ArrayList<ProfileUploadedPhotosListItem> items = new ArrayList<>();

        items.add(itemTest1);
        items.add(itemTest2);
        items.add(itemTest3);

        // Create a new adapter for the recycler view
        ProfileUploadedPhotosAdapter adapter = new ProfileUploadedPhotosAdapter(items);
        UserProfileActivity.viewProfileUploadedPhotos.setAdapter(adapter);

        // Get user info from Parse
        // ProfileUtils.getParseInfo(UserProfileActivity.context, UserProfileActivity.username);

        // Return the fragment
        return rootView;
    }
}
