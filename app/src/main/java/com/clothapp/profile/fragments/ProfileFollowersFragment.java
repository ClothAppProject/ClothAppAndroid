package com.clothapp.profile.fragments;

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
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.PeopleListAdapter;
import com.clothapp.parse.notifications.FollowUtil;
import com.clothapp.resources.User;

import java.util.ArrayList;

/**
 * Created by giacomoceribelli on 25/02/16.
 */
public class ProfileFollowersFragment extends Fragment {
    private RecyclerView viewProfileFollowers;
    private String username;
    private Context context;
    public ArrayList<User> users;
    private PeopleListAdapter adapter;

    public ProfileFollowersFragment() {
    }

    public static ProfileFollowersFragment newInstance(String username, Context context) {
        ProfileFollowersFragment fragment = new ProfileFollowersFragment();
        fragment.username = username;
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment which will contain the RecyclerView
        final View rootView = inflater.inflate(R.layout.fragment_profile_follow, container, false);
        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        viewProfileFollowers = (RecyclerView) rootView.findViewById(R.id.profile_follow_recycler_view);

        //Set the no followers textview
        final TextView noFollowers = (TextView) rootView.findViewById(R.id.no_follow);
        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(context);
        viewProfileFollowers.setLayoutManager(llm);

        users = new ArrayList<User>();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new PeopleListAdapter(users, context);
        viewProfileFollowers.setAdapter(adapter);
        //faccio la query
        FollowUtil.getFollower(users,rootView,viewProfileFollowers, username, noFollowers);

        //scroll per aggiungere followers
        viewProfileFollowers.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                FollowUtil.getFollower(users,rootView,viewProfileFollowers,username, noFollowers);
            }
        });

        return rootView;
    }
}
