package com.clothapp.login_signup;

import android.app.ProgressDialog;
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

import com.clothapp.R;
import com.clothapp.SplashScreenActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_signup);


        //REGISTRATION FIELDS
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);

        final EditText edit_address = (EditText) findViewById(R.id.edit_address);
        final EditText edit_webSite = (EditText) findViewById(R.id.edit_website);

        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);

        //checkOnline = (CheckBox) findViewById(R.id.checkbox_shop);

        try {
            getSupportActionBar().setTitle(R.string.signup);
            //inizializzo layout e tasto indietro
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("SignupActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        /*
        //CHECKBOX BUTTON
        checkOnline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    edit_webSite.setVisibility(View.VISIBLE);
                }else{
                    edit_webSite.setVisibility(View.GONE);
                }
            }
        });
        */

        // Signup button initialization
        Button btnSignup = (Button) findViewById(R.id.register_button_shop);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View vi = v;

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
                            //TODO check fisico o virtuale
                            user.put("flagISA", "Negozio");
                            user.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        // Caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                        Log.d("SignupActivity", "Registrazione utente eseguita correttamente");

                                        ParseObject negozio = new ParseObject("LocalShop");
                                        negozio.put("username", user.getUsername());
                                        negozio.put("sito", edit_address.getText().toString().trim());
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
                }
            }
        });
    }



    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

