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
            case 1:
                //nel caso in cui ci sia un errore interno al server
                output = "Errore interno al server";
                break;
            case 2:
                //nel caso in cui il servizio sia temporaneamente non disponibile
                output = "Servizio momentaneamente non disponibile";
                break;
            case 4:
                //nel caso in cui il client sia disconnesso
                output = "Errore di connessione";
                break;
            case 100:
                //nel caso in cui il server ritorna errore di connessione
                output = "Errore, impossibile connettersi al server";
                break;
            case 101:
                ////nel caso in cui i parametri de login siano sbagliati
                //nel caso in cui l'oggetto di ricerca non è stato trovato
                output = "Oggetto non trovato";
                break;
            case 102:
                //nel caso in cui la query non sia valida
                output = "Errore nella ricerca";
                break;
            case 105:
                //nel caso in cui il nome del campo non sia valido
                output ="Nome del campo non valido";
                break;
            case 107:
                //nel caso in cui il formato JSON non è valido
                output ="Formato del file JSON non valido";
                break;
            case 109:
                //nel caso in cui la libreria Parse non sia stata inizializzata
                output ="Libreria Parse non inizializzata";
                break;
            case 116:
                //nel caso in cui il valore del limite non sia valido
                output ="Valore del limite non valido";
                break;
            case 117:
                //nel caso in cui manca la dimensione del file
                output ="Dimensione del file non specificata";
                break;
            case 120:
                //nel caso di cacheMiss del server
                output ="Errore di cache miss del server";
                break;
            case 122:
                //nel caso di nome file non valido
                output ="Nome del file non ammesso";
                break;
            case 124:
                //nel caso di timeout del server
                output = "Timeout del server";
                break;
            case 125:
                //nel caso in cui il server ritorna eccezione sulla mail
                output = "La mail inserita non è valida";
                break;
            case 126:
                //nel caso in cui il tipo del file non specificato
                output ="Tipo del file non specificato";
                break;
            case 127:
                //nel caso in cui manca la dimensione del file
                output ="Dimensione del file non specificata";
                break;
            case 128:
                //nel caso in cui la dimensione del file non sia valida
                output ="Dimensione del file non valida";
                break;
            case 129:
                //nel caso in cui la dimensione del file sia troppo grande
                output ="Dimensione del file è troppo grande";
                break;
            case 130:
                //nel caso in cui il file non sia stato salvato
                output ="Errore nel salvataggio del file";
                break;
            case 131:
                //nel caso in cui i file non possa essere eliminato
                output ="Impossibile eliminare il file";
                break;
            case 137:
                //nel caso in cui il valore del campo che è unico è già presente
                output ="Identificatore già utilizzato";
                break;
            case 150:
                //nel caso in cui i dati dell'immagine non siano validi
                output ="Dati dell'immagine non validi";
                break;
            case 151:
                //nel caso in cui il file non è stato salvato
                output ="Il file non è stato salvato";
                break;
            case 152:
                //nel caso in cui la data del push non è valida
                output ="Data della push non valida";
                break;
            case 158:
                //nel caso in cui ci sia un errore dell'hosting
                output ="Errore dell'hosting";
                break;
            case 200:
                //nel caso in cui il campo username sia mancante o vuoto
                output = "Username mancante";
                break;
            case 201:
                //nel caso in cui non sia presente la password
                output = "Password mancante";
                break;
            case 202:
                //nel caso in cui l'username sia già stato presto
                output = "L'username esiste già";
                break;
            case 203:
                //nel caso in cui l'email sia già stata utilizzata
                output = "L'email è già stata utilizzata";
                break;
            case 204:
                //nel caso in cui l'email sia mancante
                output = "Email mancante";
                break;
            case 205:
                //nel caso in cui non è stato trovato alcun username con quella email
                output = "Nessun utente corrisponde a questa email";
                break;
            case 206:
                //nel caso in cui non è presente la sessione dell'utente
                output = "Sessione mancante";
                break;
            case 208:
                //nel caso in cui l'account è già stato collegato ad un altro utente
                output = "Account già collegato ad un altro utente";
                break;
            case 209:
                //nel caso in cui la sessione dell'utente non sia valida
                output = "Errore nella sessione dell'utente";
                break;
            default:
                //in qualsiasi altro caso di errore
                output = "Errore nella registrazione: "+message;
                break;
        }
        Snackbar.make(vi, output, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
