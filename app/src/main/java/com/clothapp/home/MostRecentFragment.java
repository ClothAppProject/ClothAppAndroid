package com.clothapp.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class MostRecentFragment extends Fragment {

    public static MostRecentAdapter mostRecentAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private MostRecentScrollListener mostRecentScrollListener;
    private Boolean loading = true;

    public static MostRecentFragment newInstance() {
        return new MostRecentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Use SwipeRefreshLayout to allow pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_home_most_recent, container, false);
        RecyclerView recyclerView = (RecyclerView) swipeRefreshLayout.findViewById(R.id.recyclerView);

        setupSwipeRefreshLayout(swipeRefreshLayout, recyclerView);
        setupRecyclerView(recyclerView, container.getContext());

        return swipeRefreshLayout;
    }

    // Setup the SwipeRefreshLayout by adding a custom OnScrollListener to it.
    private void setupSwipeRefreshLayout(SwipeRefreshLayout swipeRefreshLayout, final RecyclerView recyclerView) {

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Log.d("MostRecentFragment", "onRefresh");
                if (mostRecentAdapter == null) return;

                // Create a new empty list and pass it to the adapter while we wait for the Parse
                // query to complete and update the list.
                MostRecentAdapter.itemList = new ArrayList<>();
                mostRecentAdapter.notifyDataSetChanged();

                loading = true;

                // It is needed to remove the previous OnScrollListener because the number of items
                // in the list is reset to 0.

                // Remove the previous custom OnScrollListener
                recyclerView.removeOnScrollListener(mostRecentScrollListener);
                // Create a new custom OnScrollListener
                mostRecentScrollListener = new MostRecentScrollListener((GridLayoutManager) recyclerView.getLayoutManager());
                // Add the new OnScrollListener
                recyclerView.addOnScrollListener(mostRecentScrollListener);

                // Update the itemList with the result of a query to Parse.
                getParseMostRecentPhotos(0, 12);
            }
        });
    }

    // Setup the RecyclerView with a GridLayoutManager (GridView), adding an OnScrollListener and
    // loading the first 12 photos from Parse.
    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        //prendo foto scaricate nella splash
        ApplicationSupport appSupport = ((ApplicationSupport) HomeActivity.activity.getApplicationContext());
        mostRecentAdapter = new MostRecentAdapter(appSupport.getPhotos());
        recyclerView.setAdapter(mostRecentAdapter);
        mostRecentScrollListener = new MostRecentScrollListener(gridLayoutManager);
        recyclerView.addOnScrollListener(mostRecentScrollListener);

        int size = 0;
        if (MostRecentAdapter.itemList != null) {
            size = MostRecentAdapter.itemList.size();
        } else {
            MostRecentAdapter.itemList = new ArrayList<>();
        }

        getParseMostRecentPhotos(size, 12);
    }

    // Get the most recent photos from Parse from "start" to "start" + "limit"
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
                        MostRecentAdapter.itemList.add(new Image(photo));
                    }

                    // Log.d("MostRecentFragment", "Now itemList.size() is " + MostRecentAdapter.itemList.size());

                    mostRecentAdapter.notifyDataSetChanged();

                    // Log.d("MostRecentFragment", "isRefreshing() is " + swipeRefreshLayout.isRefreshing());
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                } else {
                    // No result from Parse or something went wrong...
                    Log.d("MostRecentFragment", "Error: " + e.getMessage());
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if(mostRecentAdapter!=null)mostRecentAdapter.notifyDataSetChanged();
    }

    // This class is a custom OnScrollListener, so we don't have to write anonymous classes.
    class MostRecentScrollListener extends RecyclerView.OnScrollListener {

        private GridLayoutManager gridLayoutManager;

        // Total number of loaded photos.
        private int previousTotal = 0;
        // Number of remaining loaded photos before loading more photos.
        private int visibleThreshold = 5;
        int firstVisibleItem, visibleItemCount, totalItemCount;

        public MostRecentScrollListener(GridLayoutManager gridLayoutManager) {
            this.gridLayoutManager = gridLayoutManager;
        }

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

                int size = MostRecentAdapter.itemList.size();
                // Log.d("MostRecentFragment", "Loading more photos (from " + size + " to " + (size + 12) + ")");

                // Get more photos from Parse.
                getParseMostRecentPhotos(size, 12);
            }
        }
    }
}

