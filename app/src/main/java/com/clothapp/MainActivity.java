package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clothapp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import static com.clothapp.ExceptionCheck.check;


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
                        if (checknull(edit_password.getText().toString(),edit_username.getText().toString())) {
                            Snackbar.make(v, "I campi non devono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }else{
                            try {
                                ParseUser.logIn(edit_username.getText().toString(),edit_password.getText().toString());
                                //TODO inserire queste altre informazioni nelle sharedPref
                                /*
                                ParseQuery<ParseUser> query = ParseQuery.getQuery("User");
                                query.whereEqualTo("username", edit_username.getText().toString());
                                query.findInBackground(new FindCallback<ParseUser>() {
                                    public void done(List<ParseUser> uth, ParseException ex) {
                                        if (ex == null) {
                                            ParseUser utente = uth.get(0);
                                            userInformation.edit().putString("name",utente.get("name").toString()).commit();
                                            userInformation.edit().putString("lastname",utente.get("lastname").toString()).commit();
                                            userInformation.edit().putString("date",utente.getDate("date").toString()).commit();
                                            userInformation.edit().putString("email",utente.getEmail().toString()).commit();
                                        } else {
                                            check(ex.getCode(),vi,ex.getMessage());
                                        }
                                    }
                                });*/

                                System.out.println("debug: Login eseguito correttamente");
                                //  setting isLogged to true
                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                userInformation.edit().putBoolean("isLogged",true).commit();
                                userInformation.edit().putString("username",edit_username.getText().toString()).commit();

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
