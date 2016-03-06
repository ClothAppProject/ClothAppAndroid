package com.clothapp.upload;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.resources.Cloth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 03/03/2016.
 */
public class InfoListAdapter extends BaseAdapter {
    private final Context context;

    private ArrayList<View> listCard = new ArrayList<>();
    private AutoCompleteTextView tipo;

    private int size=0;
    private ArrayList<Cloth> listCloth=new ArrayList<>();





    public InfoListAdapter(Context context) {
        this.context = context;
    }
/*
    public InfoListAdapter(Context context, List<Cloth> cloth) {
        this.context = context;
        //this.cloths = cloth;
    }
*/


    public int getListCard() {
        return listCard.size();
    }

    public int getListCloth() {
        return listCloth.size();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Cloth getItem(int position) {
        return listCloth.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_upload_infocloth, parent, false);
        }

        //adattatore per i suggerimenti
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(row.getContext(),
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        tipo = (AutoCompleteTextView) row.findViewById(R.id.cloth);
        tipo.setAdapter(adapter);
        //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
        tipo.setThreshold(1);
        EditText shop=(EditText)row.findViewById(R.id.shop);
        EditText brand=(EditText)row.findViewById(R.id.brand);
        EditText address=(EditText)row.findViewById(R.id.address);
        EditText price=(EditText)row.findViewById(R.id.price);

      /*
        tipo.setText("");
        shop.setText("");
        brand.setText("");
        address.setText("");
        price.setText("");
        */
        final Cloth c=new Cloth();
        c.setId(size);
        //c.setCloth(tipo.getText().toString());
        //c.setShop(shop.getText().toString());
        //c.setBrand(brand.getText().toString());
        //c.setAddress(address.getText().toString());
        if (!listCloth.contains(c)){
            //System.out.println("add:"+c.getID());
            listCloth.add(c);
        }
        if(!listCard.contains(row))listCard.add(row);


        if(position==size-1)tipo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // System.out.println("ontext"+c.getID());
                //c.setCloth(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                listCloth.get(c.getID()-1).setCloth(s.toString());

            }
        });


        return row;
    }





    public void addCard() {
        size++;

    }

    //prova provvisoria dei suggerimenti
    private static final String[] COUNTRIES = new String[] {
            "Belgium", "France", "Italy", "Germany", "Spain"
    };


}
