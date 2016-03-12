package com.clothapp.login_signup;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.SplashScreenActivity;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.clothapp.resources.ExceptionCheck.*;
import static com.clothapp.resources.RegisterUtil.*;


public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Date date;

    private TextView txt_birthday;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        try {
            getSupportActionBar().setTitle(R.string.signup);
            //inizializzo layout e tasto indietro
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("SignupActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        //  getting the context
        mContext = this;

        //  Punto interrogativo
        TextView txtSuggestions = (TextView) findViewById(R.id.suggestions);

        // Nascondo la tastiera all'avvio di quest'activity
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Prendo tutti valori
        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
        final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        final RadioGroup edit_sex = (RadioGroup) findViewById(R.id.radioSex);
        final Button btn_birthday = (Button) findViewById(R.id.button_set_birthday);
//                final EditText edit_day = (EditText) findViewById(R.id.edit_day);
//                final EditText edit_month = (EditText) findViewById(R.id.edit_month);
//                final EditText edit_year = (EditText) findViewById(R.id.edit_year);


        txt_birthday = (TextView) findViewById(R.id.text_birthday);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, 1994, 0, 1);

        btn_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        txt_birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // Signup button initialization
        Button btnSignup = (Button) findViewById(R.id.form_register_button);

        // Add OnClick listener to Sign Up button.
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                            //  checking if the birthday is empty
                        } else if (txt_birthday.getText().toString().equals("")) {
                            // Nel caso in cui nome e cognome siano vuoti
                            Snackbar.make(v, "Inserisci la tua data di nascita", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            Log.d("SignupActivity", "Inserisci la tua data di nascita");
                            //  checking if the birthday is empty
//                        } else if (edit_day.getText().toString().equalsIgnoreCase("") || edit_month.getText().toString().equalsIgnoreCase("") || edit_year.getText().toString().equalsIgnoreCase("")) {
//                            // Nel caso in cui la data di nasciata sia vuota
//                            Snackbar.make(v, "La data di nascita non può essere vuota", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//
//                            Log.d("SignupActivity", "La data di nascita non può esser vuota");
//                            //  checking if the birthday is acceptable
//                        } else if (!isValidBirthday(Integer.parseInt(edit_day.getText().toString()), Integer.parseInt(edit_month.getText().toString()),
//                                Integer.parseInt(edit_year.getText().toString()))) {
//                            Snackbar.make(v, "Inserire una data valida", Snackbar.LENGTH_LONG)
//                                    .setAction("Action", null).show();
//
//                            Log.d("SignupActivity", "La data inserito non è valida");
//                            // Checking if pswd length is right
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
                                    Snackbar.make(v, "La password deve contenere almeno un numero", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();

                                    Log.d("SignupActivity", "La password non contiene nessun numero");
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
                                    "Loading. Please wait...", true);

                            // Formatto data
                            // final String edit_date = edit_year.getText().toString() + "-" + edit_month.getText().toString() + "-" + edit_day.getText().toString();
                            String stringDate = txt_birthday.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("dd - MM - yyyy", Locale.US);

                            try {
                                date = sdf.parse(stringDate);
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                            // Create a new thread to handle signup in background
                            Thread signup = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    final ParseUser user = new ParseUser();
                                    user.setUsername(edit_username.getText().toString().trim());
                                    user.setPassword(edit_password.getText().toString().trim());
                                    user.setEmail(edit_email.getText().toString());
                                    user.put("name", edit_name.getText().toString().trim());
                                    user.put("flagISA","Persona");
                                    user.put("lowercase", user.getUsername().toLowerCase());
                                    user.signUpInBackground(new SignUpCallback() {
                                        public void done(ParseException e) {
                                            if (e == null) {

                                                // Caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                                Log.d("SignupActivity", "Registrazione utente eseguita correttamente");


                                                ParseObject persona = new ParseObject("Persona");
                                                persona.put("username",user.getUsername());
                                                persona.put("lastname", edit_lastname.getText().toString().trim());

                                                RadioButton sex = (RadioButton) findViewById(edit_sex.getCheckedRadioButtonId());
                                                String maleString = getResources().getString(R.string.man);
                                                if (sex.getText().equals(maleString)) {
                                                    persona.put("sex", "m");
                                                }else{
                                                    persona.put("sex", "f");
                                                }
                                                persona.put("date", date);
                                                //persona.put("city",edit_citta.getText().toString.trim());
                                                persona.saveInBackground(new SaveCallback() {
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

                        break;
                }
            }
        });

        // Add an OnClick listener to the question mark button
        txtSuggestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(R.string.passSignup)
                .setMessage(SignupActivity.this.getString((R.string.pswdStruct)));
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + " - " + (monthOfYear+1) + " - " + year;
        txt_birthday.setText(date);
    }
}