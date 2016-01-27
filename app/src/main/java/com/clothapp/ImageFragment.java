package com.clothapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.clothapp.resources.BitmapUtil;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 23/01/16.
 */
public class ImageFragment extends FragmentActivity {

    ImageView imageView;
    Bitmap imageBitmap;
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_image);
        final View v = new View(this.getApplicationContext());

        //prendo id della foto
        String objectID = getIntent().getExtras().getString("objectID");
        imageView = (ImageView) findViewById(R.id.image_view_fragment);
        final TextView username = (TextView) findViewById(R.id.username_photo);

        //eseguo query e inserisco tutto nei relativi campi
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("objectId", objectID);
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    final String nome = objects.get(0).getString("user");
                    username.setText(nome);
                    //listener sul nome dell'utente
                    username.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            i.putExtra("user", nome);
                            startActivity(i);
                            imageBitmap=null;
                            finish();
                        }
                    });

                    //inserisco immagine e info
                    ParseFile f = objects.get(0).getParseFile("photo");
                    f.getFileInBackground(new GetFileCallback() {
                        @Override
                        public void done(File data, ParseException e) {
                            //imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imageBitmap = BitmapFactory.decodeFile(data.getAbsolutePath());
                            imageView.setImageBitmap(BitmapUtil.scala(imageBitmap));
                        }
                    },
                    new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            //mostrare caricamento immagine
                        }
                    });
                } else {
                    //errore nel reperire gli oggetti Photo dal database
                    check(e.getCode(), v, e.getMessage());
                }
            }
        });
    }
}
