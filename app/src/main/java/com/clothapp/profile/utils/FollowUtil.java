package com.clothapp.profile.utils;

import com.parse.FindCallback;
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

    public static ParseObject isfollow(String from, final String to){
        final ParseObject[] find = {null};

        ParseQuery<ParseObject> queryfollow= new ParseQuery<ParseObject>("Follow");
        queryfollow.whereEqualTo("from", from);
        queryfollow.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject o: objects){
                   if( o.get("to").equals(to)){
                    find[0] =o;
                   }
                }
            }
        } );
        return find[0];
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
