package com.clothapp.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
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
import android.widget.Toast;

import com.clothapp.R;
import com.clothapp.login_signup.ResetPasswordActivity;
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

            final Preference edit_profile = (Preference) findPreference("edit_profile");
            edit_profile.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i;
                    if (ParseUser.getCurrentUser().get("flagISA").equals("Persona"))    {
                        i = new Intent(getActivity().getApplicationContext(), EditProfileActivity.class);
                    }else{
                        i = new Intent(getActivity().getApplicationContext(), EditShopProfileActivity.class);
                    }
                    startActivity(i);
                    return false;
                }
            });

            final Preference change = (Preference) findPreference("change_password");
            change.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ResetPasswordActivity.class);
                    startActivity(i);
                    return false;
                }
            });

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
                    title.setText(R.string.report);
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
                                    }else{
                                        Toast.makeText(getActivity().getApplicationContext(),R.string.report_sent,Toast.LENGTH_SHORT).show();
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


            final SwitchPreference facebookPref = (SwitchPreference) findPreference("facebook");
            if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                //utente è già connesso
                facebookPref.setChecked(true);
            }else{
                // L'utente non è connesso a facebook
                facebookPref.setChecked(false);
            }
            facebookPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                        //utente è già connesso: gli do solo l'opzione per disconnettersi da facebook
                        AlertDialog.Builder disconnect_facebook = new AlertDialog.Builder(getActivity());
                        disconnect_facebook.setTitle(R.string.ask_disconnect_facebook);
                        // Add action buttons
                        disconnect_facebook.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                                    @Override
                                    public void done(ParseException ex) {
                                        if (ex == null) {
                                            Log.d("SettingsActivity", "Disconesso da Facebook");
                                            facebookPref.setChecked(false);
                                            Toast.makeText(getActivity().getApplicationContext(),R.string.facebook_disconnected,Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Controllo che non ci siano eccezioni Parse
                                            ExceptionCheck.check(ex.getCode(), getView(), ex.getMessage());
                                            Toast.makeText(getActivity().getApplicationContext(), ex.getMessage(),Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                        disconnect_facebook.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //annullata
                            }
                        });
                        AlertDialog disconnectReport = disconnect_facebook.create();
                        // display dialog
                        disconnectReport.show();
                    }else{
                        // Specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                        List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                        ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), getActivity(), permissions, new SaveCallback() {
                            @Override
                            public void done(ParseException ex) {
                                if (ex == null) {
                                    Log.d("SettingsActivity", "Connesso a Facebook");
                                    facebookPref.setChecked(true);
                                    Toast.makeText(getActivity().getApplicationContext(),R.string.facebook_connected,Toast.LENGTH_SHORT).show();
                                }else if (ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                                    // Controllo che non ci siano eccezioni parse
                                    ExceptionCheck.check(ex.getCode(), getView(), ex.getMessage());
                                    Toast.makeText(getActivity().getApplicationContext(), ex.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    return false;
                }
            });

            final String username = ParseUser.getCurrentUser().getUsername();

            final CheckBoxPreference savePhotos = (CheckBoxPreference) findPreference("savePhotos");
            savePhotos.setChecked(UserSettingsUtil.checkIfSavePhotos());
            savePhotos.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean nuovoValore = !UserSettingsUtil.checkIfSavePhotos();
                    savePhotos.setChecked(nuovoValore);
                    UserSettingsUtil.setSavePhotos(nuovoValore);
                    return false;
                }
            });

            final SwitchPreference like = (SwitchPreference) findPreference("like");
            like.setChecked(UserSettingsUtil.checkNotificationLike(username));
            like.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean nuovoValore = !UserSettingsUtil.checkNotificationLike(username);
                    like.setChecked(nuovoValore);
                    UserSettingsUtil.setLikeNotifications(nuovoValore);
                    return false;
                }
            });

            final SwitchPreference follower = (SwitchPreference) findPreference("follower");
            follower.setChecked(UserSettingsUtil.checkNotificationFollower(username));
            follower.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean nuovoValore = !UserSettingsUtil.checkNotificationFollower(username);
                    follower.setChecked(nuovoValore);
                    UserSettingsUtil.setFollowerNotifications(nuovoValore);
                    return false;
                }
            });

            final SwitchPreference new_photo = (SwitchPreference) findPreference("newPhoto");
            new_photo.setChecked(UserSettingsUtil.checkNotificationNewPhoto(username));
            new_photo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean nuovoValore = !UserSettingsUtil.checkNotificationNewPhoto(username);
                    new_photo.setChecked(nuovoValore);
                    UserSettingsUtil.setNetPhotoNotifications(nuovoValore);
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


}
