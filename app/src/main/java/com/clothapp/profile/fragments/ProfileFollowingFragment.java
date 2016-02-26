package com.clothapp.profile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.PeopleListAdapter;
import com.clothapp.profile.utils.FollowUtil;
import com.clothapp.resources.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 25/02/16.
 */
public class ProfileFollowingFragment extends Fragment {
    private static final String PARSE_USERNAME = "username";
    public static ArrayList<User> users;
    private PeopleListAdapter adapter;
    public ProfileFollowingFragment() {
    }

    public static ProfileFollowingFragment newInstance(String username) {
        ProfileFollowingFragment fragment = new ProfileFollowingFragment();
        Bundle args = new Bundle();
        args.putString(PARSE_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the fragment which will contain the RecyclerView
        final View rootView = inflater.inflate(R.layout.fragment_profile_follow, container, false);
        // Set the recycler view declared in UserProfileActivity to the newly created RecyclerView
        UserProfileActivity.viewProfileFollowing = (RecyclerView) rootView.findViewById(R.id.profile_follow_recycler_view);

        // Set the layout manager for the recycler view.
        // LinearLayoutManager makes the recycler view look like a ListView.
        LinearLayoutManager llm = new LinearLayoutManager(UserProfileActivity.context);
        UserProfileActivity.viewProfileFollowing.setLayoutManager(llm);


        users = new ArrayList<User>();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new PeopleListAdapter(users,"persona");
        UserProfileActivity.viewProfileFollowing.setAdapter(adapter);
        //faccio la query
        FollowUtil.getFollowing(users,rootView,UserProfileActivity.viewProfileFollowing);

        return rootView;
    }
}
