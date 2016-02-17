package com.clothapp.resources;

import android.view.View;

import com.parse.FindCallback;
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
 public static ArrayList<Image> searchHashtag(String s, final View vi)  {

     ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
     queryFoto.whereContains("hashtag",s);
     final ArrayList<Image> lista=new ArrayList<Image>();
     queryFoto.findInBackground(new FindCallback<ParseObject>() {
         @Override
         public void done(List<ParseObject> objects, ParseException e) {
             if (e == null) {
                 ListIterator<ParseObject> i=objects.listIterator();
                 while(i.hasNext()){
                     ParseObject o=i.next();
                     lista.add(new Image(o));
                 }
             } else {
                 check(e.getCode(),vi, e.getMessage());;
             }
         }
     });
     return lista;}


    // Cerco tutti le photo per vestito e restituisco una lista di immagini
    public static List<Image> searchCloth(String s, final View vi) {

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("tipo", s.toString());
        final ArrayList<Image> lista=new ArrayList<Image>();
        List<String>cloth=new ArrayList<String>();
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ListIterator<ParseObject> i=objects.listIterator();
                    while(i.hasNext()){
                        ParseObject o=i.next();
                        lista.add(new Image(o));
                    }
                } else {
                    check(e.getCode(),vi, e.getMessage());
                }
            }
        });
        return lista;}



//cerco il primo utente con quel nome
    public static List<User> searchUser(String s,View vi){
//TODO fare in modo che restituisca più utenti durante la rcerca in tempo reale
        ParseQuery<ParseUser> queryFoto = ParseUser.getQuery();
        queryFoto.whereContains("username", s);
        List<User> p=new ArrayList<User>();
        System.out.println(s);
        try{
            System.out.println(s);
            List<ParseUser> o= queryFoto.find();
            System.out.println(o.size());
            for(int i=0;i<o.size();i++){
                System.out.println(o.get(i)+"aggiunto"+o.get(i).getString("username"));
                p.add(new User(o.get(i)));
            }
        }
        catch( ParseException e){
            check(e.getCode(), vi, e.getMessage());
        }

        return p;
    }
}
















