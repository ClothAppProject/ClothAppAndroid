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


        //prendo i vari oggetti del card_layout
        TextView address = (TextView) row.findViewById(R.id.address);
        TextView shop=(TextView)row.findViewById(R.id.shop);
        TextView price=(TextView)row.findViewById(R.id.price);
        TextView brand = (TextView) row.findViewById(R.id.brand);
        TextView cloth=(TextView)row.findViewById(R.id.cloth);
        address.setText((CharSequence)cloths.get(position).getAddress());
        shop.setText((CharSequence)cloths.get(position).getShop());
        price.setText((CharSequence) cloths.get(position).getPrice());
        brand.setText((CharSequence)cloths.get(position).getBrand());
        cloth.setText((CharSequence) cloths.get(position).getCloth());


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
