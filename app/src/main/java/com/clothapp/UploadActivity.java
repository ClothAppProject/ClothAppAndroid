package com.clothapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clothapp.resources.BitmapUtil;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import static android.support.v4.graphics.BitmapCompat.*;
import static com.clothapp.resources.ExceptionCheck.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by giacomoceribelli on 29/12/15.
 */
public class UploadActivity extends AppCompatActivity {

    // static fields
    /* --------------------------------------- */
    final static long mb5 = (long) (5 * 10e6);
    final static long mb3 = (long) (3 * 10e6);
    final static long mb1 = (long) (1 * 10e6);
    final static int CAPTURE_IMAGE_ACTIVITY = 2187;

    // ATTENZIONE Roberto! Possibili spoiler su Star Wars VII

    // Qui si apre un piccolo excursus: perchè CAPRUTE_IMAGE_ACTIVITY è settato a 2187 ? FN2187 è il numero di serie dell'assolatore
    // del personaggio di Finn nell'ultimo Star Wars episodio VII prima di diventare un dei "buoni"
    // inoltre 2187 corrisponde anche al numero di cella dove è stata rinchiusa la pricipessa Leia dopo essere stata catturata
    // da Dart Vather in una delle prima scene di Star Wars episodio IV

    /* --------------------------------------- */
    boolean first = true;
    final String directoryName = "ClothApp";
    String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'").format(new Date());
    Uri takenPhotoUri;
    ImageView imageView = null;
    Bitmap imageBitmap = null;
    /* --------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //inizializzo layout e tasto indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_upload);

        Log.d("UploadActivity", "Inizializzazione UploadActivity activity");

        imageView = (ImageView) findViewById(R.id.view_immagine);

        // Controllo se ci sono savedIstance: se ce ne sono vuol dire che questa non activity era già stata creata e stoppata a causa
        // dell'apertura della fotocamera
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first");
            photoFileName = savedInstanceState.getString("photoFileName");

            Log.d("UploadActivity", "First è false, quindi non avvia la fotocamera");
            // Inizializzo parse perchè l'activity è stata chiusa
        }
        if (first) {
            // Non faccio direttamente il controllo su savedIstance perchè magari in futuro potremmo passare altri parametri
            // questa è la prima volta che questa activity viene aperta, quindi richiamo direttamente la fotocamera
            Log.d("UploadActivity", "E' il first");

            // Creo un intent specificando che voglio un'immagine full size e il nome dell'uri dell'immagine
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // TODO: Check if getPhotoFileUri returns null

            // Set the image file name
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

            // If fintanto che il resolveActivity di quell'intent non è null significa che la foto non è ancora stata scattata e
            // quindi devo chiamare la fotocamera
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Starto l'attivity di cattura della foto passandogli l'intent
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY);
            }
        }

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

                int toCompress = checkToCompress(imageBitmap);
                Log.d("UploadActivity", "toCompress = " + toCompress);

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
                byte[] byteImg = stream.toByteArray();
                Log.d("UploadActivity", "Dimensione del file: " + getAllocationByteCount(imageBitmap));

                // Creazione di un ParseFile
                Log.d("UploadActivity", "Creazione di un ParseFile");
                ParseFile file = new ParseFile(photoFileName, byteImg);

                // Save the file to Parse
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("UploadActivity", "File inviato correttamente");
                        } else {
                            // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());
                            Log.d("UploadActivity", "Errore durante l'invio del file");
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
                            Log.d("UploadActivity", "Oggetto immagine inviato correttamente");

                            // Redirecting the user to the homepage activity
                            Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                            startActivity(i);

                            finish();
                        } else {
                            // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());

                            Log.d("UploadActivity", "Errore durante l'invio dell'oggetto immagine");
                        }
                    }
                });

            }
        });
    }

    // Questa funzione serve a prendere la foto dopo che è stata scattata dalla fotocamera, e mette l'immagine nella ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("UploadActivity", "Siamo arrivati alla onActivityResult");

        // Controllo che l'immagine sia stata catturata correttamente
        if (requestCode == CAPTURE_IMAGE_ACTIVITY && resultCode == RESULT_OK) {
            takenPhotoUri = getPhotoFileUri(photoFileName);

            // A questo punto l'immagine è stata salvata sullo storage
            imageBitmap = BitmapFactory.decodeFile(takenPhotoUri.getPath());

            // Inserisco l'immagine nel bitmap
            // Prima però controllo in che modo è stata scattata (rotazione)
            try {
                imageBitmap = rotateImageIfRequired(imageBitmap, takenPhotoUri);
                if (imageBitmap.getHeight() > GL10.GL_MAX_TEXTURE_SIZE || imageBitmap.getWidth()>GL10.GL_MAX_TEXTURE_SIZE) {

                    imageView.setImageBitmap(BitmapUtil.scala(imageBitmap));
                }
                else imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Errore della fotocamera
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

            Log.d("Uplaod", "L'Immagine non è stata scattata");

            // Reinderizzo l'utente alla homePage activity
            Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
            startActivity(i);

            finish();
        }
    }

    // Questa funzione serve nel caso in cui dopo aver chiamato la fotocamera, l'attività upload si chiude
    // quindi ci dobbiamo salvare la variabile first, altrimenti quando l'attività UploadActivity viene riaperta
    // rilancia di nuovo l'attività fotocamera e quindi è un ciclo continuo
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Salva la variabile first=false nell'istanza, che non viene eliminata quando l'attività upload viene chiusa
        savedInstanceState.putBoolean("first", false);

        // Inoltre salvo anche il nome del file, perchè tra un'activity e l'altra potrebbero passare millisecondi
        savedInstanceState.putString("photoFileName", photoFileName);

        Log.d("UploadActivity", "First è stato messo a false");

        super.onSaveInstanceState(savedInstanceState);
    }

    // In caso sia premuto il pulsante indietro, eliminiamo l'immagine creata e torniamo alla home activity
    @Override
    public void onBackPressed() {
        deleteImage();
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
                deleteImage();
                // Reinderizzo l'utente alla homePage activity
                Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
                startActivity(i);

                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //funzione che cancella l'imamgine scattata
    public void deleteImage()   {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName;
        File f = new File(path, photoFileName);

        // Controllo se esiste
        if (f.exists() && !f.isDirectory()) {
            // Se esiste lo elimino
            f.delete();

            Log.d("UploadActivity", "File eliminato");
        }
    }
    // Ritorna l'Uri dell'immagine su disco
    public Uri getPhotoFileUri(String fileName) {
        // Continua solamente se la memoria SD è montata
        if (isExternalStorageAvailable()) {

            // Get safe storage directory for photos
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);

            // Creo la directory di storage se non esiste
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("UploadActivity", "Impossibile creare cartella");
            }

            // Ritorna l'uri alla foto in base al fileName
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Funzione per controllare che lo storage esterno sia disponibile
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        return state.equals(Environment.MEDIA_MOUNTED);
    }

    //  Ritorna il parametro da passare il metodo compression
    //  prende la foto da comprimere
    private int checkToCompress(Bitmap photo) {
        // TODO: getAllocationByteCount non riporta il peso della foto preciso, dice che pesa 64mb quando invece pesa 4,5mb
        Log.d("UploadActivity", "photo to compress is: " + getAllocationByteCount(photo));

        if (getAllocationByteCount(photo) > mb5) return 70;
        else if (getAllocationByteCount(photo) > mb3) return 80;
        else if (getAllocationByteCount(photo) > mb1) return 90;
        else return 100;
    }

    // Funzione che controlla se ruotare l'immagine o no
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        // Prendo i dati exif della foto (comprendono data, orientamento, geolocalizzazione della foto ecc...)
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    // Funzione che ruota l'immagine
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);

        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();

        return rotatedImg;
    }
}
