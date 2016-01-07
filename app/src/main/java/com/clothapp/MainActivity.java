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
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import java.util.Arrays;
import java.util.List;

import static com.clothapp.resources.FacebookUtil.*;
import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.resources.RegisterUtil.cryptoPswd;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        final SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);

        //listener sul pulsante login with facebook
        Button facebook_login = (Button) findViewById(R.id.login_button_facebook);
        facebook_login.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                final View vi = v;
                final SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                //specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                //eseguo la chiamata per il login via facebook con parse
                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            //controllo che non ci siano eccezioni parse
                            check(err.getCode(), vi, err.getMessage());
                        } else if (user == null) {
                            //login via facebook cancellato dall'utente
                            System.out.println("debug: Login attraverso facebook cancellato");
                        } else if (user.isNew()) {
                            //L'utente non è registrato con facebook, eseguo registrazione con facebook
                            System.out.println("debug: Registrazione e Login eseguiti attraverso facebook");
                            try {
                                //chiamo per inserire le informazioni di facebook nel database parse (l'utente è già stato creato)
                                getUserDetailsRegisterFB(user, vi,userInformation);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                            startActivity(form_intent);
                            finish();
                        } else {
                            //login eseguito correttamente attraverso facebook
                            System.out.println("debug: Login eseguito attraverso Facebook");
                            getUserDetailLoginFB(user,vi,userInformation);
                            Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                            startActivity(form_intent);
                            finish();
                        }
                    }
                });
            }
        });

        //listener sul bottone login via username e password
        Button login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                switch (v.getId()) {
                    case R.id.login_button:
                        final View vi = v;
                        //prendo tutti valori, li metto nel bundle e li attacco al form intent per mandarla alla prossima activity
                        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                        if (checknull(edit_password.getText().toString().trim(), edit_username.getText().toString().trim())) {
                            Snackbar.make(v, "I campi non devono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            try {
                                ParseUser.logIn(edit_username.getText().toString().trim(), edit_password.getText().toString().trim());
                                System.out.println("debug: Login eseguito correttamente");

                                //inserisco i valori nelle sharedPref
                                ParseUser uth = ParseUser.getCurrentUser();
                                userInformation.edit().putBoolean("isLogged", true).commit();
                                userInformation.edit().putString("username", uth.get("username").toString().trim()).commit();
                                //userInformation.edit().putString("password", cryptoPswd(uth.get("password").toString())).commit();
                                userInformation.edit().putString("name", uth.get("name").toString().trim()).commit();
                                userInformation.edit().putString("lastname", uth.get("lastname").toString().trim()).commit();
                                userInformation.edit().putString("date", uth.get("date").toString().trim()).commit();
                                userInformation.edit().putString("email", uth.get("email").toString()).commit();

                                Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                                startActivity(form_intent);
                                finish();
                            } catch (ParseException e) {
                                if (e.getCode() == 101) {
                                    //siccome il codice 101 è per 2 tipi di errori faccio prima il controllo qua e in caso chiamo gli altri
                                    System.out.println("debug: errore = " + e.getMessage());
                                    Snackbar.make(v, "Username o Password errati", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                } else {
                                    check(e.getCode(), v, e.getMessage());
                                }
                            }
                        }
                        break;
                }
            }
        });

        //listener sul pulsante register
        Button register = (Button) findViewById(R.id.register_button);
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

    //dopo login su facebook ritorna qui
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    //funzione per controllare che ne username ne password siano vuote
    public boolean checknull(String p, String u) {
        if (p.equals("") || u.equals("")) return true;
        return false;
    }

}
