package com.clothapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class ProfileActivity extends BaseActivity {
    private List<ParseObject> fotos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        try {
            getSupportActionBar().setTitle(R.string.profile_button);
        } catch (NullPointerException e) {
            Log.d("ProfileActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        //imposto immagine profilo a metà schermo
        ImageView profilepicture = (ImageView) findViewById(R.id.profilepicture);
        DisplayMetrics metrics =this.getResources().getDisplayMetrics();
        int width=(metrics.widthPixels)/2;
        profilepicture.getLayoutParams().height = width;
        profilepicture.getLayoutParams().width =width;

        // Create side menu
        setUpMenu();
        final View vi = new View(this.getApplicationContext());
        final TextView nfoto = (TextView) findViewById(R.id.nfoto);
        final TextView nfollowing = (TextView) findViewById(R.id.nfollowing);
        final TextView nfollowers = (TextView) findViewById(R.id.nfollowers);

        final TextView username = (TextView) findViewById(R.id.username_field);
        final TextView name = (TextView) findViewById(R.id.name_field);
        final TextView lastname = (TextView) findViewById(R.id.lastname_field);
        final TextView date = (TextView) findViewById(R.id.date_field);

        // Get current Parse user
        final ParseUser user = ParseUser.getCurrentUser();
        username.setText(user.getUsername());
        name.setText(capitalize(user.get("name").toString()));

        //imposto n° followers, n° following, n° foto
        if (user.getList("followers")!=null) {
            nfollowers.setText(user.getList("followers").size());
        }
        if (user.getList("following")!=null) {
            nfollowing.setText(user.getList("following").size());
        }
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("user", user.getUsername());
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null)    {
                    fotos = objects;
                    for (int i=0;i<fotos.size();i++)    {
                        System.out.println("debug: imamgine"+fotos.get(i).getObjectId());
                    }
                    if (fotos!=null) {
                        nfoto.setText(""+objects.size());
                    }
                }else{
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });

        ParseQuery<ParseObject> queryInfo = new ParseQuery<ParseObject>("Persona");
        queryInfo.whereEqualTo("username", user.getUsername());
        queryInfo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    lastname.setText(objects.get(0).get("lastname").toString());
                    String timeStamp = formatDate(objects.get(0).get("date").toString());
                    date.setText(timeStamp);
                }else{
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });



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

        set(navMenuTitles, navMenuIcons,1);
    }

    private String formatDate(String s) {
        String[] dataArray = s.split(" ");
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
