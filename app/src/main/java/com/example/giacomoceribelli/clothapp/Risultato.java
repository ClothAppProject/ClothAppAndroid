package com.example.giacomoceribelli.clothapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
public class Risultato extends Activity {
    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.risultato);
        final TextView text_name = (TextView) findViewById(R.id.view_name);
        final TextView text_lastname = (TextView) findViewById(R.id.view_lastname);
        Bundle bundle = this.getIntent().getExtras();
        text_name.setText(bundle.getString("name"));
        text_lastname.setText(bundle.getString("lastname"));

        Button button = (Button) findViewById(R.id.back_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.back_button:
                        Bundle bundle = new Bundle();
                        Intent form_intent = new Intent(getApplicationContext(), MainActivity.class);
                        form_intent.putExtras(bundle);
                        startActivity(form_intent);
                        break;
                }
            }
        });
    }



}