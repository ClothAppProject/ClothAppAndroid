package com.clothapp.home_gallery;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.resources.Image;
import com.clothapp.resources.SquaredImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class ImageGridViewAdapter extends BaseAdapter {
    private final Context context;
    private List<Image> files=new ArrayList<>();

    public ImageGridViewAdapter(Context context, List<Image> photos) {
        this.context = context;
        files = photos;
    }
    public void addToGridView(Image foto)   {files.add(foto);}

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(CENTER_CROP);
        }

        //adatto la grandezza delle immagini alla grandezza del display
        DisplayMetrics metrics =context.getResources().getDisplayMetrics();

        int w=metrics.widthPixels;
        int s=((w)/2)-25;
        //System.out.println(s);
        view.setLayoutParams(new GridView.LayoutParams(s, s));
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setPadding(0,0,0,0);
        // Get the image URL for the current position.
        File file = getItem(position);


        // Trigger the download of the URL asynchronously into the image view.
        Glide.with(context)
                .load(file)
                //.fit()
                //.tag(context)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
                .into(view);

        return view;
    }

    @Override public int getCount() {
        return files.size();
    }

    @Override public File getItem(int position) {
        return (files.get(position).getFile());
    }

    @Override public long getItemId(int position) {
        return position;
    }
}