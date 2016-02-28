package com.clothapp.home;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class TopRatedFragment extends Fragment {

    public static TopRatedAdapter topRatedAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;
    private TopRatedScrollListener topRatedScrollListener;
    private Boolean loading = true;

    public static TopRatedFragment newInstance() {
        return new TopRatedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Use SwipeRefreshLayout to allow pull to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) inflater.inflate(R.layout.fragment_home_top_rated_new, container, false);
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
                // Log.d("TopRatedFragment", "onRefresh");
                if (topRatedAdapter == null) return;

                // Create a new empty list and pass it to the adapter while we wait for the Parse
                // query to complete and update the list.
                TopRatedAdapter.itemList = new ArrayList<>();
                topRatedAdapter.notifyDataSetChanged();

                loading = true;

                // It is needed to remove the previous OnScrollListener because the number of items
                // in the list is reset to 0.

                // Remove the previous custom OnScrollListener
                recyclerView.removeOnScrollListener(topRatedScrollListener);
                // Create a new custom OnScrollListener
                topRatedScrollListener = new TopRatedScrollListener((LinearLayoutManager) recyclerView.getLayoutManager());
                // Add the new OnScrollListener
                recyclerView.addOnScrollListener(topRatedScrollListener);

                // Update the itemList with the result of a query to Parse.
                getParseTopRatedPhotos(0, 12);
            }
        });
    }

    // Setup the RecyclerView with a LinearLayoutManager (ListView), adding an OnScrollListener and
    // loading the first 12 photos from Parse.
    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        topRatedAdapter = new TopRatedAdapter(new ArrayList<Image>());
        recyclerView.setAdapter(topRatedAdapter);

        topRatedScrollListener = new TopRatedScrollListener(linearLayoutManager);
        recyclerView.addOnScrollListener(topRatedScrollListener);

        int size = TopRatedAdapter.itemList.size();
        getParseTopRatedPhotos(size, 12);
    }

    // Get the top rated photos from Parse from "start" to "start" + "limit"
    private void getParseTopRatedPhotos(int start, int limit) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.addDescendingOrder("nLike");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {

            @Override
            public void done(List<ParseObject> photos, ParseException e) {

                // Log.d("TopRatedFragment", "Done: retrieved photos = " + photos.size());

                if (e == null) {

                    // Log.d("TopRatedFragment", "Successfully loaded " + photos.size() + " photos.");

                    for (final ParseObject photo : photos) {
                        // TODO: Improve download speed
                        // I don't like this: too slow...
                        // Downloading image on main thread -> Download on a separate thread
                        // Downloading is sequential -> Multiple downloads at the same time
                        TopRatedAdapter.itemList.add(new Image(photo));
                    }

                    // Log.d("TopRatedFragment", "Now itemList.size() is " + TopRatedAdapter.itemList.size());

                    topRatedAdapter.notifyDataSetChanged();

                    // Log.d("TopRatedAdapter", "isRefreshing() is " + swipeRefreshLayout.isRefreshing());
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }

                } else {
                    Log.d("TopRatedFragment", "Error: " + e.getMessage());
                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if(topRatedAdapter!=null)topRatedAdapter.notifyDataSetChanged();
    }

    // This class is a custom OnScrollListener, so we don't have to write anonymous classes.
    class TopRatedScrollListener extends RecyclerView.OnScrollListener {

        private LinearLayoutManager linearLayoutManager;

        private int previousTotal = 0;
        private int visibleThreshold = 5;
        int firstVisibleItem, visibleItemCount, totalItemCount;

        public TopRatedScrollListener(LinearLayoutManager linearLayoutManager) {
            this.linearLayoutManager = linearLayoutManager;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            visibleItemCount = recyclerView.getChildCount();
            totalItemCount = linearLayoutManager.getItemCount();
            firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

            if (loading) {
                if (totalItemCount > previousTotal) {
                    loading = false;
                    previousTotal = totalItemCount;
                }
            } else if ((totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {

                loading = true;

                int size = TopRatedAdapter.itemList.size();
                // Log.d("TopRatedFragment", "Loading more photos (from " + size + " to " + (size + 12) + ")");

                // Get more photos from Parse.
                getParseTopRatedPhotos(size, 12);
            }
        }
    }
}
