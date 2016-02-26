package com.clothapp.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MostRecentFragment extends Fragment {

    public final static String ITEMS_COUNT_KEY = "PartThreeFragment$ItemsCount";

    private MostRecentAdapter mostRecentAdapter;

    public static MostRecentFragment newInstance(int itemsCount) {
        MostRecentFragment mostRecentFragment = new MostRecentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ITEMS_COUNT_KEY, itemsCount);
        mostRecentFragment.setArguments(bundle);
        return mostRecentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.fragment_home_most_recent, container, false);
        setupRecyclerView(recyclerView, container.getContext());
        return recyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        mostRecentAdapter = new MostRecentAdapter(new ArrayList<Image>());
        recyclerView.setAdapter(mostRecentAdapter);
        getParseMostRecentPhotos(0, 20);
    }

//    private List<String> createItemList() {
//        List<String> itemList = new ArrayList<>();
//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            int itemsCount = bundle.getInt(ITEMS_COUNT_KEY);
//            for (int i = 0; i < itemsCount; i++) {
//                itemList.add("Item " + i);
//            }
//        }
//        return itemList;
//    }


    private void getParseMostRecentPhotos(int start, int limit) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.addDescendingOrder("createdAt");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> photos, ParseException e) {

                Log.d("MostRecentFragment", "Done: retrieved photos = " + photos.size());

                if (e == null) {

                    for (final ParseObject photo : photos) {
                        mostRecentAdapter.itemList.add(new Image(photo));
                    }

                    mostRecentAdapter.notifyDataSetChanged();

                } else {
                    Log.d("MostRecentFragment", "Error: " + e.getMessage());
                }
            }
        });
    }
}

