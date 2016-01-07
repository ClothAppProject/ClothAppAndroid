package com.clothapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class Homepage extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        getSupportActionBar().setTitle(R.string.homepage_button);

        setUpMenu();

        //button upload a new photo
        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view_upload) {
                // redirecting the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), Upload.class);
                startActivity(i);
                finish();
            }
        });

        //  logout button
        Button button_logout = (Button) findViewById(R.id.form_logout_button);
        button_logout.setOnClickListener(new View.OnClickListener() {
            //metto bottone logout in ascolto del click
            @Override
            public void onClick(View view_logout) {
                //inizializzo la progressDialogBar
                final ProgressDialog dialog = ProgressDialog.show(Homepage.this, "",
                        "Logging out. Please wait...", true);

                switch (view_logout.getId()) {
                    case R.id.form_logout_button:
                            //chiudo sessione e metto valore sharedPref a false
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ParseUser.logOut();
                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                userInformation.edit().putString("username", "").commit();
                                userInformation.edit().putString("name", "").commit();
                                userInformation.edit().putString("lastname", "").commit();
                                userInformation.edit().putString("email", "").commit();
                                userInformation.edit().putString("date", "").commit();
                                userInformation.edit().putBoolean("isLogged", false).commit();
                                System.out.println("debug: logout eseguito");
                                Intent form_intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(form_intent);
                                dialog.dismiss();
                                finish();
                            }
                        });
                        t.start();
                        break;
                }
            }
        });
        //  bottone profilo, fintanto che non viene implementato il menu
        Button button_profile = (Button) findViewById(R.id.form_profile_button);
        button_profile.setOnClickListener(new View.OnClickListener() {
            //metto bottone profile in ascolto del click
            @Override
            public void onClick(View view_profile) {
                // TODO Auto-generated method stub
                switch (view_profile.getId()) {
                    case R.id.form_profile_button:
                        Intent form_intent = new Intent(getApplicationContext(), Profile.class);
                        startActivity(form_intent);
                        finish();
                        break;
                }
            }
        });
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
