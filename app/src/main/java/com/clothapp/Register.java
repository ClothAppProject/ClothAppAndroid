package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        Button button = (Button) findViewById(R.id.form_register_button); //inizializzo bottone registrati
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                final View vi = v;
                switch (v.getId()) {
                    case R.id.form_register_button:
                        //TODO va inserito controllo su password con almeno 1 numero e una lettera e lunga almento tot
                        if (edit_username.getText().toString()=="") {
                            //nel caso in cui l'username è lasciato in bianco
                            Snackbar.make(v, "L'username non può essere vuoto", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: il campo username è vuoto");
                        }else if (checkPassWordAndConfirmPassword(edit_password.getText().toString(), edit_password_confirm.getText().toString())) {
                            //nel caso in cui le password non coincidano
                            Snackbar.make(v, "Le password devono coincidere", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: le password non coincidono");
                        }else if(!isValidEmailAddress(edit_email.getText().toString())) {
                            //nel caso in cui la mail non sia valida
                            Snackbar.make(v, "La mail inserita non è valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: la mail inserita è sbagliata");
                        }else if(edit_lastname.getText().toString()==""||edit_name.getText().toString()=="")  {
                            //nel caso in cui la mail non sia valida
                            Snackbar.make(v, "Nome e Cognome non possono essere vuoti", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: nome o cognome non posssono essere vuoti");
                        }else if(!isValidBirthday(Integer.parseInt(edit_day.getText().toString()) ,Integer.parseInt(edit_month.getText().toString()),
                                Integer.parseInt(edit_year.getText().toString()) )){
                            Snackbar.make(v, "Inserire una data valida", Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                            System.out.println("debug: la data inserita non e' valida");
                        }else if(!checkPswdLength(edit_password.getText().toString())) {
                            Snackbar.make(v, "La password deve essere lunga almeno 6 caratteri e non più di 12", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            System.out.println("debug: lunghezza pswd sbagliata");
                        }else if(passWordChecker(edit_password.getText().toString()) != 0){
                            String pswd = edit_password.getText().toString();
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
                            final String edit_date = edit_year.getText().toString()+"-"+edit_month.getText().toString()+"-"+edit_day.getText().toString();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            Date date = null;
                            try {
                                date = sdf.parse(edit_date);
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                            ParseUser user = new ParseUser();
                            user.setUsername(edit_username.getText().toString());
                            user.setPassword(edit_password.getText().toString());
                            user.setEmail(edit_email.getText().toString());
                            user.put("name",edit_name.getText().toString());
                            user.put("lastname",edit_lastname.getText().toString());
                            user.put("date",date);

                            System.out.println("debug: userID = "+user.getObjectId());

                            System.out.println("debug: pswd is: "+edit_password.getText().toString());

                            user.signUpInBackground(new SignUpCallback() {
                                public void done(ParseException e) {
                                    if (e==null)    {

                                        //caso in cui registrazione è andata a buon fine e non ci sono eccezioni
                                        System.out.println("debug: registrazione eseguita corretttamente");
                                        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                                        userInformation.edit().putString("username",edit_username.getText().toString()).commit();
                                        userInformation.edit().putString("name",edit_name.getText().toString()).commit();
                                        userInformation.edit().putString("lastname",edit_lastname.getText().toString()).commit();
                                        //userInformation.edit().putString("password",edit_password.getText().toString()).commit();
                                        userInformation.edit().putString("email",edit_email.getText().toString()).commit();
                                        //TODO x Giacomo ho commentato la linea subito dopo
                                        userInformation.edit().putString("date",edit_date.toString()).commit();
                                        userInformation.edit().putBoolean("isLogged",true).commit();
                                        Intent form_intent = new Intent(getApplicationContext(), SplashScreen.class);
                                        startActivity(form_intent);
                                        finish();
                                    }else {
                                        //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                                        new ExceptionCheck().check(e.getCode(),vi,e.getMessage());
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
                            }
                            //prova di una get
                            //System.out.println(new Get().execute("http://www.clothapp.it/users"));
                            */
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
    //funzione per controllare che sia indirizzo mail valido
    private boolean isValidEmailAddress(String email) {
        if (email == "") return false;
        String regex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //  it returns true if it is a valid birthday, else false
    private boolean isValidBirthday (int day, int month, int year) {

        boolean flag = false;
        if(year < 1900 || year > 2015) return flag;
        if (day <= 0 || month <= 0) return flag;

        switch (month) {

            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                if (day <= 31) flag = true;
                break;

            case 4: case 6: case 9: case 11: if(day <= 30)
                flag = true;
                break;

            case 2:
                if(day <=28) flag = true;
                else if(isBisestle(year) && day == 29) flag = true;
                break;
        }

        return flag;
    }

    //   checking if the n parameter representing the year is bisestle
    //   it returns true if it is
    private boolean isBisestle(int n) {
        if((n % 4 == 0 && n % 100 != 0) || n % 400 == 0) return true;
        return false;
    }

    //  checking pswd lenght is greater than 6
    private boolean checkPswdLength(String a){
        return a.length() >= 6 && a.length() <= 12;
    }

    /*
 *  PASSWORD MUST CONTAIN:
 *  At least one capital letter, one non capital letter and one digit character.
 *  Special characters except the dot are not allowed
 *
 *  check if the password is solid
 *  it returns:
 *   0 if everything is fine
 *  -1 if there are no capital letters
 *  -2 if there are no non capital letters
 *  -3 if there are no digits characters
 *  -4 if there space characters (tab new line ecc)
 *  -5 if there are other special characters (like comma, question mark ecc)
 */
    private int passWordChecker (String input) {
        Pattern[] passwordRegexes = new Pattern[3];
        passwordRegexes[0] = Pattern.compile(".*[A-Z].*"); //   capital letters
        passwordRegexes[1] = Pattern.compile(".*[a-z].*"); //   non capital letters
        passwordRegexes[2] = Pattern.compile(".*\\d.*");   //   numbers

        for (int i = 0; i < passwordRegexes.length; i++) {
            if (!passwordRegexes[i].matcher(input).matches()) return -(i+1);
        }

        Pattern spacePattern = Pattern.compile(".*\\s.*"); //   tab, space, new line ecc
        if(spacePattern.matcher(input).matches()) return -(passwordRegexes.length+1);
        Pattern specialChars = Pattern.compile(".*[^a-zA-Z0-9].*"); //  special characters
        input.replaceAll(".","");
        if(specialChars.matcher(input).matches()) {
            System.out.println("debug: funzione de merda");
            return -(passwordRegexes.length+2);
        }
        return 0;
    }
}