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
        Button button = (Button) findViewById(R.id.form_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.form_button:
                        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
                        final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", edit_name.getText().toString());
                        bundle.putString("lastname", edit_lastname.getText().toString());
                        Intent form_intent = new Intent(getApplicationContext(), Risultato.class);
                        form_intent.putExtras(bundle);
                        startActivity(form_intent);
                        break;
                }
            }
        });
    }


}
