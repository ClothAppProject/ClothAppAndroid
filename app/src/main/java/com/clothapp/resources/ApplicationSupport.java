package com.clothapp.resources;

import android.app.Application;
import android.util.Log;
import android.view.View;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by giacomoceribelli on 02/01/16.
 */


//Questa classe viene chiamata ogni ciclo di vita intero dell'applicazione, infatti parse va invocato solamente una volta durante tutto
//il ciclo di vita dell'app mentre se si lasciava nella launcher se la launcher era richiamata andava in errore perchè si cercava
//di inizializzare parse un'altra volta.

public class ApplicationSupport extends Application {

    // Inizializzo la variabile globale photos
    private ArrayList<Image> photos;
    private ArrayList<User> users = new ArrayList<>();
    private ArrayList<Image> cloth = new ArrayList<>();
    private ArrayList<Image> tag = new ArrayList<>();

//    private User lastUser;
//    private Date lastCloth;
//    private Date lastTag;

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public ArrayList<Image> getCloth() {
        return cloth;
    }

    public void setCloth(ArrayList<Image> cloth) {
        this.cloth = cloth;
    }

    public ArrayList<Image> getTag() {
        return tag;
    }

    public void setTag(ArrayList<Image> tag) {
        this.tag = tag;
    }

    public ArrayList<Image> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Image> photos) {
        this.photos = photos;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());

        // Initialize Parse SDK
        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this);
        try {
            if (ParseUser.getCurrentUser() != null) {
                ParseUser.getCurrentUser().fetch();
            }
        } catch (ParseException e) {
            // Errore nell'aggiornare il profilo locale
            ExceptionCheck.check(e.getCode(), new View(this), e.getMessage());
            Log.d("ApplicationSupport", "ParseUser.getCurrentUser().fetch() failed...");
            Log.d("ApplicationSupport", "Error: " + e.getMessage());
        }

    }


    // These methods are not used at the moment

//    public void addFirstPhoto(Image photo) {
//        photos.add(0, photo);
//    }
//
//    public void addLastPhoto(Image photo) {
//        photos.add(photo);
//    }
//
//    public String getId(int i) {
//        return photos.get(i).getObjectId();
//    }
//
//    public void addUser(User u) {
//        users.add(u);
//    }

}