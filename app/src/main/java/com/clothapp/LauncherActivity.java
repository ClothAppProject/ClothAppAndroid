package com.clothapp;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // nascondo la status bar
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_launcher);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.clothapp",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.d("LauncherActivity", "Error: " + e.getMessage());
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e) {
            Log.d("LauncherActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Current user initialization. Parse handles all the data on its own.
        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            // The user has already logged in once before.
            Log.d("LauncherActivity", "Already logged in as " + currentUser.getUsername());

            // Skip log in page. Go directly to Splash Screen Activity.
            Intent intent = new Intent(this, SplashScreenActivity.class);
            startActivity(intent);

            // This function stops the current activity and calls OnPause(), OnStop() and OnDestroy in this order.
            finish();
        } else {
            // The current user needs to log in or sign up.
            Log.d("LauncherActivity", "Not logged in... Redirecting to login activity");

            // Go to the login/signup page.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            // This function stops the current activity and calls OnPause(), OnStop() and OnDestroy in this order.
            finish();
        }

//        if (checkIfLogged()) {
//            System.out.println("debug: logged");
//            //  redirecting user to splash screen to fetch the images and then go to the homepage
//            Intent i = new Intent(getApplicationContext(), SplashScreenActivity.class);
//            startActivity(i);
//            finish();
//        } else {
//            System.out.println("debug: not logged");
//            // redirecting the user to the main activity where he can decides to log in or sign up
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            startActivity(i);
//            finish();
//        }

    }

    //  checking if the user is logged.
    //  returns true  if the user is logged else false
//    private boolean checkIfLogged(){
//        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
//
//        //controllo se esiste il valore isLogged nelle sharedPreferences, se non esiste o è false ritorna false
//        return userInformation.getBoolean("isLogged",false);
//    }
}
