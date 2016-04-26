package com.clothapp.profile_shop.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.Image;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ProfileShopUploadedPhotosFragment extends Fragment {
    public ProfileUploadedPhotosAdapter adapter;
    private String username;
    private Context context;
    private RecyclerView viewProfileUploadedPhotos;
    public ArrayList<Image> photos = new ArrayList<>();

    public ProfileShopUploadedPhotosFragment() {

    }

    public static ProfileShopUploadedPhotosFragment newInstance(String username, Context context) {
        ProfileShopUploadedPhotosFragment fragment = new ProfileShopUploadedPhotosFragment();
        fragment.username = username;
        fragment.context = context;
        fragment.photos = new ArrayList<>();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment which will contain the RecyclerView
        View rootView = inflater.inflate(R.layout.fragment_profile_uploaded_photos, container, false);

        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        viewProfileUploadedPhotos = (RecyclerView) rootView.findViewById(R.id.profile_uploaded_photos_recycler_view);

        //set the no text for no photos uploaded
        final TextView noPhotosText = (TextView) rootView.findViewById(R.id.no_photos);
        final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.loading_photos);

        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(context);
        viewProfileUploadedPhotos.setLayoutManager(llm);

        // Create an array and add the previously created items to it.
        ArrayList<Image> items = new ArrayList<>();

        // Create a new adapter for the recycler view
        adapter = new ProfileUploadedPhotosAdapter(items,"negozio", context);
        viewProfileUploadedPhotos.setAdapter(adapter);

        // Get user info from Parse
        ProfileUtils.getShopParseUploadedPhotos(username, 0, 10, progressBar, viewProfileUploadedPhotos, noPhotosText);

        viewProfileUploadedPhotos.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //update with more photos
                ProfileUtils.getShopParseUploadedPhotos(username,adapter.photos.size(),10, progressBar, viewProfileUploadedPhotos, noPhotosText);
            }
        });
        // Return the fragment
        return rootView;
    }
}
