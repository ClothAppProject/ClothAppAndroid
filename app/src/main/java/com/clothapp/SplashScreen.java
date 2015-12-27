package com.clothapp;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.content.Intent;


public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        //controllo se utente è sloggato
        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
        if (!userInformation.getBoolean("isLogged",true)) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            // close this activity
            finish();
        }
        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */
        new PrefetchData().execute();

    }

    /**
     * Async Task to make http call
     */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // before making http calls

        }

        //TODO  FETCHING AN ARRAY OF PICS FROM DB
        @Override
        protected Void doInBackground(Void... arg0) {
            /*
             * Will make http call here This call will download required data
             * before launching the app
             * example:
             * 1. Downloading and storing in SQLite
             * 2. Downloading images
             * 3. Fetching and parsing the xml / json
             * 4. Sending device information to server
             * 5. etc.,
             */

          /*
            JsonParser jsonParser = new JsonParser();
            String json = jsonParser
                    .getJSONFromUrl("http://api.androidhive.info/game/game_stats.json");

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {
                    JSONObject jObj = new JSONObject(json)
                            .getJSONObject("game_stat");
                    now_playing = jObj.getString("now_playing");
                    earned = jObj.getString("earned");

                    Log.e("JSON", "> " + now_playing + earned);

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }*/
            System.out.println("I just downloaded all the images from the server!!");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // After completing http call
            // will close this activity and lauch main activity
            Intent i = new Intent(getApplicationContext(), MainActivity.class);

            SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
            if (userInformation.getBoolean("isLogged",false))   {
                i = new Intent(getApplicationContext(), Homepage.class);
            }
            startActivity(i);

            // close this activity
            finish();
        }

    }

}

