package com.clothapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseUser;

public class HomepageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);

        try {
            getSupportActionBar().setTitle(R.string.homepage_button);
        } catch (NullPointerException e) {
            Log.d("HomepageActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Create a side menu
        setUpMenu();

        // UploadActivity a new photo button initialization
        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.upload_button);

        // Add an OnClick listener to the upload button
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view_upload) {

                // Redirect the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(i);

                finish();
            }
        });

        // Logout button initialization
        Button button_logout = (Button) findViewById(R.id.form_logout_button);

        // Add an OnClick listener to the logout button
        button_logout.setOnClickListener(new View.OnClickListener() {
            //metto bottone logout in ascolto del click
            @Override
            public void onClick(View view_logout) {

                // Inizializzo la progressDialogBar
                final ProgressDialog dialog = ProgressDialog.show(HomepageActivity.this, "",
                        "Logging out. Please wait...", true);

                // Chiudo sessione e metto valore sharedPref a false

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        // Actual logout function.
                        ParseUser.logOut();

//                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
//                                userInformation.edit().putString("username", "").commit();
//                                userInformation.edit().putString("name", "").commit();
//                                userInformation.edit().putString("lastname", "").commit();
//                                userInformation.edit().putString("email", "").commit();
//                                userInformation.edit().putString("date", "").commit();
//                                userInformation.edit().putBoolean("isLogged", false).commit();

                        Log.d("HomepageActivity", "Logout eseguito con successo");

                        // Redirect the user to the Main Activity.
                        Intent form_intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(form_intent);

                        // Close the loading dialog.
                        dialog.dismiss();

                        finish();
                    }
                });

                // Start logout thread
                t.start();
            }
        });

        // ProfileActivity button initialization
        Button button_profile = (Button) findViewById(R.id.form_profile_button);

        // Add an OnClick listener to the profile button
        button_profile.setOnClickListener(new View.OnClickListener() {
            //metto bottone profile in ascolto del click
            @Override
            public void onClick(View view_profile) {

                // Redirect the user to the ProfileActivity Activity.
                Intent form_intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(form_intent);

                finish();
            }
        });
    }

    // This function creates a side menu and populates it with the given elements.
    private void setUpMenu() {

        String[] navMenuTitles;
        TypedArray navMenuIcons;

        // Load titles from string.xml
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // Load icons from strings.xml
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        set(navMenuTitles, navMenuIcons,0);

    }

}
