package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

/**
 * Created by giacomoceribelli on 29/12/15.
 */
public class Upload extends AppCompatActivity {
    boolean first = true;
    ImageView imageView = null;
    Bitmap imageBitmap = null;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("debug: inizizializzazione activity upload");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = (ImageView) findViewById(R.id.view_immagine);

        //invocazione della fotocamera
        if (first) {  //altrimenti l'activity si ripete
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
            first = false;
        }



        Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                final View vi = v;

                //TODO RobyMac --> problema con la compressione delle immagini... bisogna risolvere
                System.out.println("debug: compressione immagine");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteImg = stream.toByteArray();

                //creazione di un ParseFile
                System.out.println("debug: creazione di un ParseFile");
                ParseFile file = new ParseFile("immagine.JPEG", byteImg);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            System.out.println("debug: file inviato correttamente");
                        } else {
                            //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            new ExceptionCheck().check(e.getCode(), vi, e.getMessage());
                        }
                    }
                });

                //Creazione di un ParseObject da inviare
                SharedPreferences userInformation = getSharedPreferences(getString(R.string.info), MODE_PRIVATE);
                ParseObject picture = new ParseObject("Photo");
                picture.put("user", userInformation.getString("username", "clothapp").toString());
                picture.put("photo", file);

                //invio ParseObject (immagine) al server
                picture.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            System.out.println("debug: immagine inviata correttamente");
                        } else {
                            //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            new ExceptionCheck().check(e.getCode(), vi, e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //qui ottengo l'immagine e la inserisco nell'imageView
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
    }
}
