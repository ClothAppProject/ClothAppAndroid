package com.clothapp.login_signup;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
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
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 19/01/16.
 */
public class FacebookUsernameActivity extends AppCompatActivity {
    private static View vi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //inizializzo layout e tasto indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_facebookusername);

        try {
            getSupportActionBar().setTitle(R.string.signup);
        } catch (NullPointerException e) {
            Log.d("FacebookUsernameActivi", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Signup button initialization
        Button btnSignup = (Button) findViewById(R.id.form_register_button);

        // Add OnClick listener to Sign Up button.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Prendo tutti valori
                final EditText edit_username = (EditText) findViewById(R.id.edit_username);
                vi = v;

                // Checking if username is nulll
                if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
                    // Nel caso in cui l'username è lasciato in bianco
                    Snackbar.make(v, "L'username non può essere vuoto", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    Log.d("SignupActivity", "Il campo username è vuoto");
                    // Checking if password and confirm password match
                }else{
                    // Inizializzo la barra di caricamento
                    final ProgressDialog dialog = ProgressDialog.show(FacebookUsernameActivity.this, "",
                            "Loading. Please wait...", true);

                    // Create a new thread to handle signup in background
                    Thread signup = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final ParseUser user = ParseUser.getCurrentUser();
                            final String nomevecchio = ParseUser.getCurrentUser().getUsername();
                            user.setUsername(edit_username.getText().toString().trim());
                            user.put("lowercase",edit_username.getText().toString().trim().toLowerCase());

                            user.saveInBackground(new SaveCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        //prendo l'oggetto Persona riferito a
                                        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Persona");
                                        query.whereEqualTo("username", nomevecchio);
                                        try {
                                            List<ParseObject> utente = query.find();
                                            ParseObject persona = utente.get(0);
                                            persona.put("username",user.getUsername());
                                            persona.saveInBackground(new SaveCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if (e==null) {
                                                        ParseQuery<ParseObject> userPhoto = new ParseQuery<ParseObject>("UserPhoto");
                                                        userPhoto.whereEqualTo("username", nomevecchio);
                                                        try {
                                                            List<ParseObject> pp = userPhoto.find();
                                                            ParseObject picture = pp.get(0);
                                                            picture.put("username",user.getUsername());
                                                            picture.saveInBackground();
                                                        } catch (ParseException e1) {
                                                            check(e1.getCode(), vi, e1.getMessage());
                                                        }
                                                        // Redirect user to Splash Screen Activity.
                                                        Intent form_intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                                                        startActivity(form_intent);

                                                        // Chiudo la dialogBar
                                                        dialog.dismiss();

                                                        finish();
                                                    }
                                                }
                                            });
                                        } catch (ParseException e1) {
                                            check(e1.getCode(), vi, e1.getMessage());
                                        }
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

    //funzione per eliminare utente appena creato con facebook
    private void deleteUser(ParseUser user)   {
        try {
            //elimino prima la persona
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Persona");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            ParseObject persona = query.getFirst();
            persona.delete();
            //e poi l'user
            user.delete();
        } catch (ParseException e) {
            check(e.getCode(), vi, e.getMessage());
        }
    }
    @Override
    public void onBackPressed() {
        //elimino utente appena creato con facebook
        deleteUser(ParseUser.getCurrentUser());
        // Reinderizzo l'utente alla main activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                //eliminio utente appena creato con facebook
                deleteUser(ParseUser.getCurrentUser());
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

