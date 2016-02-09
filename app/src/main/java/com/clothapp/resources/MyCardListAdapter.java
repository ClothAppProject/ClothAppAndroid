package com.clothapp.resources;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 09/02/2016.
 */
public class MyCardListAdapter extends BaseAdapter {
    private final Context context;
    private List<CardView> listCard=new ArrayList<CardView>();
    private List<Cloth> cloths=new ArrayList<Cloth>();

    public MyCardListAdapter(Context context,List<Cloth> cloth) {
        this.context = context;
        this.cloths=cloth;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        if (row==null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.info_cloth_card, parent, false);
        }

        CardView card=(CardView)row.findViewById(R.id.infocard);
        listCard.add(card);
   /*     final Image image = getItem(position);
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
*/
        return row;
    }

    @Override public int getCount() {
        return cloths.size();
    }

    @Override
    public Cloth getItem(int position) {
        return cloths.get(position);
    }


    @Override public long getItemId(int position) {
        return position;
    }
}
