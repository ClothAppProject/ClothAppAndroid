package com.clothapp.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

    private SwipeRefreshLayout swipeRefreshLayout;
    private MostRecentAdapter mostRecentAdapter;
    private Boolean loading = true;

    public static MostRecentFragment newInstance() {
        return new MostRecentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_home_most_recent, container, false);
        setupSwipeRefreshLayout(swipeRefreshLayout);

        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);
        setupRecyclerView(recyclerView, container.getContext());

        return swipeRefreshLayout;
    }

    private void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout) {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Log.d("MostRecentFragment", "onRefresh");
                if (mostRecentAdapter == null) return;

                mostRecentAdapter.itemList = new ArrayList<>();
                mostRecentAdapter.notifyDataSetChanged();

                getParseMostRecentPhotos(0, 12);
            }
        });
    }

    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        // recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        mostRecentAdapter = new MostRecentAdapter(new ArrayList<Image>());
        recyclerView.setAdapter(mostRecentAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            private int previousTotal = 0;
            private int visibleThreshold = 5;
            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                } else if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                    loading = true;

                    int size = mostRecentAdapter.itemList.size();
                    // Log.d("MostRecentFragment", "Loading more photos (from " + size + " to " + (size + 12) + ")");
                    getParseMostRecentPhotos(size, 12);
                }
            }
        });

        int size = mostRecentAdapter.itemList.size();
        getParseMostRecentPhotos(size, 12);
    }

    private void getParseMostRecentPhotos(int start, int limit) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.addDescendingOrder("createdAt");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> photos, ParseException e) {

                // Log.d("MostRecentFragment", "Done: retrieved photos = " + photos.size());

                if (e == null) {

                    // Log.d("MostRecentFragment", "Successfully loaded " + photos.size() + " photos.");

                    for (final ParseObject photo : photos) {
                        // TODO: Improve download speed
                        // I don't like this: too slow...
                        // Downloading image on main thread -> Download on a separate thread
                        // Downloading is sequential -> Multiple downloads at the same time
                        mostRecentAdapter.itemList.add(new Image(photo));
                    }

                    // Log.d("MostRecentFragment", "Now itemList.size() is " + mostRecentAdapter.itemList.size());

                    mostRecentAdapter.notifyDataSetChanged();

                    // Log.d("MostRecentFragment", "isRefreshing() is " + swipeRefreshLayout.isRefreshing());
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                } else {
                    Log.d("MostRecentFragment", "Error: " + e.getMessage());
                }
            }
        });
    }
}

