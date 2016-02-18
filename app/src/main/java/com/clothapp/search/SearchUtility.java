package com.clothapp.search;

import android.util.Log;
import android.view.View;

import com.clothapp.resources.Image;
import com.clothapp.resources.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by niccolò on 13/02/2016.
 *
 *
 */

//FUNZIONI PER LA SEARCH BAR

public class SearchUtility {

// Cerco tutti le photo per hashtag e restituisco una lista di immagini
 public static ArrayList<Image> searchHashtag(final String s, final View vi)  {

     ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");

     final ArrayList<Image> lista=new ArrayList<Image>();
     List<ParseObject> objects= null;
     try {
         objects = queryFoto.find();
         ListIterator<ParseObject> i = objects.listIterator();
         while (i.hasNext()) {
             List<String> tag = new ArrayList<String>();
             ParseObject o = i.next();
             tag = (ArrayList) o.get("hashtag");
             if (tag == null) tag = new ArrayList<String>(0);
             for (int j = 0; j < tag.size(); j++) {
                 if (tag.get(j).contains(s)) {
                     Log.d("SearchUtility", "trovato");
                     ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
                     query.whereEqualTo("objectId", o.getObjectId());
                     ParseObject ob=query.getFirst();
                     lista.add(new Image(ob));
                     break;

                 }
             }
         }
     } catch (ParseException e) {
         e.printStackTrace();
     }
     return lista;
 }


    // Cerco tutti le photo per vestito e restituisco una lista di immagini
    public static ArrayList<Image> searchCloth(String s, final View vi) {

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        final ArrayList<Image> lista=new ArrayList<Image>();
        List<ParseObject> objects= null;
        try {
            objects = queryFoto.find();
            ListIterator<ParseObject> i = objects.listIterator();
            while (i.hasNext()) {
                List<String> tag = new ArrayList<String>();
                ParseObject o = i.next();
                tag = (ArrayList) o.get("tipo");
                if (tag == null) tag = new ArrayList<String>(0);
                for (int j = 0; j < tag.size(); j++) {
                    if (tag.get(j).contains(s)) {
                        Log.d("SearchUtility", "trovato");
                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
                        query.whereEqualTo("objectId", o.getObjectId());
                        ParseObject ob=query.getFirst();
                        lista.add(new Image(ob));
                        break;

                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return lista;
    }




    public static List<User> searchUser(String s,View vi){
//TODO fare in modo che restituisca più utenti durante la rcerca in tempo reale
        ParseQuery<ParseUser> queryFoto = ParseUser.getQuery();
        queryFoto.whereContains("username", s);
        List<User> p=new ArrayList<User>();
        try{
            List<ParseUser> o= queryFoto.find();
            for(int i=0;i<o.size();i++){
                //System.out.println(o.get(i)+"aggiunto"+o.get(i).getString("username"));
                p.add(new User(o.get(i)));
            }
        }
        catch( ParseException e){
            check(e.getCode(), vi, e.getMessage());
        }

        return p;
    }
}
















