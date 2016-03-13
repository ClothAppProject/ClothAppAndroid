package com.clothapp.parse.notifications;

import android.util.Log;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;

import java.util.HashMap;

public class NotificationsUtils {

    // Send a push notification to a user with username = receiver
    public static void sendNotification(final String receiver, String message) {
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
