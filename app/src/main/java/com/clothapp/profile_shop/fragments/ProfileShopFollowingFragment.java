package com.clothapp.profile_shop.fragments;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.profile.adapters.PeopleListAdapter;
import com.clothapp.parse.notifications.FollowUtil;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.resources.User;

import java.util.ArrayList;

/**
 * Created by giacomoceribelli on 25/02/16.
 */
public class ProfileShopFollowingFragment extends Fragment {
    public ArrayList<User> users;
    private String username;
    private Context context;
    private RecyclerView viewProfileShopFollowing;
    private PeopleListAdapter adapter;
    public ProfileShopFollowingFragment() {
    }

    public static ProfileShopFollowingFragment newInstance(String username, Context context) {
        ProfileShopFollowingFragment fragment = new ProfileShopFollowingFragment();
        fragment.username = username;
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment which will contain the RecyclerView
        final View rootView = inflater.inflate(R.layout.fragment_profile_follow, container, false);
        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        viewProfileShopFollowing = (RecyclerView) rootView.findViewById(R.id.profile_follow_recycler_view);

        //Set the no followers textview
        final TextView noFollowing = (TextView) rootView.findViewById(R.id.no_follow);
        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(context);
        viewProfileShopFollowing.setLayoutManager(llm);

        users = new ArrayList<User>();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new PeopleListAdapter(users, context);
        viewProfileShopFollowing.setAdapter(adapter);
        //faccio la query
        FollowUtil.getFollowing(users, rootView, viewProfileShopFollowing, username, noFollowing);

        //scroll per aggiungere nuovi follower
        viewProfileShopFollowing.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                //update following list
                FollowUtil.getFollowing(users, rootView, viewProfileShopFollowing, username, noFollowing);
            }
        });

        return rootView;
    }
}
