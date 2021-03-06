package com.clothapp.login_signup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.SplashScreenActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.resources.RegisterUtil.checkPassWordAndConfirmPassword;
import static com.clothapp.resources.RegisterUtil.checkPswdLength;
import static com.clothapp.resources.RegisterUtil.isValidEmailAddress;
import static com.clothapp.resources.RegisterUtil.passWordChecker;

public class ShopSignupActivity extends AppCompatActivity {

    //static CheckBox checkOnline;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup);
        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //REGISTRATION FIELDS
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);

        final EditText edit_address = (EditText) findViewById(R.id.edit_address);
        final EditText edit_webSite = (EditText) findViewById(R.id.edit_website);

        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);

        //  getting the context
        mContext = this;

        //  Punto interrogativo
        TextView txtSuggestions = (TextView) findViewById(R.id.suggestions);

        //checkOnline = (CheckBox) findViewById(R.id.checkbox_shop);

        try {
            getSupportActionBar().setTitle(R.string.signup);
            //inizializzo layout e tasto indietro
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("SignupActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }


        // Signup button initialization
        Button btnSignup = (Button) findViewById(R.id.register_button_shop);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View vi = v;
            if (edit_address.getText().toString().equals("")&&edit_address.getText().toString().equals(""))   {
                //Nel caso in cui sia sito web che indirizzo fisico siano vuoti
                Snackbar.make(v, "L'ndirizzo fisico o l'indirizzo internet non può essere vuoto", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // Checking username contains spaces
            } else if (edit_username.getText().toString().contains(" "))    {
                // Nel caso in cui l'username contiene spazi
                Snackbar.make(v, "L'username non può contenere spazi", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d("SignupActivity", "Il campo username contiene spazi");
                // Checking if password and confirm password match
            }else if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
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
                } else if (!checkPswdLength(edit_password.getText().toString().trim())) {
                    Snackbar.make(v, "La password deve essere lunga almeno 6 caratteri e non più di 12", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    System.out.println("debug: lunghezza pswd sbagliata");
                    // Checking if pswd is acceptable. See SignupActivity.Util.passWordChecker for the parameters accepted
                } else if (passWordChecker(edit_password.getText().toString()) != 0) {
                    String pswd = edit_password.getText().toString();
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
                        case -6:
                            Snackbar.make(v, "La password non può contenere spazi", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "La password contiene spazi");
                            break;
                    }
                } else {
                    //signup can proceed:
                    ParseQuery<ParseUser> usr = ParseUser.getQuery();
                    usr.whereEqualTo("lowercase",edit_username.getText().toString().toLowerCase());
                    try {
                        //check if lowercase of the username is already taken
                        usr.getFirst();
                        Snackbar.make(v, "L'username esiste già", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } catch (ParseException e) {
                        if (e.getCode() == 101) {
                            //no user found with same username
                            // Inizializzo la barra di caricamento
                            final ProgressDialog dialog = ProgressDialog.show(ShopSignupActivity.this, "",
                                    "Loading. Please wait...", true);

                            // Create a new thread to handle signup in background
                            Thread signup = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final ParseUser user = new ParseUser();
                                    user.setUsername(edit_username.getText().toString().trim());
                                    user.setPassword(edit_password.getText().toString().trim());
                                    user.setEmail(edit_email.getText().toString());
                                    user.put("name", edit_name.getText().toString().trim());
                                    user.put("lowercase", user.getUsername().toLowerCase());
                                    user.put("flagISA", "Negozio");
                                    user.put("Settings", getString(R.string.default_settings));

                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                // Caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                                Log.d("SignupActivity", "Registrazione utente eseguita correttamente");

                                                ParseObject negozio = new ParseObject("LocalShop");

                                                negozio.put("username", user.getUsername());
                                                negozio.put("name", edit_name.getText().toString().trim());
                                                negozio.put("lowercase", user.getUsername().toLowerCase());
                                                //  checking if the shop has a physical address
                                                negozio.put("address", edit_address.getText().toString().trim());

                                                //  checking if the shop has a webisite
                                                negozio.put("webSite", edit_webSite.getText().toString().trim());

                                                negozio.saveInBackground(new SaveCallback() {
                                                    @Override
                                                    public void done(ParseException e) {
                                                        if (e == null) {
                                                            // Redirect user to Splash Screen Activity.
                                                            Intent form_intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
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

                            // Start the signup thread
                            signup.start();
                        } else {
                            check(e.getCode(), vi, e.getMessage());
                        }
                    }
                }
            }
        });

        // Add an OnClick listener to the question mark button
        txtSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.passSignup)
                        .setMessage(ShopSignupActivity.this.getString((R.string.pswdStruct)));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }


    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

