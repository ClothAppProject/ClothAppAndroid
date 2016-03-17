package com.clothapp.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.resources.Image;

import java.util.ArrayList;

/**
 * Created by SimoneConia on 17/03/16.
 */
public class PopularShopsFragment extends Fragment {

    private static PopularShopsAdapter popularShopsAdapter;

    public static PopularShopsFragment newInstance() {
        return new PopularShopsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.fragment_home_popular_shops, container, false);

        setupRecyclerView(recyclerView, container.getContext());

        return recyclerView;
    }

    private void setupRecyclerView(RecyclerView recyclerView, Context context) {
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<PopularShop> shops = new ArrayList<>();

        shops.add(new PopularShop("Cult", "Viale Libia, 123", 23, R.drawable.cult));
        shops.add(new PopularShop("Subzone", "Viale Libia, 123", 12, R.drawable.subzone));
        shops.add(new PopularShop("BF Mountain", "Viale Libia, 123", 12, R.drawable.bfmountain));
        shops.add(new PopularShop("NonSoloPolo", "Viale Libia, 123", 14, R.drawable.nonsolopolo));

        PopularShopsAdapter.itemList = shops;

        popularShopsAdapter = new PopularShopsAdapter();
        recyclerView.setAdapter(popularShopsAdapter);
    }
}
