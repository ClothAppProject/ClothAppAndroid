package com.clothapp.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.resources.Image;
import com.clothapp.resources.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 16/02/2016.
 */
public class SearchAdapterUser extends BaseAdapter {
    private final Context context;
    private List<User> users=new ArrayList<>();
    List<Image> foto= new ArrayList<>();

    public SearchAdapterUser(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
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
            row = inflater.inflate(R.layout.search_item_user, parent, false);

        }

        ImageView i=(ImageView)row.findViewById(R.id.foto);
   //     Glide.with(context)            .load(foto.get(position).getFile()) .into(i);
        TextView t=(TextView)row.findViewById(R.id.user);
        t.setText(getItem(position).getUsername());
        return row;
    }
}
