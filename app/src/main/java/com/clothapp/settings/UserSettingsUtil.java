package com.clothapp.settings;

import android.content.Context;

import com.clothapp.LauncherActivity;
import com.clothapp.R;
import com.clothapp.upload.PlaceAutocompleteAdapter;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by giacomoceribelli on 13/03/16.
 */
public class UserSettingsUtil {

    //imposto il valore alla posizione giusta
    private static String setValue(int position, boolean value) {
        String settings = ParseUser.getCurrentUser().getString("Settings");
        char[] array = settings.toCharArray();
        if (value) {
            array[position] = '1';
        } else {
            array[position] = '0';
        }
        return String.valueOf(array);
    }

    //Salvare foto su Dispositivo POSIZIONE=0
    public static boolean checkIfSavePhotos() {
        String settings = ParseUser.getCurrentUser().getString("Settings");
        if (settings.charAt(0)=='1')    {
            return true;
        }else{
            return false;
        }
    }

    //Settare salvataggio foto
    public static void setSavePhotos(boolean value)    {
        String settings = setValue(0,value);
        ParseUser.getCurrentUser().put("Settings",settings);
        ParseUser.getCurrentUser().saveInBackground();
    }

    //Notifiche Like su Dispositivo POSIZIONE=1
    public static boolean checkNotificationLike(String username) {
        ParseUser object = null;
        if (!ParseUser.getCurrentUser().getUsername().toString().equals(username)) {
            //if username different from mine ask for the parseuser
            ParseQuery<ParseUser> user = ParseUser.getQuery();
            user.whereEqualTo("username", username);
            try {
                object = user.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            object = ParseUser.getCurrentUser();
        }
        return object.getString("Settings").charAt(1) == '1';
    }

    //Settare notifiche like
    public static void setLikeNotifications(boolean value)    {
        String settings = setValue(1,value);
        ParseUser.getCurrentUser().put("Settings",settings);
        ParseUser.getCurrentUser().saveInBackground();
    }

    //Notifiche Follower su Dispositivo POSIZIONE=2
    public static boolean checkNotificationFollower(String username) {
        ParseUser object = null;
        //if username different from mine ask for the parseuser
        if (!ParseUser.getCurrentUser().getUsername().toString().equals(username)) {
            ParseQuery<ParseUser> user = ParseUser.getQuery();
            user.whereEqualTo("username", username);
            try {
                object = user.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            object = ParseUser.getCurrentUser();
        }
        return object.getString("Settings").charAt(2) == '1';
    }

    //Settare notifiche follower
    public static void setFollowerNotifications(boolean value)    {
        String settings = setValue(2,value);
        ParseUser.getCurrentUser().put("Settings",settings);
        ParseUser.getCurrentUser().saveInBackground();
    }

    //Notifiche Nuove Foto dei following su Dispositivo POSIZIONE=3
    public static boolean checkNotificationNewPhoto(String username) {
        ParseUser object = null;
        //if username different from mine ask for the parseuser
        if (!ParseUser.getCurrentUser().getUsername().toString().equals(username)) {
            ParseQuery<ParseUser> user = ParseUser.getQuery();
            user.whereEqualTo("username", username);
            try {
                object = user.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else{
            object = ParseUser.getCurrentUser();
        }
        return object.getString("Settings").charAt(3) == '1';
    }

    //Settare notifiche follower
    public static void setNetPhotoNotifications(boolean value)    {
        String settings = setValue(3,value);
        ParseUser.getCurrentUser().put("Settings",settings);
        ParseUser.getCurrentUser().saveInBackground();
    }
}
