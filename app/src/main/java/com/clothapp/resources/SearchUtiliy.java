package com.clothapp.resources;

import android.view.View;

import com.parse.FindCallback;
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

public class SearchUtiliy {

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
    public static ArrayList<Image> searchVestiti(String s, final View vi) {

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Vestito");
        queryFoto.whereContains("tipo", s.toString());
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
                    check(e.getCode(),vi, e.getMessage());
                }
            }
        });
        return lista;}

//cerco il primo utente con quel nome
    public static User searchUtente(String s){
//TODO fare in modo che restituisca più utenti durante la rcerca in tempo reale
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereContains("username", s);
        User p=null;
        try{
        ParseObject o=queryFoto.getFirst();
        p=new User(o);}
        catch( ParseException e){
            System.out.println(e.getCode());
        }

        return p;
    }
}
















