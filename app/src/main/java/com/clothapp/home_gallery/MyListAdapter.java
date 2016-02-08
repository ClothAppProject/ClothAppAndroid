package com.clothapp.home_gallery;



import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.clothapp.R;

/**
 * Created by jack1 on 07/02/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.resources.Image;
import com.clothapp.resources.SquaredImageView;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.widget.ImageView.ScaleType.CENTER_CROP;

public class MyListAdapter extends BaseAdapter {
    private final Context context;
    private List<Image> files=new ArrayList<>();

    public MyListAdapter(Context context, List<Image> photos) {
        this.context = context;
        files = photos;
    }
    public void addToGridView(Image foto)   {files.add(foto);}

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        if (row==null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_cardlist, parent, false);
        }
        final Image image = getItem(position);
        //prendo i vari oggetti del gridview_layout
        ImageView view = (ImageView) row.findViewById(R.id.topfoto);
        TextView user=(TextView)row.findViewById(R.id.user);
        user.setText((CharSequence)files.get(position).getUser());


        // Get the image URL for the current position.
        File file = image.getFile();
        // Trigger the download of the URL asynchronously into the image view.

        Glide.with(context)
                .load(file)
                .centerCrop()
                .placeholder(R.mipmap.gallery_icon)
                .into(view);

        return row;
    }

    @Override public int getCount() {
        return files.size();
    }

    @Override public Image getItem(int position) {
        return (files.get(position));
    }

    @Override public long getItemId(int position) {
        return position;
    }
}