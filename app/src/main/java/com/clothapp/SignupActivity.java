package com.clothapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.*;
import static com.clothapp.resources.FacebookUtil.getUserDetailLoginFB;
import static com.clothapp.resources.FacebookUtil.getUserDetailsRegisterFB;
import static com.clothapp.resources.RegisterUtil.*;


public class SignupActivity extends AppCompatActivity {
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        getSupportActionBar().setTitle(R.string.register_button);

        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Signup button initialization
        Button btnSignup = (Button) findViewById(R.id.form_register_button);

        // Add OnClick listener to Sign Up button.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Prendo tutti valori
                final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
                final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                final EditText edit_email = (EditText) findViewById(R.id.edit_email);
                final EditText edit_name = (EditText) findViewById(R.id.edit_name);
                final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
                final EditText edit_day = (EditText) findViewById(R.id.edit_day);
                final EditText edit_month = (EditText) findViewById(R.id.edit_month);
                final EditText edit_year = (EditText) findViewById(R.id.edit_year);
                final View vi = v;

                switch (v.getId()) {
                    case R.id.form_register_button:
                        // Checking if username is nulll
                        if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
                            // Nel caso in cui l'username è lasciato in bianco
                            Snackbar.make(v, "L'username non può essere vuoto", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "Il campo username è vuoto");
                            // Checking if password and confirm password match
                        } else if (checkPassWordAndConfirmPassword(edit_password.getText().toString().trim(), edit_password_confirm.getText().toString())) {
                            // Nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "Le password non coincidono");
                            // Checking if the email address is valid
                        } else if (!isValidEmailAddress(edit_email.getText().toString().trim())) {
                            // Nel caso in cui la mail non sia valida
                            Snackbar.make(v, "La mail inserita non è valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "La mail inserito non è corretta");
                            // Checking if first and last name are not null
                        } else if (edit_lastname.getText().toString().trim().equalsIgnoreCase("") || edit_name.getText().toString().trim().equalsIgnoreCase("")) {
                            // Nel caso in cui nome e cognome siano vuoti
                            Snackbar.make(v, "Nome e Cognome non possono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "Nome o cognome non possono essere vuoti");
                            //  checking if the birthday is acceptable
                        } else if (!isValidBirthday(Integer.parseInt(edit_day.getText().toString()), Integer.parseInt(edit_month.getText().toString()),
                                Integer.parseInt(edit_year.getText().toString()))) {
                            Snackbar.make(v, "Inserire una data valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "La data inserito non è valida");
                            // Checking if pswd length is right
                        } else if (!checkPswdLength(edit_password.getText().toString().trim())) {
                            Snackbar.make(v, "La password deve essere lunga almeno 6 caratteri e non più di 12", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: lunghezza pswd sbagliata");
                            // Checking if pswd is acceptable. See SignupActivity.Util.passWordChecker for the parameters accepted
                        } else if (passWordChecker(edit_password.getText().toString().trim()) != 0) {
                            String pswd = edit_password.getText().toString().trim();
                            int result = passWordChecker(pswd);

                            switch (result) {
                                case -1:
                                    Snackbar.make(v, "La password deve contenere almeno 1 lettera maiuscola", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password non contiene una lettera maiuscola");
                                    break;
                                case -2:
                                    Snackbar.make(v, "La password deve contenere almeno 1 lettera minuscola", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password non contiene una lettera minuscola");
                                    break;
                                case -3:
                                    Snackbar.make(v, "La password deve contenere almeno un numeo", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password non contiente nessun numero");
                                    break;
                                case -4:
                                    Snackbar.make(v, "La password non può contenere spazi o caratteri tab e new line", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password contiene spazi o tab o new line");
                                    break;
                                case -5:
                                    Snackbar.make(v, "La password non può contenere caratteri speciali", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password contiene caratteri speciali");
                                    break;
                            }
                        } else {
                            // Inizializzo la barra di caricamento
                            final ProgressDialog dialog = ProgressDialog.show(SignupActivity.this, "",
                                    "Logging out. Please wait...", true);

                            // Formatto data
                            final String edit_date = edit_year.getText().toString() + "-" + edit_month.getText().toString() + "-" + edit_day.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                            try {
                                date = sdf.parse(edit_date);
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                            // Create a new thread to handle signup in background
                            Thread signup = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ParseUser user = new ParseUser();
                                    user.setUsername(edit_username.getText().toString().trim());
                                    user.setPassword(edit_password.getText().toString().trim());
                                    user.setEmail(edit_email.getText().toString());
                                    user.put("name", edit_name.getText().toString().trim());
                                    user.put("lastname", edit_lastname.getText().toString().trim());
                                    user.put("date", date);

                                    Log.d("SignupActivity", "Signup background thread: userID = " + user.getObjectId());
                                    Log.d("SignupActivity", "Signup background thread: password = " + edit_password.getText().toString());

                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                // Caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                                Log.d("SignupActivity", "Registrazione utente eseguita correttamente");

                                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                                userInformation.edit().putString("username", edit_username.getText().toString().trim()).commit();
                                                userInformation.edit().putString("name", edit_name.getText().toString().trim()).commit();
                                                userInformation.edit().putString("lastname", edit_lastname.getText().toString().trim()).commit();
                                                userInformation.edit().putString("password", cryptoPswd(edit_password.getText().toString().trim())).commit();
                                                userInformation.edit().putString("email", edit_email.getText().toString()).commit();
                                                userInformation.edit().putString("date", edit_date.toString()).commit();
                                                userInformation.edit().putBoolean("isLogged", true).commit();

                                                // Redirect user to Splash Screen Activity.
                                                Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                                                startActivity(form_intent);

                                                // Chiudo la dialogBar
                                                dialog.dismiss();

                                                finish();
                                            } else {
                                                // Chiudo la dialogBar
                                                dialog.dismiss();

                                                // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                                                check(e.getCode(), vi, e.getMessage());
                                            }
                                        }
                                    });
                                }
                            });
                        }
                        break;
                }
            }
        });

        // Facebook button initialization
        Button facebook_login = (Button) findViewById(R.id.register_button_facebook);

        // Add OnClick listener to the Facebook button
        facebook_login.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {

                final View vi = v;
                final SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);

                // Specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");

                // Eseguo la chiamata per il login via facebook con parse
                ParseFacebookUtils.logInWithReadPermissionsInBackground(SignupActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            // Controllo che non ci siano eccezioni parse
                            check(err.getCode(), vi, err.getMessage());
                        } else if (user == null) {
                            // Login via facebook cancellato dall'utente
                            Log.d("SignupActivity", "Login attraverso Facebook cancellato dall'utente");
                        } else if (user.isNew()) {
                            // L'utente non è registrato con facebook, eseguo registrazione con facebook
                            Log.d("SignupActivity", "L'utente non è registrato con Facebook, eseguo registrazione con Facebook");

                            try {
                                // Chiamo per inserire le informazioni di facebook nel database parse (l'utente è già stato creato)
                                getUserDetailsRegisterFB(user, vi, userInformation);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Redirect user to Splash Screen Activity.
                            Intent form_intent = new Intent(SignupActivity.this, SplashScreen.class);
                            startActivity(form_intent);

                            finish();
                        } else {
                            // Login eseguito correttamente attraverso facebook
                            Log.d("SignupActivity", "Login eseguito attraverso Facebook");

                            getUserDetailLoginFB(user, vi, userInformation);

                            // Redirect user to Splash Screen Activity
                            Intent form_intent = new Intent(SignupActivity.this, SplashScreen.class);
                            startActivity(form_intent);

                            finish();
                        }
                    }
                });
            }
        });
    }

    // Dopo login su facebook ritorna qui
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}