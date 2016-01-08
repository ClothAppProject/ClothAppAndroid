package com.clothapp;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class Profile extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            getSupportActionBar().setTitle(R.string.profile_button);

        } catch (NullPointerException e) {
            Log.d("Profile", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Create side menu
        setUpMenu();

        TextView username = (TextView) findViewById(R.id.username_field);
        TextView name = (TextView) findViewById(R.id.name_field);
        TextView lastname = (TextView) findViewById(R.id.lastname_field);
        TextView email = (TextView) findViewById(R.id.email_field);
        TextView date = (TextView) findViewById(R.id.date_field);

        // Get current Parse user
        final ParseUser user = ParseUser.getCurrentUser();

        username.setText(user.getUsername());
        name.setText(capitalize(user.get("name").toString()));
        lastname.setText(capitalize(user.get("lastname").toString()));
        email.setText(user.getEmail());
        // Trimmed the data String in order to delete white spaces
        String timeStamp = formatDate(user.get("date").toString());
        date.setText(timeStamp);

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
                                Log.d("Profile", "Disconesso da Facebook");

                                // Redirect the user to the Profile Activity
                                Intent form_intent = new Intent(getApplicationContext(), Profile.class);
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
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, Profile.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ex != null) {
                                // Controllo che non ci siano eccezioni parse
                                check(ex.getCode(), vi, ex.getMessage());
                            }
                            if (ParseFacebookUtils.isLinked(user)) {
                                Log.d("Profile", "Connesso a Facebook");

                                // Redirect the user to the Profile Activity
                                Intent form_intent = new Intent(getApplicationContext(), Profile.class);
                                startActivity(form_intent);

                                finish();
                            }
                        }
                    });
                }
            });
        }
    }

    // In caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {
        // Redirect the user to the Homepage Activity
        Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    // Capitalize the first character of a string.
    public String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    // Create a side menu
    private void setUpMenu() {
        String[] navMenuTitles;
        TypedArray navMenuIcons;
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles, navMenuIcons);
    }

    private String formatDate(String s) {
        String[] dataArray = s.split(" ");
        for (int i = 0; i < dataArray.length; i++) {
            System.out.println("debug: " + i + dataArray[i]);
        }
        s = dataArray[2] + "/" + formatMonth(dataArray[1]) + "/" + dataArray[5];
        return s;
    }

    private String formatMonth(String s) {
        switch (s) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
        }
        return "";
    }
}
