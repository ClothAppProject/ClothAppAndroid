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
import com.clothapp.resources.ExceptionCheck;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;


/**
 * This fragment will populate the children of the ViewPager from
 */import android.support.v4.app.Fragment;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ImageDetailFragment extends Fragment {

    private String user;
    private ImageView v;
    private TextView t;
    private DonutProgress donutProgress;
    private String Id;

    public ImageDetailFragment(String id)   {
        this.Id=id;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t=(TextView)rootView.findViewById(R.id.user);
        v=(ImageView) rootView.findViewById(R.id.details);
        donutProgress = (DonutProgress) rootView.findViewById(R.id.donut_progress);
        return rootView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Fragment fragmento = this;

        //faccio query al database per scaricare la foto
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", Id);
        query.orderByDescending("createdAt");
        try {
            List<ParseObject> objects = query.find();
            user = objects.get(0).getString("user");
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
            objects.get(0).getParseFile("photo").getFileInBackground(new GetFileCallback() {
                @Override
                public void done(File file, ParseException e) {
                    if (e==null)    {
                        //nascondo caricamento mostro immagine
                        donutProgress.setVisibility(View.INVISIBLE);
                        v.setVisibility(View.VISIBLE);
                        Glide.with(fragmento)
                                .load(file)
                                .into(v);
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer percentDone) {
                    //passo percentuale
                    donutProgress.setProgress(percentDone);
                }
            });
        } catch (ParseException e) {
            //errore chiamata
        }
    }

}
