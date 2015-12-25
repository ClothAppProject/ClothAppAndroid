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
                        if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString()))   {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_SHORT)
                             .setAction("Action", null).show();
                        }else {

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
                                //preparazione della connessione

                                System.out.println("creazione connessione");
                                URL url = new URL("http://192.168.1.2:3000/reg");
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("POST");
                                connection.setDoInput(false);
                                connection.setDoOutput(true);


                                /*HashMap<String , String> postDataParams = new HashMap<String, String>();

                                postDataParams.put("username", edit_username.getText().toString());

                                performPostCall("http://192.168.1.2:3000/reg", postDataParams);
*/
                                System.out.println("creata connessione");
                                //inserisco tutti i parametri nella stringa da inziare via post
                                String data = "username=" + URLEncoder.encode(edit_username.getText().toString()) +
                                        "&mail=" + URLEncoder.encode(edit_mail.getText().toString()) +
                                        "&password=" + URLEncoder.encode(edit_username.getText().toString()) +
                                        "&name=" + URLEncoder.encode(edit_name.getText().toString()) +
                                        "&lastname=" + URLEncoder.encode(edit_lastname.getText().toString()) +
                                        "&date=" + URLEncoder.encode(edit_date.getText().toString());


                                connection.getOutputStream().write(data.getBytes("UTF8"));
                                System.out.println("Dati inviati");
                                //preparo un output sul quale inserisco i dati e lo invio
                                /*
                                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                                System.out.println("preparati dati");
                                out.writeBytes(data);
                                out.flush();
                                out.close();
                                System.out.println("chiuso stream dati");
                                //leggo risposta della connesione

                                int response_code = connection.getResponseCode();
                                System.out.println(response_code);
                                */
                            }catch (Exception e)  {
                                System.out.println("Errore nell'invio dei dati");
                            }
                        }
                        break;
                }
            }
        });
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    private boolean checkPassWordAndConfirmPassword(String password,String confirmPassword)
    {
        boolean pstatus = true;
        if (confirmPassword != null && password != null)
        {
            if (password.equals(confirmPassword))
            {
                pstatus = false;
            }
        }
        return pstatus;
    }
    public String performPostCall(String requestURL,
                                  HashMap<String, String> postDataParams) {

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;

                    Log.e("Res:", response);
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }



}
