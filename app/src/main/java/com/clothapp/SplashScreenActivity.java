package com.clothapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.clothapp.R;
import com.clothapp.home.HomeActivity;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;


public class SplashScreenActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Hide status bar.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */

        // This array will be used to store data of the photos.

        // Initialize a query to find the 12 most recent photos from Parse.
        final ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.orderByDescending("createdAt");
        query.setLimit(12);

        // Start the query in background.
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    Log.d("SplashScreenActivity", "Retrieved " + photos.size() + " photos from Parse");

                    ArrayList<Image> images = new ArrayList<>();
                    for (ParseObject photo : photos) {
                        images.add(new Image(photo));
                    }


                    ApplicationSupport applicationSupport = ((ApplicationSupport) getApplicationContext());
                    applicationSupport.setPhotos(images);

                    Log.d("SplashScreenActivity", "Photo prefetch successfully completed");

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    Log.d("SplashScreenActivity", "Error: " + e.getMessage());
                }
            }
        });

//        try {
//            List<ParseObject> fotos = query.find();
//            Log.d("SplashScreenActivity", "Retrieved " + fotos.size() + " photos");
//            ApplicationSupport appSupport = ((ApplicationSupport) getApplicationContext());
//            for (ParseObject obj : fotos) {
//                ParseFile file = obj.getParseFile("thumbnail");
//                //inserisco le foto in una lista che poi setto come variabile globale nella ApplicationSupport
//                photo.add(new Image(file.getFile(), obj.getObjectId(), obj.getString("user"),
//                        obj.getList("like"), obj.getInt("nLike"), obj.getList("hashtag"),
//                        obj.getList("vestiti"), obj.getList("tipo")));
//            }
//            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
//            //prendo la variabile globale photos e ci metto dentro le immagini
//            appSupport.setPhotos(photo);
//            startActivity(intent);
//            finish();
//        } catch (ParseException e) {
//            //errore nel reperire gli oggetti Photo dal database
//            check(e.getCode(), new View(getApplicationContext()), e.getMessage());
//        }
    }

}

