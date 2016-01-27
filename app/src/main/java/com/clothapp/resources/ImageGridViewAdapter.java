package com.clothapp.resources;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.clothapp.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageGridViewAdapter extends BaseAdapter {
    private final Context context;
    private List<String> urls = new ArrayList<String>();

    public ImageGridViewAdapter(Context context,String[] urls) {
        this.context = context;
        ArrayList<String>a=new ArrayList<String>();
        for(int i=0;i<urls.length;i++) a.add(i,urls[i]);
        this.urls=(List)a;

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
        String url = getItem(position);

        // Trigger the download of the URL asynchronously into the image view.
        Picasso.with(context) //
                .load(url) //
                .fit() //
                .tag(context) //
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}