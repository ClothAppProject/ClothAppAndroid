package com.clothapp.resources;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.List;
/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class ApplicationSupport extends Application {
//Questa classe viene chiamata ogni ciclo di vita intero dell'applicazione, infatti parse va invocato solamente una volta durante tutto
//il ciclo di vita dell'app mentre se si lasciava nella launcher se la launcher era richiamata andava in errore perchè si cercava
//di inizializzare parse un'altra volta.

    //inizializzo la variabile globale photos
    private List<Image> photos;
    public List<Image>getPhotos(){
        return photos;
    }
    public void setPhotos(List<Image> foto)  {
        photos=foto;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());


        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this);
        try {
            ParseUser.getCurrentUser().fetch();
        } catch (ParseException e) {
            //errore nell'aggiornare il profilo locale
        }

    }
}