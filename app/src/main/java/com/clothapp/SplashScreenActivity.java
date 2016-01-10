package com.clothapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;


public class SplashScreenActivity extends AppCompatActivity {

    //  ms to wait for the splash screen
    private final int TIME_TO_WAIT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // nascondo la status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        // TODO: Questa classe mi sembra da rivedere. Troppo copia&incolla? :) (Simone)
        // TODO: serve a scaricare le immagini da mostrare nella galleria in modo asincrono
        // TODO: Android mette a disposizione questa classe invece che usare i thread. Pensi non vada bene? (Roberto)

        /**
         * Showing splashscreen while making network calls to download necessary
         * data before launching the app Will use AsyncTask to make http call
         */

//        //  calling the class to fetch the images
//        new PrefetchData().execute();

        Log.d("SplashScreenActivity", "Finito il prefetch dei dati.");

        // Create a thread which switches activity after TIME_TO_WAIT.
        Runnable r = new Runnable(){
            public void run(){
                try{
                    Thread.sleep(TIME_TO_WAIT);
                    Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
//                    SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                    startActivity(i);

                    // close this activity
                    finish();

                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        };

        Thread t = new Thread(r);

        // Start the thread.
        t.start();

//        // After completing http call
//        // will close this activity and lauch homepage activity
//        Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
//        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
//        startActivity(i);
//
//        // close this activity
//        finish();
    }

//    /**
//     * Async Task to make http call
//     */
//    private class PrefetchData extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            // before making http calls
//
//        }
//
//        //TODO  FETCHING AN ARRAY OF PICS FROM DB
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            /*
//             * Will make http call here This call will download required data
//             * before launching the app
//             * example:
//             * 1. Downloading and storing in SQLite
//             * 2. Downloading images
//             * 3. Fetching and parsing the xml / json
//             * 4. Sending device information to server
//             * 5. etc.,
//             */
//
//          /*
//            JsonParser jsonParser = new JsonParser();
//            String json = jsonParser
//                    .getJSONFromUrl("http://api.androidhive.info/game/game_stats.json");
//
//            Log.e("Response: ", "> " + json);
//
//            if (json != null) {
//                try {
//                    JSONObject jObj = new JSONObject(json)
//                            .getJSONObject("game_stat");
//                    now_playing = jObj.getString("now_playing");
//                    earned = jObj.getString("earned");
//
//                    Log.e("JSON", "> " + now_playing + earned);
//
//                } catch (JSONException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//
//            }*/
//            System.out.println("Debug: I just downloaded all the images from the server!!");
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            super.onPostExecute(result);
//
//
//        }
//
//    }

}

