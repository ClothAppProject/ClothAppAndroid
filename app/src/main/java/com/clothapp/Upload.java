package com.clothapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import static android.support.v4.graphics.BitmapCompat.*;
import static com.clothapp.resources.ExceptionCheck.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by giacomoceribelli on 29/12/15.
 */
public class Upload extends AppCompatActivity {

    // static fields
    /* --------------------------------------- */
    final static long mb5 = (long) (5 * 10e6);
    final static long mb3 = (long) (3 * 10e6);
    final static long mb1 = (long) (1 * 10e6);
    final static int CAPTURE_IMAGE_ACTIVITY = 2187;
    //qui si apre un piccolo excursus: perchè CAPRUTE_IMAGE_ACTIVITY è settato a 2187 ? FN2187 è il numero di serie dell'assolatore
    // del personaggio di Finn nell'ultimo Star Wars episodio VII prima di diventare un dei "buoni"
    //inoltre 2187 corrisponde anche al numero di cella dove è stata rinchiusa la pricipessa Leia dopo essere stata catturata
    //da Dart Vather in una delle prima scene di Star Wars episodio IV

    /* --------------------------------------- */
    boolean first=true;
    final String directoryName = "ClothApp";
    String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'").format(new Date());
    Uri takenPhotoUri;
    ImageView imageView = null;
    Bitmap imageBitmap = null;
    /* --------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        System.out.println("debug: inizizializzazione activity upload");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        imageView = (ImageView) findViewById(R.id.view_immagine);

        //controllo se ci sono savedIstance: se ce ne sono vuol dire che questa non activity era già stata creata e stoppata a causa
        //dell'apertura della fotocamera
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first");
            photoFileName = savedInstanceState.getString("photoFileName");
            System.out.println("debug: first è false quindi non avvia la fotocamera");
            //inizializzo parse perchè l'activity è stata chiusa
        }
        if (first)   {
            //non faccio direttamente il controllo su savedIstance perchè magari in futuro potremmo passare altri parametri
            //questa è la prima volta che questa activity viene aperta, quindi richiamo direttamente la fotocamera
            System.out.println("debug: è il first");
            // creo un intent specificando che voglio un'immagine full size e il nome dell'uri dell'immagine
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //TODO check if getPhotoFileUri returns null
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

            // If fintanto che il resolveActivity di quell'intent non è null significa che la foto non è ancora stata scattata e
            //quindi devo chiamare la fotocamera
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Starto l'attivity di cattura della foto passandogli l'intent
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY);
            }
        }
        final TextView percentuale = (TextView) findViewById(R.id.percentuale);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.barraProgresso);
        final Button button = (Button) findViewById(R.id.send);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //listener sul bottone
                final View vi = v;
                button.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                percentuale.setVisibility(View.VISIBLE);

                System.out.println("debug: compressione immagine");
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // e la funzione a fine file checkToCompress()

                int toCompress = checkToCompress(imageBitmap);
                System.out.println("debug: toCompress = " + toCompress);

                imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
                byte[] byteImg = stream.toByteArray();
                System.out.println("debug: dimensione del file è "+getAllocationByteCount(imageBitmap));

                //creazione di un ParseFile
                System.out.println("debug: creazione di un ParseFile");
                ParseFile file = new ParseFile(photoFileName, byteImg);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            System.out.println("debug: file inviato correttamente");
                        } else {
                            //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());
                            System.out.println("debug: errore nell'invio del file");
                        }
                    }
                }, new ProgressCallback() {
                        public void done (Integer percentDone){
                            // Update your progress spinner here. percentDone will be between 0 and 100.
                            percentuale.setText("Caricamento: "+percentDone+"%");
                            progressBar.setProgress(percentDone);
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
                            System.out.println("debug: Oggetto immagine inviata correttamente");
                            // redirecting the user to the homepage activity
                            Intent i = new Intent(getApplicationContext(), Homepage.class);
                            startActivity(i);
                            finish();
                        } else {
                            //chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                            check(e.getCode(), vi, e.getMessage());
                            System.out.println("debug: errore nell'invio dell'oggetto immagine");
                        }
                    }
                });

            }
        });
    }

    //questa funzione serve a prendere la foto dopo che è stata scattata dalla fotocamera, e mette l'immagine nella ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("debug: siamo arrivati alla onActivityResult");
        //controllo che l'immagine sia stata catturata correttamente
        if (requestCode == CAPTURE_IMAGE_ACTIVITY && resultCode == RESULT_OK) {
            takenPhotoUri = getPhotoFileUri(photoFileName);
            // a questo punto l'immagine è stata salvata sullo storage
            imageBitmap = BitmapFactory.decodeFile(takenPhotoUri.getPath());

            //inserisco l'immagine nel bitmap
            //prima però controllo in che modo è stata scattata (rotazione)
            try {
                imageBitmap = rotateImageIfRequired(imageBitmap,takenPhotoUri);
                imageView.setImageBitmap(imageBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { // Errore della fotocamera
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            System.out.println("debug: immagine non è stata scattata");
            // reinderizzo l'utente alla homePage activity
            Intent i = new Intent(getApplicationContext(), Homepage.class);
            startActivity(i);
            finish();
        }
    }

    //questa funzione serve nel caso in cui dopo aver chiamato la fotocamera, l'attività upload si chiude
    //quindi ci dobbiamo salvare la variabile first, altrimenti quando l'attività Upload viene riaperta
    //rilancia di nuovo l'attività fotocamera e quindi è un ciclo continuo
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // salva la variabile first=false nell'istanza, che non viene eliminata quando l'attività upload viene chiusa
        savedInstanceState.putBoolean("first",false);
        // inoltre salvo anche il nome del file, perchè tra un'activity e l'altra potrebbero passare millisecondi
        savedInstanceState.putString("photoFileName",photoFileName);
        System.out.println("debug: first è messo a false, l'attività upload è stata sospesa");
        super.onSaveInstanceState(savedInstanceState);
    }

    //in caso sia premuto il pulsante indietro, eliminiamo l'immagine creata e torniamo alla home activity
    @Override
    public void onBackPressed() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+directoryName;
        File f = new File(path,photoFileName);
        //controllo se esiste
        if(f.exists() && !f.isDirectory()) {
            // se esiste lo elimino
            f.delete();
            System.out.println("debug: file eliminato");
        }
        // reinderizzo l'utente alla homePage activity
        Intent i = new Intent(getApplicationContext(), Homepage.class);
        startActivity(i);
        finish();
    }

    // Ritorna l'Uri dell'immagine su disco
    public Uri getPhotoFileUri(String fileName) {
        // Continua solamente se la memoria SD è montata
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);
            // Creo la directory di storage se non esiste
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                System.out.println("debug: impossibile creare cartella");
            }
            // Ritorna l'uri alla foto in base al fileName
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    //funzione per controllare che lo storage esterno sia disponibile
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    //  ritorna il parametro da passare il metodo compression
    //  prende la foto da comprimere
    private int checkToCompress(Bitmap photo){
        //TODO getAllocationByteCount non riporta il peso della foto preciso, dice che pesa 64mb quando invece pesa 4,5mb
        System.out.println("photo to compress is: "+getAllocationByteCount(photo));
        if(getAllocationByteCount(photo) > mb5) return 70;
        else if(getAllocationByteCount(photo)> mb3) return 80;
        else if(getAllocationByteCount(photo) > mb1) return 90;
        else return 100;
    }

    //funzione che controlla se ruotare l'immagine o no
    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException {
        //prendo i dati exif della foto (comprendono data, orientamento, geolocalizzazione della foto ecc...)
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

    //funzione che ruota l'immagine
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}
