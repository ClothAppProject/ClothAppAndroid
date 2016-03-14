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
    public static void sendNotification(final String receiver, String type) {
        String message = null;
        boolean check = false;
        switch (type)   {
            case "like":
                //caso in cui devo inviare notifica per like
                message = ParseUser.getCurrentUser().getUsername() + " ha messo \"Mi Piace\" a una tua foto!";
                //controllo se l'utente ha attiva la ricezione le notifiche per like
                check = UserSettingsUtil.checkNotificationLike(receiver);
                send(check, receiver, message);
                break;
            case "follow":
                //caso in cui devo inviare notifica per follow
                message = ParseUser.getCurrentUser().getUsername() + " ha cominciato a seguirti!";
                //controllo se l'utente ha attiva la ricezione di notifiche per followers
                check = UserSettingsUtil.checkNotificationFollower(receiver);
                send(check, receiver, message);
                break;
            case "newPhoto":
                System.out.println("debug invio notifiche ai follower");
                //caso in cui devo inviare notifica per nuova foto caricata
                message = ParseUser.getCurrentUser().getUsername() + " ha caricato una nuova foto!";

                //ottengo follower ai quali inviare la foto
                ParseQuery<ParseObject> notifyFollowers = new ParseQuery<>("Follow");
                notifyFollowers.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null)    {
                            for (ParseObject o : objects)    {
                                //controllo se l'utente ha attiva la ricezione di notifiche per nuove foto di persone che segue
                                boolean check = UserSettingsUtil.checkNotificationNewPhoto(o.getString("from"));
                                send(check, receiver, o.getString("from"));
                            }
                        }
                    }
                });

                break;

        }
    }
    public static void send(boolean check, final String receiver, String message) {
        //send notification only if granted
        if (check) {
            System.out.println("debug "+message);
            HashMap<String, Object> params = new HashMap<>();
            params.put("recipientUsername", receiver);
            params.put("message", message);

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
