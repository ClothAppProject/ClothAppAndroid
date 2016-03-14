package com.clothapp.login_signup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.clothapp.R;
import com.clothapp.http.Get;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by giacomoceribelli on 06/01/16.
 */

// infromazioni qui: http://blog.grafixartist.com/facebook-login-with-parse-part-2/

public class FacebookUtil {

    private static String name;
    private static String email;
    private static String lastname;
    private static Date birthday;
    private static ParseException ret = null;

    // Funzione per prelevare le informazioni da facebook e inserirle in Parse
    public static ParseException getUserDetailsRegisterFB(ParseUser uth, View v, final Context context) throws InterruptedException {
        final View vi = v;
        final ParseUser user = uth;
        // final SharedPreferences userInformation = userInfo;

        // Prelevo informazioni da facebook
        Bundle parameters = new Bundle();

        // specifico i parametri che voglio ottenere da facebook
        parameters.putString("fields", "birthday,email,first_name,last_name,gender,picture.type(large)");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(), "/me", parameters, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(final GraphResponse response) {
                // Prelevo il risultato
                try {
                    String dateStr = response.getJSONObject().getString("birthday");
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
                    birthday = sdf.parse(dateStr);

                    email = response.getJSONObject().getString("email");
                    name = response.getJSONObject().getString("first_name");
                    lastname = response.getJSONObject().getString("last_name");

                    JSONObject picture = response.getJSONObject().getJSONObject("picture");
                    JSONObject data = picture.getJSONObject("data");
                    //  Returns a 50x50 profile picture
                    String pictureUrl = data.getString("url");

                    Log.d("FacebookUtil", "Informazioni prelevate da Facebook");

                    // Inserisco le info nel ParseUser
                    user.setEmail(email);
                    user.put("name", name.trim());
                    user.put("flagISA","Persona");
                    user.put("Settings", context.getString(R.string.default_settings));
                    try {
                        //uso save e non savebackground perchè non deve essere asincrona
                        user.save();

                        ParseObject persona = new ParseObject("Persona");
                        persona.put("username",user.getUsername());
                        persona.put("lastname", lastname.trim());
                        if (response.getJSONObject().getString("gender").equals("male")) {
                            persona.put("sex","m");
                        }else{
                            persona.put("sex","f");
                        }
                        persona.put("date", birthday);
                        //persona.put("city",citta.trim());
                        persona.save();

                        //scarico l'immagine presa da facebook
                        URL aURL = new URL(pictureUrl);
                        URLConnection conn = aURL.openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);

                        //comprilo l'immagine in byte[] e la invio come parseFile
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        (BitmapFactory.decodeStream(bis)).compress(Bitmap.CompressFormat.JPEG, 70, stream);
                        byte[] pp = stream.toByteArray();

                        ParseFile file = new ParseFile("facebook_picture.jpg", pp);
                        file.save();
                        // Creazione di un ParseObject da inviare
                        ParseObject userPhoto = new ParseObject("UserPhoto");
                        userPhoto.put("username", user.getUsername());
                        userPhoto.put("profilePhoto", file);
                        userPhoto.save();

                        //chiamata get per salvare il thumbnail
                        String url = "http://clothapp.parseapp.com/createprofilethumbnail/"+userPhoto.getObjectId();
                        Get g = new Get();
                        g.execute(url);
                    } catch (ParseException e) {
                        ret = e;
                        System.out.println("debug: ret = "+ret.getMessage().toString());

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (JSONException e) {
                    System.out.println("debug: eccezione nell'ottenere info da facebook");

                } catch (java.text.ParseException e) {
                    System.out.println("debug: eccezione nel formattare la data");

                }
            }
        }
        ).executeAndWait();
        return ret;
    }
}
