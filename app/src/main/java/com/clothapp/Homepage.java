package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseSession;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class Homepage extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private View vi = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //  just a sample button it does nothing when clicked
        Button button_final = (Button) findViewById(R.id.final_button);

        //button upload a new photo
        FloatingActionButton upload = (FloatingActionButton) findViewById(R.id.upload_button);
        upload.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view_upload) {
                vi = view_upload;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //  logout button
        Button button_logout = (Button) findViewById(R.id.form_logout_button);
        button_logout.setOnClickListener(new View.OnClickListener() { //metto bottone logout in ascolto del click
            @Override
            public void onClick(View view_logout) {
                // TODO Auto-generated method stub
                switch (view_logout.getId()) {
                    case R.id.form_logout_button:
                        //chiudo sessione e metto valore sharedPref a false
                        ParseUser.logOut();
                        SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                        userInformation.edit().putString("username","").commit();
                        userInformation.edit().putString("name","").commit();
                        userInformation.edit().putString("lastname","").commit();
                        userInformation.edit().putString("email","").commit();
                        userInformation.edit().putString("date","").commit();
                        userInformation.edit().putBoolean("isLogged",false).commit();
                        System.out.println("debug: logout eseguito");
                        Intent form_intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(form_intent);
                        finish();
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            System.out.println("debug: immagine catturata, conversione in bitmap");
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //TODO RobyMac --> problema con la compressione delle immagini... bisogna risolvere
            System.out.println("debug: compressione immagine");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteImg = stream.toByteArray();

            System.out.println("debug: creazione di un ParseFile");
            ParseFile file = new ParseFile("immagine.JPEG", byteImg);
            file.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null)  {
                        System.out.println("debug: file inviato correttamente");
                    }else {
                        //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                        new ExceptionCheck().check(e.getCode(),vi,e.getMessage());
                    }
                }
            });
            SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
            ParseObject immagine = new ParseObject("Photo");
            immagine.put("user", userInformation.getString("username","clothapp").toString());
            immagine.put("photo", file);

            immagine.saveInBackground(new SaveCallback() {
                public void done(ParseException e) {
                    if (e == null)  {
                        System.out.println("debug: immagine inviata correttamente");
                    }else {
                        //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                        new ExceptionCheck().check(e.getCode(),vi,e.getMessage());
                    }
                }
            });
        }
    }
}
