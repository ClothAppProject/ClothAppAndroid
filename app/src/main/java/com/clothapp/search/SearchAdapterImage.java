package com.clothapp.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.User;

import java.util.ArrayList;
import java.util.List;
import com.clothapp.resources.Image;

/**
 * Created by nc94 on 2/17/16.
 */
public class SearchAdapterImage extends BaseAdapter {
    private final Context context;
    private List< Image > image=new ArrayList<>();


    public SearchAdapterImage(Context context, List<Image> image) {
        this.context = context;
        this.image = image;

    }
    @Override
    public int getCount() {
        return image.size();
    }

    @Override
    public Image getItem(int position) {
        return image.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row==null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.search_item_image, parent, false);

        }

        TextView tipi=(TextView)row.findViewById(R.id.tipo);
        tipi.setText((CharSequence) image.get(position).getTypeVestitiToString());


        TextView hashtag=(TextView)row.findViewById(R.id.hashtag);
        hashtag.setText((CharSequence) image.get(position).getHashtagToString());

        ImageView imageView=(ImageView)row.findViewById(R.id.foto);
        Glide.with(context)
                .load(image.get(position).getFile())
                .into(imageView);

        TextView t=(TextView)row.findViewById(R.id.user);
        t.setText(getItem(position).getUser());

        return row;
    }

}
