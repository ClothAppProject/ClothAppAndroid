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
        // nascondo la status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);


        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */

        //qui scarico le foto
        final ArrayList<Image> photo = new ArrayList<>();

        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.setLimit(12);
        query.orderByDescending("createdAt");
        try {
            List<ParseObject> fotos = query.find();
            Log.d("SplashScreenActivity", "Retrieved " + fotos.size() + " photos");
            //aggiunto la data della prima foto
            ApplicationSupport appSupport = ((ApplicationSupport) getApplicationContext());
            for (ParseObject obj : fotos) {
                ParseFile file = obj.getParseFile("thumbnail");
                //inserisco le foto in una lista che poi setto come variabile globale nella ApplicationSupport
                photo.add(new Image(file.getFile(), obj.getObjectId(),obj.getString("user"),
                        obj.getList("like"),obj.getInt("nLike"),obj.getList("hashtag"),
                        obj.getList("vestiti"), obj.getList("tipo")));
            }
            Intent intent = new Intent(getBaseContext(), HomeActivity.class);
            //prendo la variabile globale photos e ci metto dentro le immagini
            appSupport.setPhotos(photo);
            startActivity(intent);
            finish();
        } catch (ParseException e) {
            //errore nel reperire gli oggetti Photo dal database
            check(e.getCode(), new View(getApplicationContext()), e.getMessage());
        }
        Log.d("SplashScreenActivity", "Finito il prefetch dei dati.");
    }

}

