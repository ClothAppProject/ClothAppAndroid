package com.clothapp.search;

import android.util.Log;
import android.view.View;

import com.clothapp.resources.Cloth;
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
//TODO: questa classe non serve più
// Cerco tutti le photo per hashtag e restituisco una lista di immagini
 public static ArrayList<Image> searchHashtag(final String s, final View vi)  {

     ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");

     final ArrayList<Image> lista=new ArrayList<Image>();
     List<ParseObject> objects= null;

         queryFoto.findInBackground(new FindCallback<ParseObject>() {
             @Override
             public void done(List<ParseObject> objects, ParseException e) {
                 if(e==null){
                     ListIterator<ParseObject> i = objects.listIterator();
                     while (i.hasNext()) {
                         List<String> tag = new ArrayList<String>();
                         ParseObject o = i.next();
                         tag = (ArrayList) o.get("hashtag");
                         if (tag == null) tag = new ArrayList<String>(0);
                         for (int j = 0; j < tag.size(); j++) {
                             if (tag.get(j).contains(s)) {

                                 lista.add(new Image(o));

                                 break;

                             }
                         }
                     }
                 }
                 else check(e.getCode(), vi, e.getMessage());
             }
         });


     return lista;
 }


    // Cerco tutti le photo per vestito e restituisco una lista di immagini
    public static ArrayList<Image> searchCloth(final String s, final View vi) {

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        final ArrayList<Image> lista=new ArrayList<Image>();

            queryFoto.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if(e==null) {
                        ListIterator<ParseObject> i = objects.listIterator();
                        while (i.hasNext()) {
                            List<String> tag = new ArrayList<String>();
                            ParseObject o = i.next();
                            tag = (ArrayList) o.get("tipo");
                            if (tag == null) tag = new ArrayList<String>(0);
                            for (int j = 0; j < tag.size(); j++) {
                                if (tag.get(j).contains(s)) {
                                    lista.add(new Image(o));
                                    break;

                                }
                            }
                        }
                    }
                    else check(e.getCode(), vi, e.getMessage());
                }
            });


        return lista;
    }




    public static List<User> searchUser(String s, final View vi){
//TODO fare in modo che restituisca più utenti durante la rcerca in tempo reale
        ParseQuery<ParseUser> queryFoto = ParseUser.getQuery();
        queryFoto.whereContains("username", s);
        final List<User> p=new ArrayList<User>();

            queryFoto.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> o, ParseException e) {
                    if(e==null) {
                        for (int i = 0; i < o.size(); i++) {
                            //System.out.println(o.get(i)+"aggiunto"+o.get(i).getString("username"));
                            p.add(new User(o.get(i)));
                        }
                    }
                    else check(e.getCode(), vi, e.getMessage());
                }
            });




        return p;
    }

/*
    public static List<Cloth> searchCloth(List<Image>arrayList){
        //per ogni vestito cerco le informazioni
        if (arrayList == null) arrayList = new ArrayList<Cloth>();
        ArrayList<Cloth> vestiti = new ArrayList<Cloth>(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Vestito");
            query1.whereEqualTo("codice", arrayList.get(i));
            query1.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject info, ParseException e) {
                    if (e == null) {
                        Cloth c = new Cloth(info.getString("tipo"),
                                info.getString("luogoAcquisto"),
                                info.getString("prezzo"),
                                info.getString("shop"),
                                info.getString("brand"));
                        vestiti.add(c);
                        MyCardListAdapter adapter = new MyCardListAdapter(context, vestiti);
                        listView.setAdapter(adapter);
                        setListViewHeightBasedOnItems(listView);
                    }

                }
            });
        }
    }
*/


}
















