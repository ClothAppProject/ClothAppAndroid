package com.clothapp.profile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.image_detail.ZoomPhoto;
import com.clothapp.profile.fragments.ProfileUploadedPhotosFragment;
import com.clothapp.profile_shop.ShopProfileActivity;
import com.clothapp.profile.adapters.ProfileInfoAdapter;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile_shop.adapters.ProfileShopInfoAdapter;
import com.clothapp.profile_shop.fragments.ProfileShopUploadedPhotosFragment;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Image;
import com.clothapp.upload.UploadProfilePictureActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.clothapp.resources.ExceptionCheck.check;

// This class helps keeping the code clean and modular.
public class ProfileUtils {

    // Object to store info about the user (not necessarily the current user).
    private static ParseUser user;
    // Object to store info about the "Persona" associated with the user above.
    private static ParseObject persona;
    // Object to store info about the "LocalShop" associated with the user above.
    private static ParseObject shop;

    private final static int SHOP_INFO_COUNT = 5;
    private final static int USER_INFO_COUNT = 4;


    // Get info about the user from Parse.
    // Call getParseUser() and getParsePerson() and getParseShop.
    public static void getParseInfo(final Context context, String username, RecyclerView viewProfileInfo) {
        getParseUser(context, username, viewProfileInfo);
        getParsePerson(context, username, viewProfileInfo);
    }

    // Call getParseUser() and getParseShop().
    public static void getParseShopInfo(final Context context, String username, RecyclerView viewProfileInfo) {
        getParseUserShop(context, username, viewProfileInfo);
        getParseShop(context, username, viewProfileInfo);
    }


