package com.clothapp.profile_shop.fragments;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Jacopo on 27/04/2016.
 */

public class ProfileShopMapFragment extends Fragment {

    private String username;
    private Context context;
    private RecyclerView viewProfileInfo;
    public ProfileShopMapFragment() {

    }

    public static ProfileShopMapFragment newInstance(String username, Context context) {
        ProfileShopMapFragment fragment = new ProfileShopMapFragment();
        fragment.username = username;
        fragment.context = context;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the fragment which will contain the RecyclerView
        View rootView;

        try {
            rootView = inflater.inflate(R.layout.fragment_profileshop_map, container, false);
        }catch (Exception e2){
            rootView = inflater.inflate(R.layout.fragment_profile_info,container,false);
        }

        final SupportMapFragment mapFragment= (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapshop);

        final GoogleMap map = mapFragment.getMap();
        final String[] via = {""};

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("LocalShop");
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    ParseObject shop = object;
                    // showDialog(context, "Success", "Successfully retrieved shop info from Parse.");

                    //controllo in caso di valori nulli
                    if (!(shop.getString("address") == null && shop.getString("webSite") == null)) {
                        if ((shop.getString("address") != null) && (shop.getString("address").length()>0)) {
                            via[0] = shop.getString("address");
                            if (shop.getString("Città") != null)
                                via[0] += (" " + shop.getString("Citta"));
                            try {
                                LatLng point = getLocationFromAddress(context, via[0]);
                                Marker TP = map.addMarker(new MarkerOptions().position(point).title("Point"));
                                map.getUiSettings().setZoomGesturesEnabled(true);
                                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 12.0f));
                            }
                            catch (Exception e1){
                                System.out.println("errore");
                            }
                        } else {
                            via[0] = "roma";
                            map.getUiSettings().setZoomGesturesEnabled(true);
                        }

                    }

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve shop info. Check your Internet connection.");
                }
            }
        });


        // Return the fragment
        return rootView;
    }

    public LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }

}