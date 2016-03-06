package com.clothapp.upload;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.clothapp.R;
import com.clothapp.resources.Cloth;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 03/03/2016.
 */
public class InfoListAdapter extends BaseAdapter {
    private final Context context;

    private AutoCompleteTextView tipo;

    private int size=0;
    private ArrayList<Cloth> listCloth=new ArrayList<>();
    private Resources resources;
    private String output=null;


    public InfoListAdapter(Context context) {
        this.context = context;
    }
/*
    public InfoListAdapter(Context context, List<Cloth> cloth) {
        this.context = context;
        //this.cloths = cloth;
    }
*/



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

        resources = context.getResources();

        try
        {
            //Load the file from the raw folder - don't forget to OMIT the extension
            output = LoadFile("categorie", true);
            //output to LogCat
            Log.i("test", output);
        }
        catch (IOException e)
        {
            //display an error toast message
            Toast toast = Toast.makeText(context, "File: not found!", Toast.LENGTH_LONG);
            toast.show();
        }
/*
        try
        {
            //Load the file from assets folder - don't forget to INCLUDE the extension
            output = LoadFile("categorie.txt", false);
            //output to LogCat
            Log.i("test", output);
        }
        catch (IOException e)
        {
            //display an error toast message
            Toast toast = Toast.makeText(context, "File: not found!", Toast.LENGTH_LONG);
            toast.show();
        }
*/
        //adattatore per i suggerimenti
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(row.getContext(),
                android.R.layout.simple_dropdown_item_1line, output.split("\n"));
        tipo = (AutoCompleteTextView) row.findViewById(R.id.cloth);
        tipo.setAdapter(adapter);
        //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
        tipo.setThreshold(1);
        final AutoCompleteTextView shop=(AutoCompleteTextView)row.findViewById(R.id.shop);
        EditText brand=(EditText)row.findViewById(R.id.brand);
        final EditText address=(EditText)row.findViewById(R.id.address);
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



        if(position==size-1){
            tipo.addTextChangedListener(new TextWatcher() {
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

            final View finalRow = row;
            shop.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final ParseQuery<ParseObject> shopUser=new ParseQuery<ParseObject>("LocalShop");
                    shopUser.whereContains("username",s.toString());
                    shopUser.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> objects, ParseException e) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(finalRow.getContext(),android.R.layout.simple_dropdown_item_1line, shopToString(objects));
                            //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
                            shop.setAdapter(adapter);
                            shop.setThreshold(1);
                            shop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    shop.setText(objects.get(position).getString("username"));
                                    address.setText(objects.get(position).getString("address"));
                                }
                            });

                        }
                    });

                }

                @Override
                public void afterTextChanged(Editable s) {
                    listCloth.get(c.getID()-1).setShop(s.toString());
                }
            });

            brand.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    listCloth.get(c.getID()-1).setBrand(s.toString());
                }
            });

            address.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    listCloth.get(c.getID()-1).setAddress(s.toString());
                }
            });

            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    listCloth.get(c.getID()-1).setPrize(Float.parseFloat(s.toString()));
                }
            });
        }




        return row;
    }

    private String[] shopToString(List<ParseObject> objects) {
        String [] s=new String[objects.size()];
        for(int i=0;i<s.length;i++){
            s[i]=objects.get(i).getString("username")+", "+objects.get(i).getString("address");
        }
        return s;
    }


    public void addCard() {
        size++;

    }

    public void deleteCard(){
        listCloth.remove(size-1);
        size--;
    }






    //load file from apps res/raw folder or Assets folder
    public String LoadFile(String fileName, boolean loadFromRawFolder) throws IOException {
        //Create a InputStream to read the file into
        InputStream iS;

        if (loadFromRawFolder)
        {
            //get the resource id from the file name
            int rID = resources.getIdentifier("com.clothapp:raw/"+fileName, null, null);
            //get the file as a stream
            iS = resources.openRawResource(rID);
        }
        else
        {
            //get the file as a stream
            iS = resources.getAssets().open(fileName);
        }

        //create a buffer that has the same size as the InputStream
        byte[] buffer = new byte[iS.available()];
        //read the text file as a stream, into the buffer
        iS.read(buffer);
        //create a output stream to write the buffer into
        ByteArrayOutputStream oS = new ByteArrayOutputStream();
        //write this buffer to the output stream
        oS.write(buffer);
        //Close the Input and Output streams
        oS.close();
        iS.close();

        //return the output stream as a String
        return oS.toString();
    }


}
