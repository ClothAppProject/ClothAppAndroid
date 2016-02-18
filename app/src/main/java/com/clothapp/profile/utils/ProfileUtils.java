package com.clothapp.profile.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.clothapp.profile.adapters.ProfileInfoAdapter;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.fragments.ProfileUploadedPhotosFragment;
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
import java.util.concurrent.TimeUnit;

// This class helps keeping the code clean and modular.
public class ProfileUtils {

    // Object to store info about the user (not necessarily the current user).
    private static ParseUser user;
    // Object to store info about the "Persona" associated with the user above.
    private static ParseObject person;


    // Get info about the user from Parse.
    // Call getParseUser() and getParsePerson().
    public static void getParseInfo(final Context context, String username) {
        getParseUser(context, username);
        getParsePerson(context, username);
    }

    public static void getParseUploadedPhotos(String username, int start, int limit) {

        ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
        query.whereEqualTo("user", username);
        query.addDescendingOrder("createdAt");
        query.setSkip(start);
        query.setLimit(limit);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> photos, ParseException e) {
                if (e == null) {
                    // Log.d("ProfileUtils", "Ehi, Retrieved " + photos.size() + " results");

                    for (final ParseObject photo : photos) {
                        // Log.d("ProfileUtils", photo.getObjectId());

                        ParseFile parseFile = photo.getParseFile("thumbnail");
                        parseFile.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {

                                if (e == null) {

                                    // Log.d("ProfileUtils", "Loaded thumbnail for " + photo.getObjectId());

                                    RecyclerView view = UserProfileActivity.viewProfileUploadedPhotos;
                                    ProfileUploadedPhotosAdapter adapter = (ProfileUploadedPhotosAdapter) view.getAdapter();

                                    String objectId = photo.getObjectId();
                                    String username = photo.get("user").toString();
                                    int nLikes = photo.getInt("nLike");

                                    ProfileUploadedPhotosListItem item = new ProfileUploadedPhotosListItem(objectId, file, username, nLikes);

                                    item.hashtags = photo.getList("hashtag");
                                    item.clothes = photo.getList("vestiti");
                                    item.users = photo.getList("like");

                                    adapter.items.add(item);


                                    adapter.notifyDataSetChanged();

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

    public static void getParseUserProfileImage(final Activity activity, String username, final ImageView mainImageView, final ImageView drawerImageView) {

        ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject photo, ParseException e) {

                if (e == null) {
                    Log.d("ProfileUtils", "ParseObject for profile image found!");

                    ParseFile parseFile = photo.getParseFile("thumbnail");
                    parseFile.getFileInBackground(new GetFileCallback() {
                        @Override
                        public void done(File file, ParseException e) {

                            if (e == null) {
                                Log.d("ProfileUtils", "File for profile image found!");

                                Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

                                RoundedBitmapDrawable rounded = RoundedBitmapDrawableFactory.create(activity.getResources(), imageBitmap);
                                rounded.setCornerRadius(imageBitmap.getWidth());

                                mainImageView.setImageDrawable(rounded);
                                drawerImageView.setImageDrawable(rounded);

                            } else {
                                Log.d("ProfileUtils", "Error: " + e.getMessage());
                            }
                        }
                    });

                } else {
                    Log.d("ProfileUtils", "Error: " + e.getMessage());
                }
            }
        });
    }

    // Gets a ParseUser object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParseUser(final Context context, final String username) {

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseUser>() {

            @Override
            public void done(ParseUser object, ParseException e) {
                if (e == null) {
                    user = object;
                    // showDialog(context, "Success", "Successfully retrieved user info from Parse.");

                    updateListItem(0, user.get("name").toString());
                    updateListItem(3, user.getEmail());

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve user info. Check your Internet connection.");
                }
            }
        });
    }

    // Gets a ParseObject ("Persona") object for the given username.
    // The context arguments is needed to show a dialog in case of success or failure.
    private static void getParsePerson(final Context context, String username) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Persona");
        query.whereEqualTo("username", username);

        query.getFirstInBackground(new GetCallback<ParseObject>() {

            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    person = object;
                    // showDialog(context, "Success", "Successfully retrieved person info from Parse.");

                    long age = getAge(person.get("date").toString());

                    if (age < 0) updateListItem(1, "Not found");
                    else updateListItem(1, age + " years old");

                    updateListItem(2, person.get("city").toString());

                } else {
                    e.printStackTrace();
                    // showDialog(context, "Error", "Failed to retrieve person info. Check your Internet connection.");
                }
            }
        });
    }

    private static void updateListItem(int position, String text) {

        ProfileInfoAdapter adapter = (ProfileInfoAdapter) UserProfileActivity.viewProfileInfo.getAdapter();

        ProfileInfoListItem item = adapter.items.get(position + 1);
        item.setContent(text);

        adapter.notifyDataSetChanged();
    }

    private static long getAge(String text) {

        try {
            DateFormat format = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            Date birthday = format.parse(text);
            Date now = new Date();

            long diffInMillies = now.getTime() - birthday.getTime();
            return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) / 365;

        } catch (java.text.ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }

    // Shows a simple dialog with a title, a message and two buttons.
    // The context argument is needed to show the dialog inside the UserProfileActivity activity.
    private static void showDialog(Context context, String title, String message) {
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
}