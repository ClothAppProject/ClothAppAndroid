package com.clothapp.home_gallery;

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

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        if (row==null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.gridview_layout, parent, false);
        }
        //prendo i vari oggetti del gridview_layout
        SquaredImageView view = (SquaredImageView) row.findViewById(R.id.image);
        //l'id è solo una prova per vedere se si vedeva
        TextView id = (TextView) row.findViewById(R.id.id);
        id.setText(getItem(position).getObjectId());
        ImageView cuore = (ImageView) row.findViewById(R.id.cuore);

        //listener sul like della foto
        cuore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //non funziona il click sul cuore...
                System.out.printf("debug: premuto cuore");
            }
        });

        //adatto la grandezza delle immagini alla grandezza del display
        DisplayMetrics metrics =context.getResources().getDisplayMetrics();

        int w=metrics.widthPixels;
        int s=((w)/2)-25;
        row.setLayoutParams(new GridView.LayoutParams(s, s));
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        view.setPadding(0,0,0,0);
        //devo per forza ridargli la dimesione corretta altrimenti non so perchè me le resiza...
        view.getLayoutParams().height = s;
        view.getLayoutParams().width = s;
        // Get the image URL for the current position.
        File file = getItem(position).getFile();
        // Trigger the download of the URL asynchronously into the image view.
        Glide.with(context)
                .load(file)
                //.fit()
                //.tag(context)
                //.diskCacheStrategy(DiskCacheStrategy.ALL)
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