package com.clothapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.resources.RegisterUtil.isValidEmailAddress;

/**
 * Created by giacomoceribelli on 09/01/16.
 */
public class ResetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inizializzo layout e tasto indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_reset_password);

        final EditText editEmail = (EditText) findViewById(R.id.mainEmail);

        Button reset = (Button) findViewById(R.id.mainReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                if (!isValidEmailAddress(editEmail.getText().toString())) {
                    Snackbar.make(v, "Email non valida", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                } else {
                    ParseUser.requestPasswordResetInBackground(editEmail.getText().toString(), new RequestPasswordResetCallback() {
                        public void done(ParseException e) {

                            if (e == null) {
                                // email inviata correttamente
                                Snackbar.make(v, "Email di reset inviata correttamente", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                            } else {
                                // Errore nel reperire l'email
                                check(e.getCode(), v, e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        // Reinderizzo l'utente alla main activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
