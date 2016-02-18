package com.clothapp.profile.utils;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import com.clothapp.profile.ProfileActivity;
import com.clothapp.R;
import com.clothapp.http.Get;
import com.clothapp.resources.BitmapUtil;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 26/01/16.
 */
public class ProfilePictureGalleryActivity extends AppCompatActivity {
    final static int RESULT_LOAD_IMG = 2187;
    String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'").format(new Date());
    Uri takenPhotoUri;
    Bitmap imageBitmap = null;
    View vi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vi = new View(this);

        //inizializzo immagine da prendere in galleria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

    }

    //immagine inserita all'interno dell'image view e salvata in imageview al ritorno della galleria
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // When an Image is picked
        if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            takenPhotoUri = data.getData();

            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(takenPhotoUri, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageBitmap = BitmapFactory.decodeFile(picturePath);

            imageBitmap = BitmapUtil.rotateGalleryImage(picturePath,imageBitmap);
            upload();
        } else {
            // L'utente non ha scelto nessuna immagine lo rimando indietro
            onBackPressed();
        }

    }

    public void upload()    {
        //inizializzo barra di caricamento
        final ProgressDialog dialog = ProgressDialog.show(ProfilePictureGalleryActivity.this, "",
                getResources().getString(R.string.setting_pp), true);

        //controllo se c'è un'altra immagine del profilo online per lo stesso utente e la elimino
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
        queryFoto.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size()>0) {
                        objects.get(0).deleteInBackground();
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // e la funzione a fine file checkToCompress()

        int toCompress = BitmapUtil.checkToCompress(imageBitmap);

        imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
        byte[] byteImg = stream.toByteArray();

        // Creazione di un ParseFile
        ParseFile file = new ParseFile(photoFileName, byteImg);

        // Save the file to Parse
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ProfilePictureGalleryActivity", "File inviato correttamente");
                } else {
                    // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                    check(e.getCode(), vi, e.getMessage());
                    Log.d("ProfilePictureGalleryActivity", "Errore durante l'invio del file");
                }
            }
        });

        // Creazione di un ParseObject da inviare
        final ParseObject picture = new ParseObject("UserPhoto");
        picture.put("username", ParseUser.getCurrentUser().getUsername());
        picture.put("profilePhoto", file);

        // Invio ParseObject (immagine) al server
        picture.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    dialog.dismiss();

                    //chiamata get per salvare il thumbnail
                    String url = "http://clothapp.parseapp.com/createprofilethumbnail/"+picture.getObjectId();
                    Get g = new Get();
                    g.execute(url);

                    Log.d("ProfilePictureGalleryActivity", "Oggetto immagine inviato correttamente");
                    // Redirecting the user to the profile activity
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("user",ParseUser.getCurrentUser().getUsername().toString());
                    startActivity(i);

                    finish();
                } else {
                    // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                    check(e.getCode(), vi, e.getMessage());

                    Log.d("ProfilePictureGalleryActivity", "Errore durante l'invio dell'oggetto immagine");
                }
            }
        });
    }
}
