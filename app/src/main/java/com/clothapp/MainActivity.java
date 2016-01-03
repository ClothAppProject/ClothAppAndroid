package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static Resources.ExceptionCheck.check;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button login = (Button) findViewById(R.id.login_button);
        Button register = (Button) findViewById(R.id.register_button);
        login.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.login_button:
                        final View vi = v;
                        //prendo tutti valori, li metto nel bundle e li attacco al form intent per mandarla alla prossima activity
                        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                        if (checknull(edit_password.getText().toString().trim(),edit_username.getText().toString().trim())) {
                            Snackbar.make(v, "I campi non devono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }else{
                            try {
                                ParseUser.logIn(edit_username.getText().toString().trim(),edit_password.getText().toString().trim());
                                System.out.println("debug: Login eseguito correttamente");

                                //inserisco i valori nelle sharedPref
                                ParseUser uth = ParseUser.getCurrentUser();
                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                userInformation.edit().putBoolean("isLogged",true).commit();
                                userInformation.edit().putString("username",uth.get("username").toString().trim()).commit();
                                userInformation.edit().putString("name",uth.get("name").toString().trim()).commit();
                                userInformation.edit().putString("lastname",uth.get("lastname").toString().trim()).commit();
                                userInformation.edit().putString("date",uth.get("date").toString().trim()).commit();
                                userInformation.edit().putString("email",uth.get("email").toString().trim()).commit();

                                Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                                startActivity(form_intent);
                                finish();
                            }catch (ParseException e) {
                                if (e.getCode()==101)   {
                                    //siccome il codice 101 è per 2 tipi di errori faccio prima il controllo qua e in caso chiamo gli altri
                                    System.out.println("debug: errore = " +e.getMessage());
                                    Snackbar.make(v, "Username o Password errati", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }else {
                                    check(e.getCode(), v, e.getMessage());
                                }
                            }
                        }
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
    public boolean checknull(String p,String u) {
        if (p.equals("") || u.equals("")) return true;
        return false;
    }


}
