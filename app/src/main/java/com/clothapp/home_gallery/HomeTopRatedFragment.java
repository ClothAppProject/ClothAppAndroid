package com.clothapp.home_gallery;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/02/16.
 */
public class HomeTopRatedFragment extends Fragment {
    private List<CardView>cardViews;
    private View rootView;
    private View view;
    private ListView l;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home_top_rated, container, false);
        l=(ListView)rootView.findViewById(R.id.toplist);
        findTopRated();

        //setCardViews(findTopRated(),container);
        return rootView;
    }
/*
    public void setCardViews(List<Image>images,ViewGroup container){
        int i;
        for(i=0;i<images.size();i++){
           // Card c=new Card();
            ImageView imageView= (ImageView) getActivity().findViewById(R.id.topfoto);
            l.addView(imageView);
            TextView user=(TextView)rootView.findViewById(R.id.user);
            user.setText(images.get(i).getUser());

            Glide.with(this)
                    .load(images.get(i).getFile())
                    .into(imageView);
        }
    }
*/
    public List<Image> findTopRated(){
        //qui scarico le foto
        final View vi = new View(getActivity().getApplicationContext());
        final ArrayList<Image> photo = new ArrayList<>();
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.setLimit(12);
        query.orderByDescending("nLike");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fotos, ParseException e) {
                if (e == null) {
                    Log.d("toprated", "Retrieved " + fotos.size() + " photos");

                    int i;
                    for (i = 0; i < fotos.size(); i++) {
                        ParseObject obj = fotos.get(i);
                        ParseFile file = obj.getParseFile("thumbnail");
                        try {
                            //inserisco le foto in una lista
                            photo.add(new com.clothapp.resources.Image(file.getFile(), obj.getObjectId(),obj.getString("user"),obj.getList("like")));

                        } catch (ParseException e1) {
                            check(e.getCode(), vi, e.getMessage());
                        }
                    }
                    MyListAdapter adapter=new MyListAdapter(getActivity().getApplicationContext(), photo);
                    l.setAdapter(adapter);
                } else {
                    //errore nel reperire gli oggetti Photo dal database
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });
        return photo;
    }


}
