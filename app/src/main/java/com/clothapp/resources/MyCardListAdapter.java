package com.clothapp.resources;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageDetailFragment;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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
        String p="";
        if(cloths.get(position).getPrice()!=null)p=cloths.get(position).getPrice().toString();
        if(p.split("\\.").length>1 && p.split("\\.")[1].length()==1) p=p+"0";
        //System.out.println(p.s());
        price.setText(p);
        brand.setText((CharSequence)cloths.get(position).getBrand());
        cloth.setText((CharSequence) cloths.get(position).getCloth());
        if (cloths.get(position).getShopUsername()!=null) {
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, ShopProfileActivity.class);
                    i.putExtra("user", cloths.get(position).getShopUsername());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(i);
                }
            });
        }else{
            //TODO se il negozio non ha username e quindi non è registrato, lo rimando all'activity della search
        }

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
