package com.clothapp.parse.notifications;

import com.clothapp.parse.notifications.NotificationsUtils;
import com.clothapp.resources.Image;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Collections;

/**
 * Created by giacomoceribelli on 24/02/16.
 */
public class LikeRes {
    public static void deleteLike(String objectId, Image image, String username)  {
        ParseObject point = ParseObject.createWithoutData("Photo", objectId);
        //rimuovo like dall'immagine
        image.remLike(username);
        //rimuovo like da parse
        point.increment("nLike",-1);
        point.removeAll("like", Collections.singletonList(username));
        point.saveInBackground();
    }
    public static void addLike(String objectId, Image image, String username)  {
        ParseObject point = ParseObject.createWithoutData("Photo", objectId);
        //aggiungo like all'image
        image.addLike(username);

        // Send "Like" notification to the user who posted the image
        if (!image.getUser().equals(ParseUser.getCurrentUser().getUsername())) {
            NotificationsUtils.sendNotification(image.getUser(), "like");
        }

        //aggiungo like su parse
        point.addUnique("like", username);
        point.increment("nLike");
        point.saveInBackground();
    }
}
