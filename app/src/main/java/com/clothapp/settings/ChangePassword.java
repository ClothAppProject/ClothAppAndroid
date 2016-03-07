package com.clothapp.settings;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clothapp.BaseActivity;
import com.clothapp.R;
import com.clothapp.resources.RegisterUtil;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;

import static com.clothapp.resources.RegisterUtil.checkPswdLength;
import static com.clothapp.resources.RegisterUtil.isValidBirthday;
import static com.clothapp.resources.RegisterUtil.passWordChecker;

/**
 * Created by jack1 on 07/03/2016.
 */
public class ChangePassword extends BaseActivity {
    View v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        v=findViewById(R.id.change);
        final EditText password=(EditText)findViewById(R.id.password);
        final EditText newpassword=(EditText)findViewById(R.id.newpassword);
        final EditText repeatpassword=(EditText)findViewById(R.id.repeatpassword);
        Button button= (Button) findViewById(R.id.change_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), password.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e==null) {
                            if (user != null) {
                                // Hooray! The password is correct
                                changePassord(newpassword.getText().toString(), repeatpassword.getText().toString());
                            } else {
                                // The password was incorrect
                                Snackbar.make(v, "wrong password", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }

                        }
                    }
                });

            }
        });


    }

    private void changePassord(String newPsw, String repeat) {
        if(!newPsw.equals(repeat)){
            Snackbar.make(v, "Le passord non corrispondono", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }else if (!checkPswdLength(newPsw.trim())) {
            Snackbar.make(v, "La password deve essere lunga almeno 6 caratteri e non più di 12", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            System.out.println("debug: lunghezza pswd sbagliata");
            // Checking if pswd is acceptable. See SignupActivity.Util.passWordChecker for the parameters accepted
        } else if (passWordChecker(newPsw.trim()) != 0) {
            String pswd = newPsw.trim();
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
        } else{
            ParseUser.getCurrentUser().setPassword(newPsw);
        }

    }
}
