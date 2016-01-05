package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class Profile extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.profile_button);

        setUpMenu();
        TextView username = (TextView) findViewById(R.id.username_field);
        TextView name = (TextView) findViewById(R.id.name_field);
        TextView lastname = (TextView) findViewById(R.id.lastname_field);
        TextView email = (TextView) findViewById(R.id.email_field);
        TextView date = (TextView) findViewById(R.id.date_field);

        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
        username.setText(userInformation.getString("username","Impossibile ottenere username").toString());
        name.setText(capitalize(userInformation.getString("name","Impossibile ottenere nome").toString()));
        lastname.setText(capitalize(userInformation.getString("lastname","Impossibile ottenere cognome").toString()));
        email.setText(userInformation.getString("email","Impossibile ottenere mail").toString());
        date.setText(userInformation.getString("date","Impossibile ottenere ").toString());
    }
    //in caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {
        // reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }
    public String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
    private void setUpMenu(){
        String[] navMenuTitles;
        TypedArray navMenuIcons;
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles,navMenuIcons);
    }
}
