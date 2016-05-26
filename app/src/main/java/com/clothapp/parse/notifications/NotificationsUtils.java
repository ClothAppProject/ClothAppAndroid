package com.clothapp.parse.notifications;

import android.util.Log;

import com.clothapp.settings.UserSettingsUtil;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.HashMap;
import java.util.List;

public class NotificationsUtils {

    // Send a push notification to a user with username = receiver
    public static void sendNotification(final String receiver, final String type, final String objectid) {
        switch (type)   {
            case "like":
                //caso in cui devo inviare notifica per like
                //creo thread per evitare chiamate bloccanti quadno faccio la
                Thread like = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = ParseUser.getCurrentUser().getUsername() + " ha messo \"Mi Piace\" a una tua foto!";
                        //controllo se l'utente ha attiva la ricezione le notifiche per like
                        boolean check = UserSettingsUtil.checkNotificationLike(receiver);
                        send(check, receiver, message, objectid); //obectid della foto
                    }
                });
                like.start();
                break;
            case "follow":
                //caso in cui devo inviare notifica per follow
                //creo thread per evitare chiamate bloccanti
                Thread follow = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String message = ParseUser.getCurrentUser().getUsername() + " ha cominciato a seguirti!";
                        //controllo se l'utente ha attiva la ricezione di notifiche per followers
                        boolean check = UserSettingsUtil.checkNotificationFollower(receiver);
                        send(check, receiver, message, objectid); //obectid del profilo
                    }
                });
                follow.start();
                break;
            case "newPhoto":
                //caso in cui devo inviare notifica per nuova foto caricate
                // creo thread per evitare chiamate bloccanti
                Thread newPhoto = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final String photoMessage = ParseUser.getCurrentUser().getUsername() + " ha caricato una nuova foto!";

                        //ottengo follower ai quali inviare la foto
                        ParseQuery<ParseObject> notifyFollowers = new ParseQuery<>("Follow");
                        notifyFollowers.whereEqualTo("to", ParseUser.getCurrentUser().getUsername());
                        notifyFollowers.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    for (ParseObject o : objects) {

                                        //controllo se l'utente ha attiva la ricezione di notifiche per nuove foto di persone che segue
                                        boolean check = UserSettingsUtil.checkNotificationNewPhoto(o.getString("from"));
                                        send(check, o.getString("from"), photoMessage, objectid); //obectid della foto
                                    }
                                }
                            }
                        });
                    }
                });
                newPhoto.start();
                break;

        }
    }
    public static void send(boolean check, final String receiver, String message, String objectid) {
        //send notification only if granted
        if (check) {
            HashMap<String, Object> params = new HashMap<>();
            params.put("recipientUsername", receiver);
            params.put("message", message);
            params.put("objectid",objectid);

            // Call a Parse Cloud Code function. This function is hosted on Parse and
            // allows a deeper level of security. If you want to change the Cloud function
            // code, you have to modify the code hosted at ClothAppServer.
            ParseCloud.callFunctionInBackground("sendPushToUser", params, new FunctionCallback<String>() {

                @Override
                public void done(String success, ParseException e) {
                    if (e == null) {
                        // Push sent successfully
                        Log.d("NotificationsUtils", "Push sent successfully to " + receiver);
                    } else {
                        // Error...
                        Log.d("NotificationsUtils", "Could not send push notification...");
                    }
                }
            });
        }
    }

}
