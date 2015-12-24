package com.example.giacomoceribelli.clothapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*FloatingActionButton centro = (FloatingActionButton) findViewById(R.id.centro);
        centro.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "allahk bar", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });*/
        Button login = (Button) findViewById(R.id.login_button);
        Button register = (Button) findViewById(R.id.register_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.login_button:
                        Bundle bundle = new Bundle();
                        Intent form_intent = new Intent(getApplicationContext(), Login.class);
                        form_intent.putExtras(bundle);
                        startActivity(form_intent);
                        break;
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.register_button:
                        Bundle bundle = new Bundle();
                        Intent form_intent = new Intent(getApplicationContext(), Register.class);
                        form_intent.putExtras(bundle);
                        startActivity(form_intent);
                        break;
                }
            }
        });
    }


}
