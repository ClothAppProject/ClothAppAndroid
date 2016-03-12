package com.clothapp.profile.fragments;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.Image;

import java.util.ArrayList;

public class ProfileUploadedPhotosFragment extends Fragment {
    public static ProfileUploadedPhotosAdapter adapter;
    public static ArrayList<Image> photos = new ArrayList<>();

    private static final String PARSE_USERNAME = "username";
    public static TextView noPhotosText;
    public ProfileUploadedPhotosFragment() {

    }

    public static ProfileUploadedPhotosFragment newInstance(String username) {
        ProfileUploadedPhotosFragment fragment = new ProfileUploadedPhotosFragment();
        Bundle args = new Bundle();
        args.putString(PARSE_USERNAME, username);
        fragment.setArguments(args);
        photos = new ArrayList<>();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment which will contain the RecyclerView
        View rootView = inflater.inflate(R.layout.fragment_profile_uploaded_photos, container, false);

        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        UserProfileActivity.viewProfileUploadedPhotos = (RecyclerView) rootView.findViewById(R.id.profile_uploaded_photos_recycler_view);

        //set the no text for no photos uploaded
        noPhotosText = (TextView) rootView.findViewById(R.id.no_photos);
        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(UserProfileActivity.context);
        UserProfileActivity.viewProfileUploadedPhotos.setLayoutManager(llm);

        // Create an array and add the previously created items to it.
        ArrayList<Image> items = new ArrayList<>();

        // Create a new adapter for the recycler view
         adapter = new ProfileUploadedPhotosAdapter(items,"persona");
        UserProfileActivity.viewProfileUploadedPhotos.setAdapter(adapter);

        // Get user info from Parse
        ProfileUtils.getParseUploadedPhotos(UserProfileActivity.username, 0, 10);

        UserProfileActivity.viewProfileUploadedPhotos.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //update with more photos
                ProfileUtils.getParseUploadedPhotos(UserProfileActivity.username,adapter.photos.size(),10);
            }
        });

        // Return the fragment
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if (adapter!=null) adapter.notifyDataSetChanged();
    }

}
