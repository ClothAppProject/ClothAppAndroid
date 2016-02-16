package com.clothapp.settings;

import android.content.Intent;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.clothapp.R;
import com.clothapp.home_gallery.HomeActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setto pulsante indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }


    @Override
    public void onBackPressed() {
        // cesso questa activity e resto a quella precedente
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante indietro termino semplicemente l'activity
            case android.R.id.home:
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }



    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }

    }



    //ti lascio qua sotto il codice per connettere l'account a facebook, considera mi dava troppo fastidio sul profilo quindi l'ho spostato qua
    /*
    // Create connect to Facebook button
    final Button connect = (Button) findViewById(R.id.facebook_connect_button);
    // Create disconnect from Facebook button
    final Button disconnect = (Button) findViewById(R.id.facebook_disconnect_button);
    // Controlliamo se è connesso
    if (ParseFacebookUtils.isLinked(user)) {
        // L'utente è già connesso: gli do solo l'opzione per disconnettersi da facebook
        connect.setVisibility(View.INVISIBLE);

        // Add an OnClick listener to the disconnect button
        disconnect.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                final View vi = v;
                ParseFacebookUtils.unlinkInBackground(user, new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ex == null) {
                            Log.d("ProfileActivity", "Disconesso da Facebook");

                            // Redirect the user to the ProfileActivity Activity
                            Intent form_intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(form_intent);

                            finish();
                        } else {
                            // Controllo che non ci siano eccezioni Parse
                            check(ex.getCode(), vi, ex.getMessage());
                        }
                    }
                });
            }
        });
    } else {
        // L'utente non è connesso a facebook: gli do solo l'opzione per connettersi
        disconnect.setVisibility(View.INVISIBLE);

        // Add an OnClick listener to the connect button
        connect.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
            @Override
            public void onClick(View v) {
                final View vi = v;
                // Specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                ParseFacebookUtils.linkWithReadPermissionsInBackground(user, ProfileActivity.this, permissions, new SaveCallback() {
                    @Override
                    public void done(ParseException ex) {
                        if (ex != null) {
                            // Controllo che non ci siano eccezioni parse
                            check(ex.getCode(), vi, ex.getMessage());
                        }
                        if (ParseFacebookUtils.isLinked(user)) {
                            Log.d("ProfileActivity", "Connesso a Facebook");

                            // Redirect the user to the ProfileActivity Activity
                            Intent form_intent = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(form_intent);

                            finish();
                        }
                    }
                });
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }
    */

}
