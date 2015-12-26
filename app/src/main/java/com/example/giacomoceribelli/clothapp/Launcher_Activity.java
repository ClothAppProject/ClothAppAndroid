package com.example.giacomoceribelli.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Launcher_Activity extends AppCompatActivity {

    final String info= "Log Info"; //   name of the sharedPreference file. It shows whether the user is logged or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        if(checkIfLogged()) {
            //  redirecting user to splash screen to fetch the images and then go to the homepage
            Intent i = new Intent(Launcher_Activity.this, SplashScreen.class);
            startActivity(i);
            finish();
        } else {
            // redirecting the user to the main activity where he can decides to log in or sign up
            Intent i = new Intent(Launcher_Activity.this, SplashScreen.class);
            startActivity(i);
            finish();
        }

    }

    //  checking if the user is logged.
    //  returns true  if the user is logged else false
    private boolean checkIfLogged(){

        //  getting the file named "Log Info"
        SharedPreferences userInformation = getSharedPreferences(info, MODE_PRIVATE);
        boolean flag = userInformation.getBoolean("isLogged", true) ? true : false;
        return flag;
    }
}