    public static void getParseUploadedPhotos(String username, final int start, int limit, final ProgressBar progressBar,
                                              final RecyclerView viewProfileUploadedPhotos, final TextView noPhotosText) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.whereEqualTo("user", username);
        query.addDescendingOrder("createdAt");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    //check if user has no photo uploaded
                    if (start==0 && photos.isEmpty())   {
                        noPhotosText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    RecyclerView view = viewProfileUploadedPhotos;
                    final ProfileUploadedPhotosAdapter adapter = (ProfileUploadedPhotosAdapter) view.getAdapter();

                    // Log.d("ProfileUtils", "Ehi, Retrieved " + photos.size() + " results");

                    for (final ParseObject photo : photos) {
                        // Log.d("ProfileUtils", photo.getObjectId());

                        ParseFile parseFile = photo.getParseFile("thumbnail");
                        parseFile.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {

                                if (e == null) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    // Log.d("ProfileUtils", "Loaded thumbnail for " + photo.getObjectId());

                                    Image item = new Image(file, photo.getObjectId(), photo.getString("user"),
                                            photo.getList("like"), photo.getInt("nLike"), photo.getList("hashtag"),
                                            photo.getList("vestiti"), photo.getList("tipo"));

                                    if (!adapter.photos.contains(item)) {

                                        adapter.photos.add(item);
                                        adapter.notifyDataSetChanged();
                                    }

                                } else {
                                    Log.d("ProfileUtils", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }

                } else {
                    Log.d("ProfileUtils", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getShopParseUploadedPhotos(String username, final int start, int limit, final ProgressBar progressBar,
                                                  final RecyclerView viewProfileUploadedPhotos, final TextView noPhotosText) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.whereEqualTo("user", username);
        query.addDescendingOrder("createdAt");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    //check if user has no photo uploaded
                    if (start==0 && photos.isEmpty())   {
                        noPhotosText.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    RecyclerView view = viewProfileUploadedPhotos;
                    final ProfileUploadedPhotosAdapter adapter = (ProfileUploadedPhotosAdapter) view.getAdapter();

                    // Log.d("ProfileUtils", "Ehi, Retrieved " + photos.size() + " results");

                    for (final ParseObject photo : photos) {
                        // Log.d("ProfileUtils", photo.getObjectId());

                        ParseFile parseFile = photo.getParseFile("thumbnail");
                        parseFile.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {

                                if (e == null) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    // Log.d("ProfileUtils", "Loaded thumbnail for " + photo.getObjectId());

                                    Image item = new Image(file, photo.getObjectId(), photo.getString("user"),
                                            photo.getList("like"), photo.getInt("nLike"), photo.getList("hashtag"),
                                            photo.getList("vestiti"), photo.getList("tipo"));

                                    if (!adapter.photos.contains(item)) {
                                        adapter.photos.add(item);
                                        adapter.notifyDataSetChanged();
                                    }

                                } else {
                                    Log.d("ProfileUtils", "Error: " + e.getMessage());
                                }
                            }
                        });
                    }

                } else {
                    Log.d("ProfileUtils", "Error: " + e.getMessage());
                }
            }
        });
    }

    public static void getParseUserProfileImage(final String username, final ImageView mainImageView, final Context context, final boolean shop,final Context dialogContext) {

        ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject photo, final ParseException e) {
                if (e == null) {
                    Log.d("ProfileUtils", "ParseObject for profile image found!");

                    ParseFile parseFile = photo.getParseFile("thumbnail");
                    if(parseFile==null || parseFile.getUrl()==null) {
                        //if thumbnail not already created
                        parseFile = photo.getParseFile("profilePhoto");
                        System.out.println("debug richiesta foto a dimensione originale");
                    }
                    if (!shop) {
                        Glide.with(context)
                                .load(parseFile.getUrl())
                                .transform(new CircleTransform(context))
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_circle)
                                .into(mainImageView);
                    } else {
                        Glide.with(context)
                                .load(parseFile.getUrl())
                                .placeholder(R.drawable.shop)
                                .into(mainImageView);
                    }
                    mainImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(context, ZoomPhoto.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("url",photo.getParseFile("profilePhoto").getUrl());
                            context.startActivity(i);
                        }
                    });
                }
                if (username.equals(ParseUser.getCurrentUser().getUsername())) {
                    mainImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {
                            final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(dialogContext);
                            //esisite immagine del profilo
                            int array = R.array.profile_picture_options;
                            if (e != null) array = R.array.profile_picture_options_negative;
                            builder.setTitle(R.string.choose_profile_picture)
                                    .setItems(array, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent i = new Intent(context, UploadProfilePictureActivity.class);
                                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            switch (which) {
                                                case 0:
                                                    // Redirect the user to the ProfilePictureActivity with camera
                                                    i.putExtra("photoType", 2187);
                                                    context.startActivity(i);
                                                    break;
                                                case 1:
                                                    // Redirect the user to the ProfilePictureActivity with galery
                                                    i.putExtra("photoType", 1540);
                                                    context.startActivity(i);
                                                    break;
                                                case 2:
                                                    i = new Intent(context, ZoomPhoto.class);
                                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    i.putExtra("url",photo.getParseFile("profilePhoto").getUrl());
                                                    context.startActivity(i);
                                                    break;
                                                case 3:
                                                    //delete profile picture
                                                    photo.deleteInBackground();
                                                    Glide.clear(mainImageView);
                                                    Glide.with(context)
                                                            .load(R.drawable.com_facebook_profile_picture_blank_circle)
                                                            .into(mainImageView);
                                                    break;
                                            }
                                        }
                                    });
                            android.app.AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    // Gets a ParseUser object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParseUser(final Context context, final String username, final RecyclerView viewProfileInfo) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseUser>() {

            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    user = object;

                    //controllo che parametri non siano null
                    if (user.getString("name")!=null) {
                        updateListItem(0, user.get("name").toString(), viewProfileInfo);
                        updateListItem(3, user.getEmail(), viewProfileInfo);
                    }

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve user info. Check your Internet connection.");
                }
            }
        });
    }

    private static void getParseUserShop(final Context context, final String username, final RecyclerView viewProfileInfo) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {

            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    user = object;
                    // showDialog(context, "Success", "Successfully retrieved user info from Parse.");

                    //controllo in caso di valori nulli
                    if (user.getString("name")!=null) {
                        updateShopListItem(0, user.get("name").toString(), viewProfileInfo);
                        updateShopListItem(3, user.getEmail(), viewProfileInfo);
                    }

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve user info. Check your Internet connection.");
                }
            }
        });
    }

    // Gets a ParseObject ("Persona") object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParsePerson(final Context context, String username, final RecyclerView viewProfileInfo) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Persona");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    persona = object;
                    // showDialog(context, "Success", "Successfully retrieved person info from Parse.");

                    //controllo in caso di valori nulli
                    if (persona.getDate("date")!=null) {
                        long age = getAge(persona.getDate("date"));

                        if (age < 0) updateListItem(1, "Not found", viewProfileInfo);
                        else updateListItem(1, age + " years old", viewProfileInfo);

                        updateListItem(2, persona.getString("city"), viewProfileInfo);
                    }
                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve person info. Check your Internet connection.");
                }
            }
        });
    }

    private static void getParseShop(final Context context, final String username, final RecyclerView viewProfileInfo) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("LocalShop");
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    shop = object;
                    // showDialog(context, "Success", "Successfully retrieved shop info from Parse.");

                    //controllo in caso di valori nulli
                    if (!(shop.getString("address")==null && shop.getString("webSite")==null)) {
                        updateShopListItem(1, shop.getString("address"), viewProfileInfo);
                        updateShopListItem(2, shop.getString("Citta"), viewProfileInfo);
                        updateShopListItem(4, shop.getString("webSite"), viewProfileInfo);
                    }

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve shop info. Check your Internet connection.");
                }
            }
        });
    }

    private static void updateListItem(int position, String text, RecyclerView viewProfileInfo) {

        ProfileInfoAdapter adapter = (ProfileInfoAdapter) viewProfileInfo.getAdapter();

        if (text != null && !text.isEmpty()) {
            ProfileInfoListItem item = adapter.items.get(position + 1 - (USER_INFO_COUNT - adapter.items.size() + 1));
            item.setContent(text);
        } else {
            adapter.items.remove(position + 1 - (USER_INFO_COUNT - adapter.items.size() + 1));
        }

        adapter.notifyDataSetChanged();
    }

    private static void updateShopListItem(int position, String text, RecyclerView viewProfileInfo) {

        ProfileShopInfoAdapter adapter = (ProfileShopInfoAdapter) viewProfileInfo.getAdapter();

        int toGet = position + 1 - (SHOP_INFO_COUNT - adapter.items.size() + 1);

        if (toGet < 0) return;

        if (text != null && !text.isEmpty()) {
            ProfileInfoListItem item = adapter.items.get(toGet);
            item.setContent(text);
        } else {
            adapter.items.remove(position + 1 - (SHOP_INFO_COUNT - adapter.items.size() + 1));
        }

        adapter.notifyDataSetChanged();
    }

    private static long getAge(Date birthday) {

        Date now = new Date();

        long diffInMillies = now.getTime() - birthday.getTime();
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;

//        try {
//            DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);
//            Date birthday = format.format(date);
//            Date now = new Date();
//
//            long diffInMillies = now.getTime() - birthday.getTime();
//            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;
//
//        } catch (java.text.ParseException e) {
//            e.printStackTrace();
//            return -1;
//        }
    }

    // Shows a simple dialog with a title, a message and two buttons.
    // The context argument is needed to show the dialog inside the UserProfileActivity activity.
    public static void showDialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);

        String positiveText = "OK";
        builder.setPositiveButton(positiveText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                    }
                });

        String negativeText = "CANCEL";
        builder.setNegativeButton(negativeText,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();
    }

    public static void goToProfile(final Context context, final String username) {
        //controllo se username è un negozio o una persona
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e==null)    {
                    if(user==null || user.getString("flagISA")==null)   {
                        Snackbar.make( new View(context),"riprova",Snackbar.LENGTH_SHORT);
                    } else {
                        Intent i = null;
                        if (user.getString("flagISA").equals("Persona")) {
                            i = new Intent(context, UserProfileActivity.class);
                        } else {
                            i = new Intent(context, ShopProfileActivity.class);
                        }
                        i.putExtra("user", username);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                }else{
                    check(e.getCode(), new View(context), e.getMessage());
                }
            }
        });
    }
}