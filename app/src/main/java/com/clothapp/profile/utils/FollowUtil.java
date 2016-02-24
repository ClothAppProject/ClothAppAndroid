package com.clothapp.profile.utils;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by niccolò on 24/02/2016.
 */
public class FollowUtil {

    public static boolean isfollow(String from, final String to){
        final boolean[] find={false};

        ParseQuery<ParseObject> queryfollow= new ParseQuery<ParseObject>("Follow");
        queryfollow.whereEqualTo("from", from);
        queryfollow.findInBackground(new FindCallback<ParseObject>(){
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                for(ParseObject o: objects){
                   if( o.get("to").equals(to)){
                    find[0]=true;
                   }
                }
            }
        } );
        return find[0];
    }
}
