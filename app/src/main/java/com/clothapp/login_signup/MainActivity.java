package com.clothapp.login_signup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.SplashScreenActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;
import static com.clothapp.resources.RegisterUtil.setButtonTint;
import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.login_signup.FacebookUtil.getUserDetailsRegisterFB;


public class MainActivity extends AppCompatActivity {
    private ParseException ret = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context mContext = this;
        final EditText editUsername = (EditText) findViewById(R.id.mainUsername);
        final EditText editPassword = (EditText) findViewById(R.id.mainPassword);

        Button btnLogin = (Button) findViewById(R.id.mainLogin);
        Button btnFacebookLogin = (Button) findViewById(R.id.mainLoginFacebook);
        Button btnTwitterLogin = (Button) findViewById(R.id.mainLoginTwitter);
        //coloro pulsanti twitter e facebook su API 21
        setButtonTint(btnFacebookLogin,getResources().getColorStateList(R.color.facebook));
        setButtonTint(btnTwitterLogin,getResources().getColorStateList(R.color.twitter));

        TextView txtForgotPassword = (TextView) findViewById(R.id.mainForgotPassword);
        TextView txtSignup = (TextView) findViewById(R.id.mainSignup);

        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Add a listener to the normal login button
        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get trimmed strings from the username and password fields
                final String username = editUsername.getText().toString().trim();
                final String password = editPassword.getText().toString().trim();
                final View vi = v;

                // Check if either the username or the password are not blank
                if (username.equalsIgnoreCase("") || password.equalsIgnoreCase("")) {
                    Snackbar.make(v, R.string.empty_field, Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    return;
                }

                // Show a loading dialog. Needed to show something to the user if the Internet connection is slow.
                dialog = ProgressDialog.show(MainActivity.this, "", "Logging... Please wait...", true);

                // Create a thread to handle login in the background.
                Thread login = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ParseUser.logIn(username, password);

                            Log.d("MainActivity", "Login eseguito correttamente");

                            // Redirect user to Splash Screen Activity
                            Intent form_intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                            startActivity(form_intent);

                            // Close the loading dialog.
                            dialog.dismiss();

                            finish();

                        } catch (ParseException e) {

                            // Close the loading dialog.
                            dialog.dismiss();

                            if (e.getCode() == 101) {
                                // Siccome il codice 101 è per 2 tipi di errori faccio prima il controllo qua e in caso chiamo gli altri
                                Log.d("MainActivity", "Errore: " + e.getMessage());

                                Snackbar.make(vi, "Username o Password errati...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {
                                check(e.getCode(), vi, e.getMessage());
                            }
                        }
                    }
                });

                // Start the login thread.
                login.start();
            }
        });

        // Add a listener to the Facebook button
        btnFacebookLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {

                final View vi = v;
                // Inizializzo barra di caricamento
                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Logging with Facebook. Please wait...", true);

                // Create a thread to manage Facebook Login in background.
                Thread facebook = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        // Specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                        List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");

                        // Eseguo la chiamata per il login via facebook con parse
                        ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, permissions, new LogInCallback() {

                            @Override
                            public void done(ParseUser user, ParseException err) {

                                if (err != null) {
                                    //errore nel login via facebook
                                    // Chiudo barra di caricamento
                                    dialog.dismiss();

                                    // Controllo che non ci siano eccezioni parse
                                    check(err.getCode(), vi, err.getMessage());

                                } else if (user == null) {
                                    // Login via facebook cancellato dall'utente
                                    Log.d("MainActivity", "Login attraverso Facebook cancellato dall'utente.");

                                    // Chiudo barra di caricamento
                                    dialog.dismiss();

                                } else if (user.isNew()) {
                                    // L'utente non è registrato con facebook, eseguo registrazione con facebook
                                    Log.d("MainActivity", "L'utente non è registrato con Facebook, eseguo registrazione e login con Facebook");

                                    //inizializzo una parse exception che ritorna l'errore se mail è già stata usata
                                    Thread saveDataFB = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            //inserisco dati facebook nel nuovo utente
                                            try {
                                                //assegno alla variabile ParseException ret il ritorno della funzione
                                                ret=getUserDetailsRegisterFB(ParseUser.getCurrentUser(),v, getApplicationContext());
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    });
                                    saveDataFB.start();
                                    try {
                                        //aspetto che il thread del salvataggio dei dati termini
                                        saveDataFB.join();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    //controllo sui vari casi del ritorno del salvataggio di dati di facebook
                                    if (ret == null) {
                                        // Redirect user to Splash Screen Activity
                                        Intent form_intent = new Intent(MainActivity.this, FacebookUsernameActivity.class);
                                        startActivity(form_intent);
                                        dialog.dismiss();
                                        finish();
                                    }else if (ret.getCode()==203){
                                        //caso in cui l'utente che si registra con facebook ha email già associata ad altro account
                                        try {
                                            ParseUser.getCurrentUser().delete();
                                        } catch (ParseException e) {
                                            //in caso l'eliminazione dell'utente dia problemi
                                            check(e.getCode(), vi, e.getMessage());
                                        }
                                        Snackbar.make(vi, "Errore: Email associata all'account facebook già utilizzata", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();
                                    }else{
                                        //altro caso di errore di salvataggio dei dati di facebook
                                        check(ret.getCode(), vi, ret.getMessage());
                                    }
                                    // Chiudo barra di caricamento
                                    dialog.dismiss();
                                } else {
                                    // Login eseguito correttamente attraverso facebook
                                    Log.d("MainActivity", "Login eseguito correttamente attraverso Facebook");

                                    //minuscolo
                                    ParseUser persona = new ParseUser();
                                    persona.put("lowercase", user.getUsername().toLowerCase());
                                    persona.saveInBackground();

                                    // Redirect user to Splash Screen Activity
                                    Intent form_intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                                    startActivity(form_intent);

                                    // Chiudo barra di caricamento
                                    dialog.dismiss();

                                    finish();
                                }
                            }
                        });
                    }
                });

                // Start Facebook Login thread
                facebook.start();
            }
        });

        // Add an OnClick listener to the signup button
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.choose_user)
                        .setItems(R.array.signup, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Redirect the user to the ProfilePictureCameraActivity Activity
                                        Intent profilo = new Intent(getApplicationContext(), SignupActivity.class);
                                        startActivity(profilo);
                                        break;
                                    case 1:
                                        // Redirect the user to the ProfilePictureGalleryActivity Activity
                                        Intent negozio = new Intent(getApplicationContext(), ShopSignupActivity.class);
                                        startActivity(negozio);
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // Add an OnClick listener to the forgot password button
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Redirect user to forgot password Activity.
                Intent signupIntent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                startActivity(signupIntent);
            }
        });
    }

    // Get the result of the Facebook login activity when coming back to this activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


}
