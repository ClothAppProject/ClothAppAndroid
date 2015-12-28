package com.clothapp;

import android.app.LauncherActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;

public class Homepage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //  just a sample button it does nothing when clicked
        Button button_final = (Button) findViewById(R.id.final_button);

        //  logout button
        Button button_logout = (Button) findViewById(R.id.form_logout_button);
        button_logout.setOnClickListener(new View.OnClickListener() { //metto bottone logout in ascolto del click
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.form_logout_button:
                        //chiudo sessione e metto valore sharedPref a false
                        ParseUser.logOut();
                        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                        userInformation.edit().putString("username","").commit();
                        userInformation.edit().putString("name","").commit();
                        userInformation.edit().putString("lastname","").commit();
                        userInformation.edit().putString("email","").commit();
                        userInformation.edit().putString("date","").commit();
                        userInformation.edit().putBoolean("isLogged",false).commit();
                        System.out.println("debug: logout eseguito");
                        Intent form_intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(form_intent);
                        finish();
                        break;
                }
            }
        });
    }
}
