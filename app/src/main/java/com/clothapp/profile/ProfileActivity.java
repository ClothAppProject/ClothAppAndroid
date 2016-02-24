package com.clothapp.profile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.BaseActivity;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.home_gallery.HomeActivity;
import com.clothapp.profile.utils.ProfilePictureCameraActivity;
import com.clothapp.profile.utils.ProfilePictureGalleryActivity;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.ExceptionCheck;
import com.clothapp.resources.Image;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class ProfileActivity extends BaseActivity {

    LinearLayout myGallery;
    Context mContext;
    ParseUser user;
    View vi;
    List<String> followers;
    List<String> following;
    ArrayList<Image> lista = new ArrayList<Image>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mContext = this;
        myGallery = (LinearLayout) findViewById(R.id.personal_gallery);

        String username = getIntent().getExtras().getString("user");

        vi = new View(this.getApplicationContext());

        user = getParseUser(username);

        // Set user profile image to the center of the screen
        final ImageView profilepicture = (ImageView) findViewById(R.id.profilepicture);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = (metrics.widthPixels) / 2;
        profilepicture.getLayoutParams().height = width;
        profilepicture.getLayoutParams().width  = width;

        // Create side menu
        setUpSideMenu();

        final TextView nfoto = (TextView) findViewById(R.id.nfoto);
        final TextView nfollowing = (TextView) findViewById(R.id.nfollowing);
        final TextView nfollowers = (TextView) findViewById(R.id.nfollowers);

        final TextView txtUsername = (TextView) findViewById(R.id.username_field);
        final TextView name = (TextView) findViewById(R.id.name_field);
        final Button follow_edit = (Button) findViewById(R.id.follow_edit);


        /*//tasto "segui" se profilo non tuo, "modifica profilo" se profilo tuo
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            follow_edit.setText("Modifica Profilo");

        } else {
            if (ParseUser.getCurrentUser().getList("following").contains(user.getUsername().toString()))   {
                //nel caso in cui si può smettere di seguire l'utente
                follow_edit.setText("Non Seguire");
                follow_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO problemi di permessi di scrittura sui follower di altri utenti
                        *//*List<String> yout = (user.getList("followers"));
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
                        }*//*
                    }
                });
            }else{
                //nel caso si può segurie l'utente
                follow_edit.setText("Segui");
                follow_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO problemi di permessi di scrittura sui follower di altri utenti
                        *//*
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
                        *//*
                    }
                });
            }
        }*/

        txtUsername.setText(user.getUsername());
        name.setText(capitalize(user.get("name").toString()));

        //imposto n° followers, n° following, n° foto
        if (user.getList("followers") != null) {
            nfollowers.setText(Integer.toString(user.getList("followers").size()));
        }
        if (user.getList("following") != null) {
            nfollowing.setText(Integer.toString(user.getList("following").size()));
        }

        loadUserPhotos(this);

        // Get Parse user profile picture

        loadUserProfilePhoto(this, profilepicture);

        //listener sull'imageview dell'immagine del profilo
        if (user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {

            setProfilePhotoClickListener(this, profilepicture);

        }
    }

    // Capitalize the first character of a string.
    private String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    // Create a side menu
    private void setUpSideMenu() {
        String[] navMenuTitles;
        TypedArray navMenuIcons;
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles, navMenuIcons, 1);

        final ImageView imageView = (ImageView) findViewById(R.id.ppProfile1);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.profProfile1);

        TextView textView = (TextView) findViewById(R.id.nameMenu);
        textView.setText(ParseUser.getCurrentUser().getString("name"));

        TextView textView2 = (TextView) findViewById(R.id.nameUsername);
        textView2.setText(ParseUser.getCurrentUser().getUsername());


        final View vi = new View(this.getApplicationContext());

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
        queryFoto.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //  if the user has a profile pic it will be shown in the side menu
                    //  else the app logo will be shown
                    if (objects.size() != 0) {
                        ParseFile f = objects.get(0).getParseFile("profilePhoto");

                        try {
                            File file = f.getFile();
                            Glide.with(getApplicationContext())
                                    .load(file)
                                    .centerCrop()
                                    .transform(new CircleTransform(ProfileActivity.this))
                                    .into(imageView);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });


        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });


        LinearLayout l = (LinearLayout) findViewById(R.id.drawer);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // ROBA DELLA GALLERIA
    public View insertPhoto(final ParseObject p,final int position) {

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
                    lista.add(new Image(file,p.getObjectId(),p.getString("user"),p.getList("like"),
                            p.getInt("nLike"),p.getList("hashtag"),p.getList("vestiti")));
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
                toPass.putExtra("lista",lista);
                toPass.putExtra("position", position);
                startActivity(toPass);
            }
        });
        layout.addView(imageView);
        return layout;
    }

    // In caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {

        // Redirect the user to the Homepage Activity
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);

        finish();
    }

    // Get user with a given username from Parse
    public static ParseUser getParseUser(String username) {

        ParseUser user = null;

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("username", username);

        try {
            user = queryUser.find().get(0);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return user;
    }

    public void loadUserPhotos(final ProfileActivity activity) {

        //ho creato questo thread per diminuire il lavoro del thread principale altrimenti scattava
        Thread putPhotos = new Thread(new Runnable() {
            @Override
            public void run() {
                ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
                queryFoto.whereEqualTo("user", activity.user.getUsername());
                queryFoto.orderByDescending("createdAt");
                queryFoto.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < objects.size(); i++) {
                                activity.myGallery.addView(activity.insertPhoto(objects.get(i),i));
                            }
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        putPhotos.start();
    }

    public void loadUserProfilePhoto(final ProfileActivity activity, final ImageView profilepicture) {

        ParseQuery<ParseObject> queryProfilePicture = new ParseQuery<ParseObject>("UserPhoto");
        queryProfilePicture.whereEqualTo("username", activity.user.getUsername());
        queryProfilePicture.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    final DonutProgress donutProgress = (DonutProgress) activity.findViewById(R.id.donut_progress_profile);
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
                                    Glide.with(activity.mContext)
                                            .load(data)
                                            .centerCrop()
                                            .placeholder(R.mipmap.profile)
                                            .into(profilepicture);
                                } else {
                                    check(e.getCode(), activity.vi, e.getMessage());
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
                    check(e.getCode(), activity.vi, e.getMessage());
                }
            }
        });
    }

    public void setProfilePhotoClickListener(final ProfileActivity activity, ImageView profilepicture) {
        profilepicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity.mContext);
                builder.setTitle(R.string.choose_profile_picture)
                        //.set
                        .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        // Redirect the user to the ProfilePictureCameraActivity Activity
                                        Intent intentCamera = new Intent(activity.getApplicationContext(), ProfilePictureCameraActivity.class);
                                        activity.startActivity(intentCamera);
                                        break;
                                    case 1:
                                        // Redirect the user to the ProfilePictureGalleryActivity Activity
                                        Intent intentGallery = new Intent(activity.getApplicationContext(), ProfilePictureGalleryActivity.class);
                                        activity.startActivity(intentGallery);
                                        break;
                                    case 2:
                                        //delete profile picture
                                        ParseQuery<ParseObject> queryFotoProfilo = new ParseQuery<ParseObject>("UserPhoto");
                                        queryFotoProfilo.whereEqualTo("username", activity.user.getUsername());
                                        queryFotoProfilo.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                if (e == null) {
                                                    if (objects.size() > 0) {
                                                        objects.get(0).deleteInBackground();
                                                        activity.finish();
                                                        activity.startActivity(activity.getIntent());
                                                    }
                                                } else {
                                                    check(e.getCode(), activity.vi, e.getMessage());
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
