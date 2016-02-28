package com.clothapp.resources;

import android.app.Application;
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
public class ApplicationSupport extends Application {
//Questa classe viene chiamata ogni ciclo di vita intero dell'applicazione, infatti parse va invocato solamente una volta durante tutto
//il ciclo di vita dell'app mentre se si lasciava nella launcher se la launcher era richiamata andava in errore perchè si cercava
//di inizializzare parse un'altra volta.

    //inizializzo la variabile globale photos
    private ArrayList<Image> photos;
    private Date firstDate;
    //private User lastUser;
    //private Date lastCloth;
    //private Date lastTag;
    private ArrayList<User> users=new ArrayList<>();
    private ArrayList<Image> cloth=new ArrayList<>();
    private ArrayList<Image> tag=new ArrayList<>();

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }
    public void addUser(User u){
        users.add(u);
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

    public String getId(int i)  {return photos.get(i).getObjectId();}
    //getter e setter variabili globali
    public ArrayList<Image>getPhotos()   {return photos;}
    public void setPhotos(ArrayList<Image> foto)  {photos=foto;}
    public void addFirstPhoto(Image foto)    {photos.add(0,foto);}
    public void addLastPhoto(Image foto) {photos.add(foto);}
    public void setFirstDate(Date data) {firstDate= data;}
    public Date getFirstDate()  {return firstDate;}
    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());


        Parse.enableLocalDatastore(this);
        Parse.initialize(this);
        ParseFacebookUtils.initialize(this);
        try {
            if (ParseUser.getCurrentUser()!=null) {
                ParseUser.getCurrentUser().fetch();
            }
        } catch (ParseException e) {
            ExceptionCheck.check(e.getCode(),new View(this), e.getMessage());
            //errore nell'aggiornare il profilo locale
        }

    }
/*
    public User getLastUser() {
        return lastUser;
    }

    public void setLastUser(User lastUser) {
        this.lastUser = lastUser;
    }

    public void setLastCloth(Date lastCloth) {
        this.lastCloth = lastCloth;
    }

    public Date getLastCloth() {
        return lastCloth;
    }

    public Date getLastTag() {
        return lastTag;
    }

    public void setLastTag(Date lastTag) {
        this.lastTag = lastTag;
    }
    */
}