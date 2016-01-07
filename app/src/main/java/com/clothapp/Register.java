package com.clothapp;

import android.app.ProgressDialog;
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
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.*;
import static com.clothapp.resources.FacebookUtil.getUserDetailLoginFB;
import static com.clothapp.resources.FacebookUtil.getUserDetailsRegisterFB;
import static com.clothapp.resources.RegisterUtil.*;



public class Register extends AppCompatActivity {
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(R.string.register_button);
        //nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                //prendo tutti valori
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
                        //  checking if username is nulll
                        if (edit_username.getText().toString().trim()=="") {
                            //nel caso in cui l'username è lasciato in bianco
                            Snackbar.make(v, "L'username non può essere vuoto", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: il campo username è vuoto");
                        //  checking if password and confirm password match
                        }else if (checkPassWordAndConfirmPassword(edit_password.getText().toString().trim(), edit_password_confirm.getText().toString())) {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: le password non coincidono");
                        //  checking if the email address is valid
                        }else if(!isValidEmailAddress(edit_email.getText().toString().trim())) {
                            //nel caso in cui la mail non sia valida
                            Snackbar.make(v, "La mail inserita non è valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: la mail inserita è sbagliata");
                        //  checking if first and last name are not null
                        }else if(edit_lastname.getText().toString().trim()==""||edit_name.getText().toString().trim()=="")  {
                            //nel caso in cui nome e cognome siano vuoti
                            Snackbar.make(v, "Nome e Cognome non possono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: nome o cognome non posssono essere vuoti");
                        //  checking if the birthday is acceptable
                        }else if(!isValidBirthday(Integer.parseInt(edit_day.getText().toString()) ,Integer.parseInt(edit_month.getText().toString()),
                                Integer.parseInt(edit_year.getText().toString()) )){
                            Snackbar.make(v, "Inserire una data valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                            System.out.println("debug: la data inserita non e' valida");
                        //  checking if pswd length is right
                        }else if(!checkPswdLength(edit_password.getText().toString().trim())) {
                            Snackbar.make(v, "La password deve essere lunga almeno 6 caratteri e non più di 12", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: lunghezza pswd sbagliata");
                        //  checking if pswd is acceptable. See Register.Util.passWordChecker for the parameters accepted
                        }else if(passWordChecker(edit_password.getText().toString().trim()) != 0){
                            String pswd = edit_password.getText().toString().trim();
                            int result = passWordChecker(pswd);
                            switch (result){
                                case -1:
                                    Snackbar.make(v, "La password deve contenere almeno 1 lettera maiuscola", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println("debug: password non contiene lettera maiuscola");
                                    break;
                                case -2:
                                    Snackbar.make(v, "La password deve contenere almeno 1 lettera minuscola", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println("debug: password non contiene lettera minuscola");
                                    break;
                                case -3:
                                    Snackbar.make(v, "La password deve contenere almeno un numeo", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println("debug: password non contiene nessun numero");
                                    break;
                                case -4:
                                    Snackbar.make(v, "La password non può contenere spazi o caratteri tab e new line", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println("debug: password contiene spazi o tab o new line");
                                    break;
                                case -5:
                                    Snackbar.make(v, "La password non può contenere caratteri speciali", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                    System.out.println("debug: password contiene caratteri speciali");
                                    break;
                            }
                        }else{
                            //inizializzo la barra di caricamento
                            final ProgressDialog dialog = ProgressDialog.show(Register.this, "",
                                    "Logging out. Please wait...", true);
                            //formatto data
                            final String edit_date = edit_year.getText().toString()+"-"+edit_month.getText().toString()+"-"+edit_day.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            try {
                                date = sdf.parse(edit_date);
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                            Thread register = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    ParseUser user = new ParseUser();
                                    user.setUsername(edit_username.getText().toString().trim());
                                    user.setPassword(edit_password.getText().toString().trim());
                                    user.setEmail(edit_email.getText().toString());
                                    user.put("name",edit_name.getText().toString().trim());
                                    user.put("lastname",edit_lastname.getText().toString().trim());
                                    user.put("date",date);

                                    System.out.println("debug: userID = "+user.getObjectId());

                                    System.out.println("debug: pswd is: "+edit_password.getText().toString());

                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e==null)    {

                                                //caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                                System.out.println("debug: registrazione eseguita corretttamente");
                                                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                                userInformation.edit().putString("username",edit_username.getText().toString().trim()).commit();
                                                userInformation.edit().putString("name",edit_name.getText().toString().trim()).commit();
                                                userInformation.edit().putString("lastname",edit_lastname.getText().toString().trim()).commit();
                                                userInformation.edit().putString("password",cryptoPswd(edit_password.getText().toString().trim())).commit();
                                                userInformation.edit().putString("email",edit_email.getText().toString()).commit();
                                                userInformation.edit().putString("date",edit_date.toString()).commit();
                                                userInformation.edit().putBoolean("isLogged",true).commit();
                                                Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                                                startActivity(form_intent);
                                                //chiudo la dialogBar
                                                dialog.dismiss();
                                                finish();
                                            }else {
                                                //chiudo la dialogBar
                                                dialog.dismiss();
                                                //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                                                check(e.getCode(),vi,e.getMessage());
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

        //listener sul pulsante login with facebook
        Button facebook_login = (Button) findViewById(R.id.register_button_facebook);
        facebook_login.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                final View vi = v;
                final SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                //specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                //eseguo la chiamata per il login via facebook con parse
                ParseFacebookUtils.logInWithReadPermissionsInBackground(Register.this, permissions, new LogInCallback() {
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
    }

    //dopo login su facebook ritorna qui
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
}