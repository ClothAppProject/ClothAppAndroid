package com.clothapp.profile;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// This class helps keeping the code clean and modular.
class ProfileUtils {

    // Object to store info about the user (not necessarily the current user).
    static ParseUser user;
    // Object to store info about the "Persona" associated with the user above.
    static ParseObject person;


    // Get info about the user from Parse.
    // Call getParseUser() and getParsePerson().
    static void getParseInfo(final Context context, String username) {
        getParseUser(context, username);
        getParsePerson(context, username);
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

        }  catch (java.text.ParseException e) {
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