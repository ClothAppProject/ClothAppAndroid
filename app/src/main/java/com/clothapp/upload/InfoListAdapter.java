package com.clothapp.upload;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;

import com.clothapp.R;
import com.clothapp.resources.Cloth;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 03/03/2016.
 */
public class InfoListAdapter extends BaseAdapter {
    private final Context context;
    private List<CardView> listCard = new ArrayList<>();
    //private List<Cloth> cloths = new ArrayList<>();
    private int size=1;

    public InfoListAdapter(Context context) {
        this.context = context;
    }
/*
    public InfoListAdapter(Context context, List<Cloth> cloth) {
        this.context = context;
        //this.cloths = cloth;
    }
*/
    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return null;
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
        AutoCompleteTextView textView = (AutoCompleteTextView)
                row.findViewById(R.id.cloth);
        textView.setAdapter(adapter);
        //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
        textView.setThreshold(1);



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
