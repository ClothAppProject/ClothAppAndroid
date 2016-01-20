package com.clothapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

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

        loadGridview();

    }

    //funzione di appoggio che viene chiamata per caricare le immagini nel gridview
    public void loadGridview()  {
        final Context contesto = this;
        final GridView gridview = (GridView) findViewById(R.id.galleria_homepage);
        final View vi = new View(this);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.setLimit(10);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fotos, ParseException e) {
                if (e == null) {
                    Log.d("Query", "Retrieved " + fotos.size() + " photos");

                    Galleria myImageAdapter = new Galleria(contesto,fotos,vi);
                    gridview.setAdapter(myImageAdapter);

                } else {
                    //errore nel reperire gli oggetti Photo dal database
                    check(e.getCode(), vi, e.getMessage());
                }
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

        set(navMenuTitles, navMenuIcons, 0);

    }
}
