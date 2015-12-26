package com.example.giacomoceribelli.clothapp;

import android.os.AsyncTask;

import org.apache.http.client.ClientProtocolException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

    class Get extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... uri) {
            StringBuffer responseString = null;
            InputStream is= null;
            try {
                URL url = new URL(uri[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if(conn.getResponseCode() == HttpsURLConnection.HTTP_OK){
                    System.out.println("connesso");
                }
                else {
                    System.out.println("errore di connessione"); // See documentation for more info on response handling
                }
                is = conn.getInputStream();
                int ch;
                responseString = new StringBuffer();
                while ((ch = is.read()) != -1) {
                    responseString.append((char) ch);
                }
                if (is != null) {
                    is.close();
                }
                return responseString.toString();
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            System.out.println(result);
        }
    }
