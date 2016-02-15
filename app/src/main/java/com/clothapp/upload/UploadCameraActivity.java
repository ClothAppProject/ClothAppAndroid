package com.clothapp.upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.clothapp.R;
import com.clothapp.home_gallery.HomeActivity;
import com.clothapp.http.Get;
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
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by giacomoceribelli on 29/12/15.
 */
public class UploadCameraActivity extends AppCompatActivity {

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

        setContentView(R.layout.activity_upload);

        // my_child_toolbar is defined in the layout file
        Toolbar myChildToolbar =(Toolbar) findViewById(R.id.my_home_toolbar);
        setSupportActionBar(myChildToolbar);

        // Get a support ActionBar corresponding to this toolbar
        final ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.upload);

        Log.d("UploadCameraActivity", "Inizializzazione UploadCameraActivity");

        // Controllo se ci sono savedIstance: se ce ne sono vuol dire che questa non activity era già stata creata e stoppata a causa
        // dell'apertura della fotocamera
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first");
            photoFileName = savedInstanceState.getString("photoFileName");

            Log.d("UploadCameraActivity", "First è false, quindi non avvia la fotocamera");
            // Inizializzo parse perchè l'activity è stata chiusa
        }
        if (first) {
            // Non faccio direttamente il controllo su savedIstance perchè magari in futuro potremmo passare altri parametri
            // questa è la prima volta che questa activity viene aperta, quindi richiamo direttamente la fotocamera
            Log.d("UploadCameraActivity", "E' il first");

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
        imageView = (ImageView) findViewById(R.id.view_immagine);
        ImageView add=(ImageView)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lista);
                EditText vestito=new EditText(UploadCameraActivity.this);
                vestito.setHint(R.string.cloth);
                EditText tipo = (EditText) findViewById(R.id.tipo);
                vestito.setWidth(tipo.getWidth());
                vestito.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(vestito);
            }
        });
        final EditText hash = (EditText) findViewById(R.id.hashtag);

        // Add an OnClick listener to the send button
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View vi = v;

                btnSend.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                percentuale.setVisibility(View.VISIBLE);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // e la funzione a fine file checkToCompress()

                int toCompress = BitmapUtil.checkToCompress(imageBitmap);
                Log.d("UploadCameraActivity", "toCompress = " + toCompress);

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
                byte[] byteImg = stream.toByteArray();
                Log.d("UploadCameraActivity", "Dimensione del file: " + getAllocationByteCount(imageBitmap));

                // Creazione di un ParseFile
                ParseFile file = new ParseFile(photoFileName, byteImg);

                // Save the file to Parse
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("UploadCameraActivity", "File inviato correttamente");
                        } else {
                            // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());
                            Log.d("UploadCameraActivity", "Errore durante l'invio del file");
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
                final ParseObject picture = new ParseObject("Photo");
                picture.put("user", ParseUser.getCurrentUser().getUsername());
                picture.put("photo", file);
                String[] hashtags = hash.getText().toString().split(" ");
                picture.put("hashtag",Arrays.asList(hashtags));
                picture.put("nLike",0);

                // Invio ParseObject (immagine) al server
                picture.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("UploadCameraActivity", "Oggetto immagine inviato correttamente");

                            //chiamata get per salvare il thumbnail
                            String url = "http://clothapp.parseapp.com/createthumbnail/"+picture.getObjectId();
                            Get g = new Get();
                            g.execute(url);

                            // Redirecting the user to the homepage activity
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(i);

                            finish();
                        } else {
                            // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());

                            Log.d("UploadCameraActivity", "Errore durante l'invio dell'oggetto immagine");
                        }
                    }
                });

            }
        });
    }

    // Questa funzione serve a prendere la foto dopo che è stata scattata dalla fotocamera, e mette l'immagine nella ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("UploadCameraActivity", "Siamo arrivati alla onActivityResult");

        // Controllo che l'immagine sia stata catturata correttamente
        if (requestCode == CAPTURE_IMAGE_ACTIVITY && resultCode == RESULT_OK) {
            takenPhotoUri = getPhotoFileUri(photoFileName);

            // A questo punto l'immagine è stata salvata sullo storage
            imageBitmap = BitmapFactory.decodeFile(takenPhotoUri.getPath());


            // Inserisco l'immagine nel bitmap
            // Prima però controllo in che modo è stata scattata (rotazione)
            imageBitmap = BitmapUtil.rotateImageIfRequired(imageBitmap,takenPhotoUri);
            imageView.setImageBitmap(BitmapUtil.scala(imageBitmap));

        } else {
            // Errore della fotocamera
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

            Log.d("UploadCameraActivity", "L'Immagine non è stata scattata");

            // Reinderizzo l'utente alla homePage activity
            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(i);

            finish();
        }
    }

    // Questa funzione serve nel caso in cui dopo aver chiamato la fotocamera, l'attività upload si chiude
    // quindi ci dobbiamo salvare la variabile first, altrimenti quando l'attività UploadCameraActivity viene riaperta
    // rilancia di nuovo l'attività fotocamera e quindi è un ciclo continuo
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Salva la variabile first=false nell'istanza, che non viene eliminata quando l'attività upload viene chiusa
        savedInstanceState.putBoolean("first", false);

        // Inoltre salvo anche il nome del file, perchè tra un'activity e l'altra potrebbero passare millisecondi
        savedInstanceState.putString("photoFileName", photoFileName);

        Log.d("UploadCameraActivity", "First è stato messo a false");

        super.onSaveInstanceState(savedInstanceState);
    }

    // In caso sia premuto il pulsante indietro, eliminiamo l'immagine creata e torniamo alla home activity
    @Override
    public void onBackPressed() {
        deleteImage();
        // Reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(i);
        finish();
    }

    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                deleteImage();
                // Reinderizzo l'utente alla homePage activity
                onBackPressed();
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

            Log.d("UploadCameraActivity", "File eliminato");
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
                Log.d("UploadCameraActivity", "Impossibile creare cartella");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        getMenuInflater().inflate(R.menu.base_app_bar, menu);
        return true;
    }
}
