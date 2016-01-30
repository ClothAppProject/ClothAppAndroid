package com.clothapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clothapp.resources.ApplicationSupport;
import com.parse.ParseObject;


/**
 * This fragment will populate the children of the ViewPager from
 */import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.util.List;

public class ImageDetailFragment extends Fragment {

    private static final String URL = "EXTRA_DATA";
    private static final String USER = "USER";
    private String url;
    private String user;
    private ImageView v;
    private TextView t;

    public static ImageDetailFragment newInstance(ParseObject objects) {
        String imageUrl=objects.getParseFile("photo").getUrl();
        String username= objects.getString("user");
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putString(URL, imageUrl);
        args.putString(USER,username);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        url = getArguments() != null ? getArguments().getString(URL) : null;
        user= getArguments() != null ? getArguments().getString(USER) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t=(TextView)rootView.findViewById(R.id.user);
        v=(ImageView) rootView.findViewById(R.id.details);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        t.setText(user);
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                i.putExtra("user", user);
                startActivity(i);
                getActivity().finish();
            }
        });
        Glide.with(this)
                .load(url)
                .into(v);
    }

}
