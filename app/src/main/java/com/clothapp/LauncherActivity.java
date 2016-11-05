package com.clothapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.clothapp.login_signup.MainActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class LauncherActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        //prima cosa da fare è controllare se c'è connessione ad internet!!
        if (!isNetworkAvailable()) {
            Log.d("LauncherActivity", "Killing the application...");
            showDialog(LauncherActivity.this, "Error!", "Please check your Internet connection.");
        } else {
            Log.d("LauncherActivity", "Network OK");


            // nascondo la status bar
            // requestWindowFeature(Window.FEATURE_NO_TITLE);
            // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

            // Straight from android documentation:
            // Then, from the onCreate() method in your application's main activity—and in any other activity
            // through which the user may enter your application for the first time—call setDefaultValues():
            PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        "com.clothapp",
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("LauncherActivity", "KeyHash = " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }

            } catch (PackageManager.NameNotFoundException e) {
                Log.d("LauncherActivity", "Error: " + e.getMessage());
                e.printStackTrace();

            } catch (NoSuchAlgorithmException e) {
                Log.d("LauncherActivity", "Error: " + e.getMessage());
                e.printStackTrace();
            }

            // Current user initialization. Parse handles all the data on its own.
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser != null) {
                //update the ParseUser
                currentUser.fetchInBackground();

                // The user has already logged in once before.
                Log.d("LauncherActivity", "Already logged in as " + currentUser.getUsername());

                // Skip log in page. Go directly to Splash Screen Activity.
                // Intent intent = new Intent(this, SplashScreenActivity.class);
                Intent intent = new Intent(this, SplashScreenActivity.class);
                startActivity(intent);

                // This function stops the current activity and calls OnPause(), OnStop() and OnDestroy in this order.
                finish();
            } else {
                // The current user needs to log in or sign up.
                Log.d("LauncherActivity", "Not logged in... Redirecting to login activity");

                // Go to the login/signup page.
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                // This function stops the current activity and calls OnPause(), OnStop() and OnDestroy in this order.
                finish();
            }
        }
    }
    public void moveAllImages() {
        final ParseQuery photo = new ParseQuery("Photo");
        photo.setLimit(150);
        photo.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    System.out.println("allora Photo=" + objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            ParseFile pPhoto = objects.get(i).getParseFile("photo");
                            byte[] dataPhoto = pPhoto.getData();
                            ParseFile fPhoto = new ParseFile(pPhoto.getName(), dataPhoto);
                            //fPhoto.save();
                            objects.get(i).put("photo", fPhoto);

                            ParseFile pThumbnail = objects.get(i).getParseFile("thumbnail");
                            byte[] dataThumbnail = pThumbnail.getData();
                            ParseFile tPhoto = new ParseFile(pThumbnail.getName(), dataThumbnail);
                            //tPhoto.save();
                            objects.get(i).put("thumbnail", tPhoto);

                            //objects.get(i).save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

        final ParseQuery userPhoto = new ParseQuery("UserPhoto");
        userPhoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e==null) {
                    System.out.println("allora Userphoto=" + objects.size());
                    for (int i = 0; i < objects.size(); i++) {
                        try {
                            ParseFile pPhoto = objects.get(i).getParseFile("profilePhoto");
                            byte[] dataPhoto = pPhoto.getData();
                            ParseFile fPhoto = new ParseFile(pPhoto.getName(), dataPhoto);
                            //fPhoto.save();
                            objects.get(i).put("profilePhoto", fPhoto);

                            ParseFile pThumbnail = objects.get(i).getParseFile("thumbnail");
                            byte[] dataThumbnail = pThumbnail.getData();
                            ParseFile tPhoto = new ParseFile(pThumbnail.getName(), dataThumbnail);
                            //tPhoto.save();
                            objects.get(i).put("thumbnail", tPhoto);

                            //objects.get(i).save();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    private static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        String positiveText = "CLOSE APP";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

}
