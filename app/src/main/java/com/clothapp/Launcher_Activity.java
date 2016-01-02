package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.parse.Parse;

public class Launcher_Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //nascondo la status bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if(checkIfLogged()) {
            System.out.println("debug: logged");
            //  redirecting user to splash screen to fetch the images and then go to the homepage
            Intent i = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(i);
            finish();
        } else {
            System.out.println("debug: not logged");
            // redirecting the user to the main activity where he can decides to log in or sign up
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        }

    }

    //  checking if the user is logged.
    //  returns true  if the user is logged else false
    private boolean checkIfLogged(){
        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);

        //controllo se esiste il valore isLogged nelle sharedPreferences, se non esiste o è false ritorna false
        return userInformation.getBoolean("isLogged",false);
    }
}
