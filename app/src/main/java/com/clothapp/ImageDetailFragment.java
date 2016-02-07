package com.clothapp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class ImageDetailFragment extends Fragment {

    private String user;
    private ImageView v;
    private TextView t;
    private DonutProgress donutProgress;
    private String Id;
    private static Context context;

    public static ImageDetailFragment newInstance(String id, Context c) {
        context = c;
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("ID", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.Id = getArguments() != null ? getArguments().getString("ID") : null;
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
                        Glide.with(context)
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
