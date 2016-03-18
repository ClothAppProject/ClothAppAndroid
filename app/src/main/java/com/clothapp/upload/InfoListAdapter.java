package com.clothapp.upload;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
public class InfoListAdapter extends BaseAdapter implements GoogleApiClient.OnConnectionFailedListener {
    private final Context context;

    private AutoCompleteTextView tipo;


    private ArrayList<Cloth> listCloth = new ArrayList<>(3);
    private Resources resources;
    private String output = null;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(41.9027835, 12.4963655), new LatLng(42.9027835, 13.4963655));

    protected GoogleApiClient mGoogleApiClient;

    private PlaceAutocompleteAdapter mAdapter;
    private String output2=null;
    private AutoCompleteTextView shop;
    private AutoCompleteTextView brand;
    private AutoCompleteTextView address;
    private EditText price;


    public InfoListAdapter(Context context, GoogleApiClient googleApiClient) {
        this.context = context;
        this.mGoogleApiClient = googleApiClient;
        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        mAdapter = new PlaceAutocompleteAdapter(context, mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        addCard();
    }
/*
    public InfoListAdapter(Context context, List<Cloth> cloth) {
        this.context = context;
        //this.cloths = cloth;
    }
*/




    @Override
    public int getCount() {
        return listCloth.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        System.out.println("getView "+(listCloth.get(position))+" position "+position);
        View row = convertView;
        //if (row == null) {

            //se la convertView di quest'immagine è nulla la inizializzo
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.fragment_upload_infocloth, parent, false);
        //}

        resources = context.getResources();


        try {
            //Load the file from the raw folder - don't forget to OMIT the extension
            output = LoadFile("categorie", true);
            //output to LogCat
            // Log.d("test", output);
        } catch (IOException e) {
            //display an error toast message
            Toast toast = Toast.makeText(context, "File: not found!", Toast.LENGTH_LONG);
            toast.show();
        }

        try {
            //Load the file from the raw folder - don't forget to OMIT the extension
            output2 = LoadFile("brand", true);
            //output to LogCat
            // Log.d("test", output);
        } catch (IOException e) {
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(row.getContext(),
                android.R.layout.simple_dropdown_item_1line, output.split("\n"));
        tipo = (AutoCompleteTextView) row.findViewById(R.id.cloth);
        tipo.setAdapter(adapter);
        //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
        tipo.setThreshold(1);
        shop = (AutoCompleteTextView) row.findViewById(R.id.shop);
        brand = (AutoCompleteTextView) row.findViewById(R.id.brand);
        address = (AutoCompleteTextView) row.findViewById(R.id.address);
        price = (EditText) row.findViewById(R.id.price);

        //adattatore per i suggerimenti
        final ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(row.getContext(),
                android.R.layout.simple_dropdown_item_1line, output2.split("\n"));
        brand.setAdapter(adapter2);
        brand.setThreshold(1);


        if(tipo!=null) tipo.setText(getItem(position).getCloth());
        if(shop!=null) shop.setText(getItem(position).getShop());
        if(address!=null) address.setText(getItem(position).getAddress());
        if(brand!=null) brand.setText(getItem(position).getBrand());
        if(price!=null){
            if(listCloth.get(position).getPrice()!=null)price.setText(getItem(position).getPrice().toString());
            else price.setText("");
        }



        //final Cloth c=listCloth.get(position);
        //c.setId(listCloth.size());
        //c.setCloth(tipo.getText().toString());
        //c.setShop(shop.getText().toString());
        //c.setBrand(brand.getText().toString());
        //c.setAddress(address.getText().toString());
       /*
        if (!listCloth.contains(c)) {
            //System.out.println("add:"+c.getID());
            //listCloth.add(c);
        }
        */


        if (position == listCloth.size() - 1) {

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
                    try{
                        listCloth.get(position).setCloth(s.toString().trim());
                    }catch (Exception e){

                    }

                }
            });

            final View finalRow = row;

            shop.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    //System.out.println("qui");
                    final ParseQuery<ParseObject> shopUser = new ParseQuery<ParseObject>("LocalShop");
                    shopUser.whereContains("lowercase", s.toString().toLowerCase());
                    shopUser.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(final List<ParseObject> objects, ParseException e) {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(finalRow.getContext(), android.R.layout.simple_dropdown_item_1line, shopToString(objects));
                            //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
                            shop.setAdapter(adapter);
                            shop.setThreshold(1);
                            shop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    shop.setText(objects.get(position).getString("username"));
                                    //System.out.println(objects.get(position).getString("address").length() == 0);
                                    //System.out.println(objects.get(position).getString("webSite"));
                                    //System.out.println(objects.get(position).getString("address"));
                                    if(objects.get(position).getString("address")==null || objects.get(position).getString("address").length()==0)  address.setText(objects.get(position).getString("webSite"));
                                    else address.setText(objects.get(position).getString("address"));

                                }
                            });

                        }
                    });

                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        listCloth.get(position).setShop(s.toString().trim());
                    }
                    catch (Exception e){

                    }
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
                    try{
                        listCloth.get(position).setBrand(s.toString().trim());
                    }catch (Exception e){

                    }
                }
            });

            address.setThreshold(4);
            address.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               }

               @Override
               public void onTextChanged(CharSequence s, int start, int before, int count) {

                   if (s.length()<=3) address.setAdapter(null);
                   else {


                       if (!s.toString().toLowerCase().contains("www") && !s.toString().toLowerCase().contains("http")) {
                           // Register a listener that receives callbacks when a suggestion has been selected
                           address.setOnItemClickListener(mAutocompleteClickListener);

                           // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
                           // the entire world.
                           address.setAdapter(mAdapter);
                       }

                       else {

                           final ParseQuery<ParseObject> shopUser = new ParseQuery<ParseObject>("LocalShop");
                           shopUser.whereContains("webSite", s.toString().toLowerCase());
                           shopUser.findInBackground(new FindCallback<ParseObject>() {
                               @Override
                               public void done(final List<ParseObject> objects, ParseException e) {
                                  // System.out.println("trovati!!!" + objects);
                                   ArrayAdapter<String> adapter = new ArrayAdapter<String>(finalRow.getContext(), android.R.layout.simple_dropdown_item_1line, shopToString(objects));
                                   //appena si preme una lettera appaiono i suggerimenti. Il minimo è 1
                                   address.setAdapter(adapter);
                                   address.setThreshold(3);
                                   address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                       @Override
                                       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                           shop.setText(objects.get(position).getString("username"));
                                           //System.out.println(objects.get(position).getString("address").length() == 0);
                                           //System.out.println(objects.get(position).getString("webSite"));
                                           //System.out.println(objects.get(position).getString("address"));
                                           if (objects.get(position).getString("address") == null || objects.get(position).getString("address").length() == 0)
                                               address.setText(objects.get(position).getString("webSite"));
                                           else
                                               address.setText(objects.get(position).getString("address"));

                                       }
                                   });

                               }
                           });
                       }
                   }

               }

               @Override
               public void afterTextChanged(Editable s) {
                   try{
                       listCloth.get(position).setAddress(s.toString().trim());
                   }catch (Exception e){

                   }

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
                    try {
                        if (!s.toString().equals(""))
                            listCloth.get(position).setPrize(Float.parseFloat(s.toString().trim()));
                        else listCloth.get(position).setPrize(null);
                    }catch (Exception e){

                    }
                }
            });
        }


        return row;
    }

    private String[] shopToString(List<ParseObject> objects) {
        if (objects == null) return new String[]{};
        String[] s = new String[objects.size()];
        for (int i = 0; i < s.length; i++) {
            s[i] = objects.get(i).getString("username");
            if( objects.get(i).getString("address")!=null &&  objects.get(i).getString("address").length()>0) s[i]+= ", " + objects.get(i).getString("address");
            else s[i]+= ", " + objects.get(i).getString("webSite");
        }
        return s;
    }


    public void addCard() {
        //System.out.println("addCard");
        Cloth c=new Cloth();
        c.setId(listCloth.size() + 1);
        /*
        if(tipo!=null) tipo.setText("");
        if(shop!=null) shop.setText("");
        if(address!=null) address.setText("");
        if(brand!=null) brand.setText("");
        if(price!=null) price.setText("");
        */
        listCloth.add(c);
        System.out.println(listCloth.size());

    }

    public void deleteCard() {
        if(listCloth.size()>1) {
            ArrayList<Cloth> a=new ArrayList<Cloth>();
            for (int i=0;i<listCloth.size()-1;i++){
                a.add(listCloth.get(i));
            }
            listCloth=a;
        }
    }


    //load file from apps res/raw folder or Assets folder
    public String LoadFile(String fileName, boolean loadFromRawFolder) throws IOException {
        //Create a InputStream to read the file into
        InputStream iS;

        if (loadFromRawFolder) {
            //get the resource id from the file name
            int rID = resources.getIdentifier("com.clothapp:raw/" + fileName, null, null);
            //get the file as a stream
            iS = resources.openRawResource(rID);
        } else {
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


    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            System.out.println("mAdapter=" + mAdapter + ":");
            final AutocompletePrediction item = mAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);



            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Toast.makeText(context, "Clicked: " + primaryText,
                    Toast.LENGTH_SHORT).show();

        }
    };

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully

                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            System.out.println(place.getName() +
                    place.getId() + place.getAddress() + place.getPhoneNumber() +
                    place.getWebsiteUri());

            // Display the third party attributions if set.
  /*
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution == null) {
                mPlaceDetailsAttribution.setVisibility(View.GONE);
            } else {
                mPlaceDetailsAttribution.setVisibility(View.VISIBLE);
                mPlaceDetailsAttribution.setText(Html.fromHtml(thirdPartyAttribution.toString()));
            }
*/


            places.release();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO(Developer): Check error code and notify the user of error state and resolution.
        System.out.println("error connection");
    }
}
