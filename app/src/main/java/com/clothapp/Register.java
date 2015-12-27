package com.clothapp;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class Register extends AppCompatActivity {
    final String info= "Log-Info"; //name of the sharedPreference file. It shows whether the user is logged or not

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        //prendo tutti valori
        final EditText edit_password_confirm = (EditText) findViewById(R.id.edit_password_confirm);
        final EditText edit_password = (EditText) findViewById(R.id.edit_password);
        final EditText edit_username = (EditText) findViewById(R.id.edit_username);
        final EditText edit_email = (EditText) findViewById(R.id.edit_email);
        final EditText edit_name = (EditText) findViewById(R.id.edit_name);
        final EditText edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        final EditText edit_date = (EditText) findViewById(R.id.edit_date);

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                switch (v.getId()) {
                    case R.id.form_register_button:
                        if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString())) {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null).show();
                            System.out.println("debug: le password non coincidono");
                        }
                        //vanno inserite altre verifiche: su mail, data, username già esistente ecc..
                        else {
                            ParseUser.logOut();
                            ParseUser user = new ParseUser();
                            user.setUsername(edit_username.getText().toString());
                            user.setPassword(edit_password.getText().toString());
                            user.setEmail(edit_email.getText().toString());

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e == null) {
                                        System.out.println("debug: chiamata eseguita correttamente");
                                    } else {
                                        System.out.println("debug: errore= "+e.getMessage());
                                    }
                                }
                            });

                            /*
                            //prova di una post
                            int dim_param = 6;
                            //String indirizzo = "http://ceribbo.com/server.php";
                            String indirizzo = "http://www.clothapp.it/user/signup";
                            String[] data = new String[2*dim_param+1];
                            data[0] = indirizzo;
                            data[1] = "username";
                            data[2] = edit_username.getText().toString();
                            data[3] = "password";
                            data[4] = edit_password.getText().toString();
                            data[5] = "email";
                            data[6] = edit_email.getText().toString();
                            data[7] = "name";
                            data[8] = edit_name.getText().toString();
                            data[9] = "lastname";
                            data[10] = edit_lastname.getText().toString();
                            data[11] = "date";
                            data[12] = edit_date.getText().toString();
                            AsyncTask result = new Post().execute(data);
                            if (result.toString()=="")   {
                                System.out.println("nessuna risposta dal server");
                            }else{
                                //inseriti nel db, inserisco dati nello sharedPref e metto variabile logged a true
                                SharedPreferences userInformation = getSharedPreferences(info, MODE_PRIVATE);
                                userInformation.edit().putString("username",data[2]).commit();
                                userInformation.edit().putString("name",data[4]).commit();
                                userInformation.edit().putString("lastname",data[6]).commit();
                                userInformation.edit().putString("password",data[8]).commit();
                                userInformation.edit().putString("email",data[10]).commit();
                                userInformation.edit().putString("date",data[12]).commit();
                                //userInformation.edit().putBoolean("isLogged",true).commit();
                            }*/


                            //prova di una get
                            //System.out.println(new Get().execute("http://www.clothapp.it/users"));
                        }
                        break;
                }
            }
        });
    }

    //funzione per controllare le 2 password siano uguali e non nulle
    private boolean checkPassWordAndConfirmPassword(String password, String confirmPassword) {
        boolean pstatus = true;
        if (confirmPassword != null && password != null) {
            if (password.equals(confirmPassword)) {
                pstatus = false;
            }
        }
        return pstatus;
    }


}
