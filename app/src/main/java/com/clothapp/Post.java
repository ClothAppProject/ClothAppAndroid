package com.clothapp;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

class Post extends AsyncTask<String, String, String>{

    public String performPostCall(String requestURL, HashMap<String,String> data) {

        URL url;
        String response = "";
        try {
            //prendo url e apro connessione conn
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");

            System.out.println("debug: "+getPostDataString(data));
            //inizializzo output streame, e li converto in stringa con la funzione getPostDataString e li scrivo
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes(getPostDataString(data));
            wr.flush();
            wr.close();



            /*OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));
            writer.flush();
            writer.close();
            os.close();*/
            int responseCode=conn.getResponseCode();
            System.out.println("response code: "+responseCode);
            //controllo che la riposta del server sia ok
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                //inizio a leggere la risposta del server
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
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

    //funzione per creare una stringa passandogli un hashmap contenente tutti i parametri da passare via post
    private String getPostDataString(HashMap<String,String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        Set<String> keySet = params.keySet();
        Iterator<String> it = keySet.iterator();
        while (it.hasNext()) {
            if (first)
                first = false;
            else
                result.append("&");
            String key = it.next();
            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.get(key), "UTF-8"));
        }
        /*
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
*/
        return result.toString();
    }

    @Override
    protected String doInBackground(String... params) {
        HashMap<String,String> data = new HashMap<String, String>();
        for (int i=1;i<params.length;i+=2)   {
            data.put(params[i],params[i+1]);
        }
        return performPostCall(params[0],data);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println(result);
    }
}
