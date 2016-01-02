package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.File;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class Profile extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView username = (TextView) findViewById(R.id.username_field);

        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
        username.setText(userInformation.getString("username","Impossibile ottenere nome").toString());
    }
    //in caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {
        // reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }
}
