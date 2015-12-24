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
import android.widget.TextView;


public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                switch (v.getId()) {
                    case R.id.form_register_button:
                        //prendo valori delle password
                        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
                        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                        if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString()))   {
                             Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_SHORT)
                             .setAction("Action", null).show();
                        }else {
                            //prendo tutti valori, li metto nel bundle e li attacco al form intent per mandarla alla prossima activity
                            final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                            final EditText edit_mail = (EditText) findViewById(R.id.edit_mail);
                            final EditText edit_name = (EditText) findViewById(R.id.edit_name);
                            final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
                            Bundle bundle = new Bundle();
                            bundle.putString("username", edit_username.getText().toString());
                            bundle.putString("mail", edit_mail.getText().toString());
                            bundle.putString("name", edit_username.getText().toString());
                            bundle.putString("lastname", edit_username.getText().toString());
                            bundle.putString("password", edit_password.getText().toString());
                            Intent form_intent = new Intent(getApplicationContext(), Risultato.class);
                            form_intent.putExtras(bundle);
                            startActivity(form_intent);
                        }
                        break;
                }
            }
        });
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    public boolean checkPassWordAndConfirmPassword(String password,String confirmPassword)
    {
        boolean pstatus = false;
        if (confirmPassword != null && password != null)
        {
            if (password.equals(confirmPassword))
            {
                pstatus = true;
            }
        }
        return pstatus;
    }


}
