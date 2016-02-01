package com.clothapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.profilepicture.*;
import com.clothapp.resources.BitmapUtil;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.ExceptionCheck;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.GetFileCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class ProfileActivity extends BaseActivity {

    LinearLayout myGallery;
    Context mContext;
    ParseUser user;
    View vi;
    List<String> followers;
    List<String> following;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mContext = this;
        myGallery = (LinearLayout) findViewById(R.id.personal_gallery);
        String nome = getIntent().getExtras().getString("user");
        try {
            getSupportActionBar().setTitle(nome);
        } catch (NullPointerException e) {
            Log.d("ProfileActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        vi = new View(this.getApplicationContext());

        //ottengo user
        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("username", nome);
        try {
            user = queryUser.find().get(0);
            followers = user.getList("followers");
            following = user.getList("following");
        } catch (ParseException e) {
            ExceptionCheck.check(e.getCode(), vi, e.getMessage());
        }


        //imposto immagine profilo a metà schermo
        final ImageView profilepicture = (ImageView) findViewById(R.id.profilepicture);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = (metrics.widthPixels) / 2;
        profilepicture.getLayoutParams().height = width;
        profilepicture.getLayoutParams().width  = width;

        // Create side menu
        setUpMenu();

        final TextView nfoto = (TextView) findViewById(R.id.nfoto);
        final TextView nfollowing = (TextView) findViewById(R.id.nfollowing);
        final TextView nfollowers = (TextView) findViewById(R.id.nfollowers);

        final TextView username = (TextView) findViewById(R.id.username_field);
        final TextView name = (TextView) findViewById(R.id.name_field);
        final Button follow_edit = (Button) findViewById(R.id.follow_edit);


        //tasto "segui" se profilo non tuo, "modifica profilo" se profilo tuo
        if (user.getUsername().toString()==ParseUser.getCurrentUser().getUsername().toString()) {
            follow_edit.setText("Modifica Profilo");
        }else{
            if (ParseUser.getCurrentUser().getList("following").contains(user.getUsername().toString()))   {
                //nel caso in cui si può smettere di seguire l'utente
                follow_edit.setText("Non Seguire");
                follow_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO problemi di permessi di scrittura sui follower di altri utenti
                        /*List<String> yout = (user.getList("followers"));
                        yout.remove(ParseUser.getCurrentUser().getUsername());
                        List<String> pout = ParseUser.getCurrentUser().getList("following");
                        pout.remove(user.getUsername());
                        user.put("followers",yout);
                        ParseUser.getCurrentUser().put("following",pout);
                        try {
                            user.save();
                            ParseUser.getCurrentUser().save();
                        } catch (ParseException e) {
                            ExceptionCheck.check(e.getCode(), vi, e.getMessage());
                        }*/
                    }
                });
            }else{
                //nel caso si può segurie l'utente
                follow_edit.setText("Segui");
                follow_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO problemi di permessi di scrittura sui follower di altri utenti
                        /*
                        user.add("followers",ParseUser.getCurrentUser().getUsername());
                        ParseUser.getCurrentUser().add("following",user.getUsername());
                        try {
                            user.save();
                            ParseUser.getCurrentUser().save();
                        }catch (ParseException e)   {
                            ExceptionCheck.check(e.getCode(), vi, e.getMessage());
                        }
                        finish();
                        startActivity(getIntent());
                        */
                    }
                });
            }
        }

        // Get Parse user
        username.setText(user.getUsername());
        name.setText(capitalize(user.get("name").toString()));

        //imposto n° followers, n° following, n° foto
        if (user.getList("followers") != null) {
            nfollowers.setText(Integer.toString(user.getList("followers").size()));
        }
        if (user.getList("following") != null) {
            nfollowing.setText(Integer.toString(user.getList("following").size()));
        }
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("user", user.getUsername());
        queryFoto.orderByDescending("createdAt");
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        myGallery.addView(insertPhoto(objects.get(i)));
                    }
                    if (objects != null) {
                        nfoto.setText("" + objects.size());
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });


        //Get Parse user profile picture
        ParseQuery<ParseObject> queryProfilePicture = new ParseQuery<ParseObject>("UserPhoto");
        queryProfilePicture.whereEqualTo("username", user.getUsername());
        queryProfilePicture.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    final DonutProgress donutProgress = (DonutProgress) findViewById(R.id.donut_progress_profile);
                    if (objects.size() == 0) {
                        donutProgress.setVisibility(View.INVISIBLE);
                        profilepicture.setImageResource(R.mipmap.profile);
                    } else {
                        profilepicture.setVisibility(View.INVISIBLE);
                        ParseFile picture = objects.get(0).getParseFile("profilePhoto");
                        picture.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File data, ParseException e) {
                                if (e == null) {
                                    donutProgress.setVisibility(View.INVISIBLE);
                                    profilepicture.setVisibility(View.VISIBLE);
                                    Glide.with(mContext)
                                            .load(data)
                                            .centerCrop()
                                            .placeholder(R.mipmap.profile)
                                            .into(profilepicture);
                                } else {
                                    check(e.getCode(), vi, e.getMessage());
                                }
                            }
                        }, new ProgressCallback() {
                                @Override
                                public void done (Integer percentDone){
                                    donutProgress.setProgress(percentDone);
                                }
                            });
                        }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });

        //listener sull'imageview dell'immagine del profilo
        if (user.getUsername().toString() == ParseUser.getCurrentUser().getUsername().toString()) {
            profilepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.choose_profile_picture)
                            //.set
                            .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            // Redirect the user to the ProfileCameraActivity Activity
                                            Intent intentCamera = new Intent(getApplicationContext(), ProfileCameraActivity.class);
                                            startActivity(intentCamera);
                                            break;
                                        case 1:
                                            // Redirect the user to the ProfileGalleryActivity Activity
                                            Intent intentGallery = new Intent(getApplicationContext(), ProfileGalleryActivity.class);
                                            startActivity(intentGallery);
                                            break;
                                        case 2:
                                            //delete profile picture
                                            ParseQuery<ParseObject> queryFotoProfilo = new ParseQuery<ParseObject>("UserPhoto");
                                            queryFotoProfilo.whereEqualTo("username", user.getUsername());
                                            queryFotoProfilo.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() > 0) {
                                                            objects.get(0).deleteInBackground();
                                                            finish();
                                                            startActivity(getIntent());
                                                        }
                                                    } else {
                                                        check(e.getCode(), vi, e.getMessage());
                                                    }
                                                }
                                            });
                                            break;
                                    }
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
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

        set(navMenuTitles, navMenuIcons, 1);
    }

    //ROBA DELLA GALLERIA
    private View insertPhoto(ParseObject p) {
        //inizializzo layout
        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
        layout.setGravity(Gravity.CENTER);

        final ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //inserisco foto nell'imageview
        ParseFile f = p.getParseFile("thumbnail");
        f.getFileInBackground(new GetFileCallback() {
            @Override
            public void done(File file, ParseException e) {
                if (e == null) {
                    Glide.with(mContext)
                            .load(file)
                            .centerCrop()
                            .placeholder(R.mipmap.gallery_icon)
                            .into(imageView);
                } else {
                    ExceptionCheck.check(e.getCode(), vi, e.getMessage());
                }
            }
        });
        //setto listener su ogni imageview
        final ParseObject idToPass = p;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPass = new Intent(getApplicationContext(), ImageFragment.class);
                toPass.putExtra("objectId", idToPass.getObjectId().toString());
                startActivity(toPass);
            }
        });
        layout.addView(imageView);
        return layout;
    }
}
