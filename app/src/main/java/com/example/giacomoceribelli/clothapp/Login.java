package com.example.giacomoceribelli.clothapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.giacomoceribelli.clothapp.R;


public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        Button button = (Button) findViewById(R.id.form_login_button);
        button.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.form_login_button:
                        //prendo tutti valori, li metto nel bundle e li attacco al form intent per mandarla alla prossima activity
                        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                        if (checknull(edit_password.getText().toString(),edit_username.getText().toString())) {
                            Snackbar.make(v, "I campi non devono essere vuoti", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                        }else {
                            Bundle bundle = new Bundle();
                            bundle.putString("username", edit_username.getText().toString());
                            bundle.putString("password", edit_password.getText().toString());
                            Intent form_intent = new Intent(getApplicationContext(), Register.class);
                            form_intent.putExtras(bundle);
                            startActivity(form_intent);
                        }
                        break;
                }
            }
        });
    }

    public boolean checknull(String p,String u) {
        if (p.equals("") || u.equals("")) return true;
        return false;
    }


}
