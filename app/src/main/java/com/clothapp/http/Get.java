package com.clothapp.http;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

class Get extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... uri) {
        String result = "ERRORE 90";
        StringBuffer responseString = null;
        InputStream is = null;
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                System.out.println("connesso (GET)");
            } else {
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
            if(responseString.toString()!=null) {
                result = "OK 69";
                return result;
            }
            return result;
        } catch (ClientProtocolException e) {
            Log.d("Get","ClientProtocolException= "+e.getMessage());
        } catch (IOException e) {
            Log.d("Get","Eccezione IO= "+e.getMessage());
        }
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println(result);
    }
}
