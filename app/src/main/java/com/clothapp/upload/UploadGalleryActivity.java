package com.clothapp.upload;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clothapp.HomepageActivity;
import com.clothapp.R;
import com.clothapp.resources.BitmapUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v4.graphics.BitmapCompat.getAllocationByteCount;
import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 25/01/16.
 */
public class UploadGalleryActivity extends AppCompatActivity {

    final static int RESULT_LOAD_IMG = 2187;
    String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'").format(new Date());
    Uri takenPhotoUri;
    ImageView imageView = null;
    Bitmap imageBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //inizializzo immagine da prendere in galleria
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);

        final TextView percentuale = (TextView) findViewById(R.id.percentuale);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.barraProgresso);
        final Button btnSend = (Button) findViewById(R.id.send);

        // Add an OnClick listener to the send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View vi = v;

                btnSend.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                percentuale.setVisibility(View.VISIBLE);

                System.out.println("debug: compressione immagine");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // e la funzione a fine file checkToCompress()

                int toCompress = BitmapUtil.checkToCompress(imageBitmap);
                Log.d("UploadCameraActivity", "toCompress = " + toCompress);

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
                byte[] byteImg = stream.toByteArray();
                Log.d("UploadCameraActivity", "Dimensione del file: " + getAllocationByteCount(imageBitmap));

                // Creazione di un ParseFile
                Log.d("UploadCameraActivity", "Creazione di un ParseFile");
                ParseFile file = new ParseFile(photoFileName, byteImg);

                // Save the file to Parse
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("UploadGalleryActivity", "File inviato correttamente");
                        } else {
                            // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());
                            Log.d("UploadGalleryActivity", "Errore durante l'invio del file");
                        }
                    }
                }, new ProgressCallback() {
                    public void done(Integer percentDone) {
                        // Update your progress spinner here. percentDone will be between 0 and 100.
                        percentuale.setText("Caricamento: " + percentDone + "%");
                        progressBar.setProgress(percentDone);
                    }
                });

                // Creazione di un ParseObject da inviare
                ParseObject picture = new ParseObject("Photo");
                picture.put("user", ParseUser.getCurrentUser().getUsername());
                picture.put("photo", file);

                // Invio ParseObject (immagine) al server
                picture.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("UploadGalleryActivity", "Oggetto immagine inviato correttamente");

                            // Redirecting the user to the homepage activity
                            Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                            startActivity(i);

                            finish();
                        } else {
                            // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());

                            Log.d("UploadGalleryActivity", "Errore durante l'invio dell'oggetto immagine");
                        }
                    }
                });

            }
        });
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

                imageView = (ImageView) findViewById(R.id.view_immagine);
                imageBitmap = BitmapFactory.decodeFile(picturePath);

                imageBitmap = BitmapUtil.rotateGalleryImage(picturePath,imageBitmap);
                imageView.setImageBitmap(BitmapUtil.scala(imageBitmap));

            } else {
                // L'utente non ha scelto nessuna immagine lo rimando indietro
                onBackPressed();
            }

    }
    @Override
    public void onBackPressed() {
        // Reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
        startActivity(i);
        finish();
    }
    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                // Reinderizzo l'utente alla homePage activity
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
