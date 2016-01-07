package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
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
        getSupportActionBar().setTitle(R.string.profile_button);

        setUpMenu();
        TextView username = (TextView) findViewById(R.id.username_field);
        TextView name = (TextView) findViewById(R.id.name_field);
        TextView lastname = (TextView) findViewById(R.id.lastname_field);
        TextView email = (TextView) findViewById(R.id.email_field);
        TextView date = (TextView) findViewById(R.id.date_field);

        final ParseUser user = ParseUser.getCurrentUser();
        username.setText(user.getUsername().toString());
        name.setText(capitalize(user.get("name").toString()));
        lastname.setText(capitalize(user.get("lastname").toString()));
        email.setText(user.get("email").toString());
        date.setText(user.get("date").toString());

        final Button connect = (Button) findViewById(R.id.facebook_connect_button);
        final Button disconnect = (Button) findViewById(R.id.facebook_disconnect_button);
        //controlliamo se è connesso
        if (ParseFacebookUtils.isLinked(user)) {
            //l'utente è già connesso gli do solo l'opzione per disconnettersi da facebook
            connect.setVisibility(View.INVISIBLE);
            disconnect.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
                @Override
                public void onClick(View v) {
                    final View vi = v;
                    ParseFacebookUtils.unlinkInBackground(user, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ex == null) {
                                System.out.println("debug: disconnesso a facebook");
                                Intent form_intent = new Intent(getApplicationContext(), Profile.class);
                                startActivity(form_intent);
                                finish();
                            }else{
                                //controllo che non ci siano eccezioni parse
                                check(ex.getCode(), vi, ex.getMessage());
                            }
                        }
                    });
                }
            });
        }else{
            //l'utente non è connesso a facebook, gli do l'opzione per connettersi
            disconnect.setVisibility(View.INVISIBLE);
            connect.setOnClickListener(new View.OnClickListener() { //metto bottone login in ascolto del click
                @Override
                public void onClick(View v) {
                    final View vi = v;
                    //specifico i campi ai quali sono interessato quando richiedo permesso ad utente
                    List<String> permissions = Arrays.asList("email", "public_profile", "user_birthday");
                    ParseFacebookUtils.linkWithReadPermissionsInBackground(user, Profile.this, permissions, new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ex != null) {
                                //controllo che non ci siano eccezioni parse
                                check(ex.getCode(), vi, ex.getMessage());
                            }
                            if (ParseFacebookUtils.isLinked(user)) {
                                System.out.println("debug: connesso a facebook");
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

    //in caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {
        // reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    public String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    private void setUpMenu() {
        String[] navMenuTitles;
        TypedArray navMenuIcons;
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles, navMenuIcons);
    }
}
