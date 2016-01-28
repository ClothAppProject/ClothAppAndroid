package com.clothapp.resources;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;


import com.clothapp.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageGridViewAdapter extends BaseAdapter {
    private final Context context;
   // private List<String> urls = new ArrayList<String>();
    private List<File>files=new ArrayList<File>();

    public ImageGridViewAdapter(Context context,File[] files) {
        this.context = context;
        ArrayList<File> a=new ArrayList<>();
        for(int i=0;i<files.length;i++) a.add(i,files[i]);
        this.files=(List)a;

    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        //TODO: per ora funziona solo con lo schermo verticale
        //adatto la grandezza delle immagini alla grandezza del display
        DisplayMetrics metrics =context.getResources().getDisplayMetrics();

        int w=metrics.widthPixels;
        int s=((w)/2)-25;
        //System.out.println(s);
        view.setLayoutParams(new GridView.LayoutParams(s, s));
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setPadding(0,0,0,0);
        // Get the image URL for the current position.
        File file = null;

        file = getItem(position);


        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(file) //
               .fit() //
                //.tag(context) //
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return files.size();
    }

    @Override public File getItem(int position) {
        return (files.get(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }
}