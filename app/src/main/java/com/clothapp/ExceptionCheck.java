package com.clothapp;

import android.support.design.widget.Snackbar;
import android.view.View;

import java.text.ParseException;

/**
 * Created by giacomoceribelli on 27/12/15.
 */
public class ExceptionCheck {
    public void check(int code,View vi,String message) {

        System.out.println("debug: errore = " + message);
        String output = "";
        switch (code) {
            case 124:
                //nel caso di timeout del server
                output = "Timeout del server";
                break;
            case 201:
                //nel caso in cui non sia presente la password
                output = "Password mancante";
                break;
            case 209:
                //nel caso in cui la sessione dell'utente non sia valida
                output = "Errore nella sessione dell'utente";
                break;
            case 202:
                //nel caso in cui l'username sia già stato presto
                output = "L'username esiste già";
                break;
            case 125:
                //nel caso in cui il server ritorna eccezione sulla mail
                output = "La mail inserita non è valida";
                break;
            case 100:
                //nel caso in cui il server ritorna errore di connessione
                output = "Errore, impossibile connettersi al server";
                break;
            default:
                //in qualsiasi altro caso di errore
                output = "Errore nella registrazione: "+message;
                break;
        }
        Snackbar.make(vi, output, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
