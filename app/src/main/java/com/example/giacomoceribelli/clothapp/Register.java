package com.example.giacomoceribelli.clothapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.giacomoceribelli.clothapp.R;
import com.google.android.gms.appdatasearch.GetRecentContextCall;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        //prendo tutti valori
        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_mail = (EditText) findViewById(R.id.edit_mail);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
        final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        final EditText edit_date = (EditText) findViewById(R.id.edit_date);

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                switch (v.getId()) {
                    case R.id.form_register_button:
                        if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString())) {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        } else {

                            //altrimenti ficco nel bundle e invio l'intent alla nuova attività
                            /*Bundle bundle = new Bundle();
                            bundle.putString("username", edit_username.getText().toString());
                            bundle.putString("mail", edit_mail.getText().toString());
                            bundle.putString("name", edit_username.getText().toString());
                            bundle.putString("lastname", edit_username.getText().toString());
                            bundle.putString("password", edit_password.getText().toString());
                            Intent form_intent = new Intent(getApplicationContext(), Risultato.class);
                            form_intent.putExtras(bundle);
                            startActivity(form_intent);
                            */

                            //creazione canale
                            try {
                                //creazione parametri
                                String dataUrlParameters = "username=" + edit_username.getText().toString();

                                // Create connection
                                URL url = new URL("http://192.168.1.2:3000/reg");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");
                                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                connection.setRequestProperty("Content-Length", "" + Integer.toString(dataUrlParameters.getBytes().length));
                                connection.setRequestProperty("Content-Language", "en-US");
                                connection.setUseCaches(false);
                                connection.setDoInput(true);
                                connection.setDoOutput(true);

                                // Send request
                                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                                wr.writeBytes(dataUrlParameters);
                                wr.flush();
                                wr.close();

                                // Get Response
                                InputStream is = connection.getInputStream();
                                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                                String line;
                                StringBuffer response = new StringBuffer();
                                while ((line = rd.readLine()) != null) {
                                    response.append(line);
                                    response.append('\r');
                                }
                                rd.close();
                                String responseStr = response.toString();
                                Log.d("Server response", responseStr);

                                System.out.println("creata connessione");
                                //inserisco tutti i parametri nella stringa da inziare via post
                                String data = "username=" + URLEncoder.encode(edit_username.getText().toString()) +
                                        "&mail=" + URLEncoder.encode(edit_mail.getText().toString()) +
                                        "&password=" + URLEncoder.encode(edit_username.getText().toString()) +
                                        "&name=" + URLEncoder.encode(edit_name.getText().toString()) +
                                        "&lastname=" + URLEncoder.encode(edit_lastname.getText().toString()) +
                                        "&date=" + URLEncoder.encode(edit_date.getText().toString());

                                //stampa codice di risposta
                                int response_code = connection.getResponseCode();
                                System.out.println(response_code);

                                //chiusura connessione
                                connection.disconnect();
                            } catch (Exception e) {
                                System.out.println("Errore nell'invio dei dati");
                            }
                        }
                        break;
                }
            }
        });
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean pstatus = true;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                pstatus = false;
            }
        }
        return pstatus;
    }


}
