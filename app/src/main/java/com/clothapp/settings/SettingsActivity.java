package com.clothapp.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.clothapp.R;
import com.clothapp.resources.ExceptionCheck;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

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



            final Preference signal = (Preference) findPreference("signal");
            signal.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //cliccato su segnala foto, creo dialog per segnalare foto
                    AlertDialog.Builder report = new AlertDialog.Builder(getActivity());
                    // Get the layout inflater
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    // Inflate and set the layout for the dialog
                    View dialogView = inflater.inflate(R.layout.dialog_report, null);
                    report.setView(dialogView);

                    final TextView title = (TextView) dialogView.findViewById(R.id.report_photo);
                    title.setText(R.string.segnala);
                    final EditText comment = (EditText) dialogView.findViewById(R.id.comment);
                    final Spinner spinner = (Spinner) dialogView.findViewById(R.id.select_reason);
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                            R.array.select_suggestion, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spinner.setAdapter(adapter);
                    // Add action buttons
                    report.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            ParseObject segnalazione = new ParseObject("Report");
                            segnalazione.put("comment", comment.getText().toString());
                            segnalazione.put("from_username", ParseUser.getCurrentUser().getUsername());
                            segnalazione.put("reason", spinner.getSelectedItem());
                            segnalazione.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e != null) {
                                        check(e.getCode(), getView(), e.getMessage());
                                    }
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    report.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //segnalazione annullata
                        }
                    });
                    AlertDialog dialogReport = report.create();
                    // display dialog
                    dialogReport.show();
                    return false;
                }
            });


            final Preference facebookPref = (Preference) findPreference("facebook");
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                //utente è già connesso
                facebookPref.setTitle(R.string.disconnect_facebook);
            }else{
                // L'utente non è connesso a facebook
                facebookPref.setTitle(R.string.connect_facebook);
            }
            facebookPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        //utente è già connesso: gli do solo l'opzione per disconnettersi da facebook
                        facebookPref.setTitle(R.string.disconnect_facebook);
                        ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex == null) {
                                    Log.d("SettingsActivity", "Disconesso da Facebook");
                                    facebookPref.setTitle(R.string.connect_facebook);
                                } else {
                                    // Controllo che non ci siano eccezioni Parse
                                    ExceptionCheck.check(ex.getCode(), getView(), ex.getMessage());
                                }
                            }
                        });
                    }else{
                        // L'utente non è connesso a facebook: gli do solo l'opzione per connettersi
                        facebookPref.setTitle(R.string.connect_facebook);

                        // Specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                        List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                        ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), getActivity(), permissions, new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex != null) {
                                    // Controllo che non ci siano eccezioni parse
                                    ExceptionCheck.check(ex.getCode(), getView(), ex.getMessage());
                                }else if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                                    Log.d("SettingsActivity", "Connesso a Facebook");
                                    facebookPref.setTitle(R.string.disconnect_facebook);
                                }
                            }
                        });
                    }
                    return false;
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    //ti lascio qua sotto il codice per connettere l'account a facebook, considera mi dava troppo fastidio sul profilo quindi l'ho spostato qua
    /*
    // Create connect to Facebook button
    final Button connect = (Button) findViewById(R.id.facebook_connect_button);
    // Create disconnect from Facebook button
    final Button disconnect = (Button) findViewById(R.id.facebook_disconnect_button);

    */

}
