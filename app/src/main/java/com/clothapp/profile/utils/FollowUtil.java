package com.clothapp.profile.utils;

import android.view.View;
import android.widget.Button;

import com.clothapp.R;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by niccolò on 24/02/2016.
 */
public class FollowUtil {

    private static ParseObject found = null;
    public static ParseObject isfollow(String from, String to){

        ParseQuery<ParseObject> queryfollow= new ParseQuery("Follow");
        queryfollow.whereEqualTo("from", from);
        queryfollow.whereEqualTo("to", to);
        queryfollow.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                found = object;
            }
        });
        return found;
    }
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
}
