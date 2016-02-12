package com.clothapp.profilepicture;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import static com.clothapp.resources.ExceptionCheck.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by giacomoceribelli on 29/12/15.
 */
public class ProfileCameraActivity extends AppCompatActivity {
    final static int CAPTURE_IMAGE_ACTIVITY = 2187;
    /* --------------------------------------- */
    boolean first = true;
    final String directoryName = "ClothApp";
    String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'").format(new Date());
    Uri takenPhotoUri;
    Bitmap imageBitmap = null;
    View vi;
    /* --------------------------------------- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        vi = new View(this);

        // Controllo se ci sono savedIstance: se ce ne sono vuol dire che questa non activity era già stata creata e stoppata a causa
        // dell'apertura della fotocamera
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first");
            photoFileName = savedInstanceState.getString("photoFileName");

            Log.d("ProfileCameraActivity", "First è false, quindi non avvia la fotocamera");
            // Inizializzo parse perchè l'activity è stata chiusa
        }
        if (first) {
            // Non faccio direttamente il controllo su savedIstance perchè magari in futuro potremmo passare altri parametri
            // questa è la prima volta che questa activity viene aperta, quindi richiamo direttamente la fotocamera
            Log.d("ProfileCameraActivity", "E' il first");

            // Creo un intent specificando che voglio un'immagine full size e il nome dell'uri dell'immagine
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Set the image file name
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

            // If fintanto che il resolveActivity di quell'intent non è null significa che la foto non è ancora stata scattata e
            // quindi devo chiamare la fotocamera
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Starto l'attivity di cattura della foto passandogli l'intent
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY);
            }
        }
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
            imageBitmap = BitmapUtil.rotateImageIfRequired(imageBitmap, takenPhotoUri);
            upload();
        } else {
            // Errore della fotocamera
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

            Log.d("UploadCameraActivity", "L'Immagine non è stata scattata");

            // Reinderizzo l'utente alla profile activity
            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
            i.putExtra("user",ParseUser.getCurrentUser().getUsername());
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

        Log.d("ProfileCameraActivity", "First è stato messo a false");

        super.onSaveInstanceState(savedInstanceState);
    }

    //funzione che cancella l'imamgine scattata
    public void deleteImage()   {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName;
        File f = new File(path, photoFileName);

        // Controllo se esiste
        if (f.exists() && !f.isDirectory()) {
            // Se esiste lo elimino
            f.delete();

            Log.d("ProfileCameraActivity", "File eliminato");
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
                Log.d("ProfileCameraActivity", "Impossibile creare cartella");
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

    public void upload()    {
        //inizializzo barra di caricamento
        final ProgressDialog dialog = ProgressDialog.show(ProfileCameraActivity.this, "",
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
                    Log.d("ProfileCameraActivity", "File inviato correttamente");
                } else {
                    // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                    check(e.getCode(), vi, e.getMessage());
                    Log.d("ProfileCameraActivity", "Errore durante l'invio del file");
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
                    Log.d("ProfileCameraActivity", "Oggetto immagine inviato correttamente");
                    deleteImage();


                    //chiamata get per salvare il thumbnail
                    String url = "http://clothapp.parseapp.com/createprofilethumbnail/"+picture.getObjectId();
                    Get g = new Get();
                    g.execute(url);

                    // Redirecting the user to the profile activity
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("user",ParseUser.getCurrentUser().getUsername().toString());
                    startActivity(i);

                    finish();
                } else {
                    // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                    check(e.getCode(), vi, e.getMessage());

                    Log.d("ProfileCameraActivity", "Errore durante l'invio dell'oggetto immagine");
                }
            }
        });
    }
}
