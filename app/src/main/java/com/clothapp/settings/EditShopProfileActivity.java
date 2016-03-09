package com.clothapp.settings;

/**
 * Created by giacomoceribelli on 08/03/16.
 */

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.CircleTransform;
import com.clothapp.upload.UploadProfilePictureActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.RegisterUtil.isValidEmailAddress;



/**
 * Created by giacomoceribelli on 09/01/16.
 */
public class EditShopProfileActivity extends AppCompatActivity {
    private ParseObject negozio;
    private ImageView profile_picture;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inizializzo layout e tasto indietro
        final ParseUser utente = ParseUser.getCurrentUser();
        getSupportActionBar().setTitle(utente.getUsername());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_shop_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //show user info
        final EditText editEmail = (EditText) findViewById(R.id.email);
        editEmail.setText(utente.getEmail());

        final EditText editName = (EditText) findViewById(R.id.name);
        editName.setText(utente.getString("name"));

        final EditText editAddress = (EditText) findViewById(R.id.address);
        final EditText editWebsite = (EditText) findViewById(R.id.website);
        ParseQuery<ParseObject> queryInfo = ParseQuery.getQuery("LocalShop");
        queryInfo.whereEqualTo("username", utente.getUsername());
        queryInfo.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    negozio = object;
                    editAddress.setText(object.getString("address"));
                    editWebsite.setText(object.getString("webSite"));
                }
            }
        });

        //show profile picutre
        profile_picture = (ImageView) findViewById(R.id.profile_picture);
        ProfileUtils.loadProfilePicture(profile_picture,getApplicationContext());
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditShopProfileActivity.this);
                builder.setTitle(R.string.choose_profile_picture)
                        .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getApplicationContext(), UploadProfilePictureActivity.class);
                                switch (which) {
                                    case 0:
                                        // Redirect the user to the ProfilePictureActivity with camera
                                        i.putExtra("photoType", 2187);
                                        startActivity(i);
                                        break;
                                    case 1:
                                        // Redirect the user to the ProfilePictureActivity with galery
                                        i.putExtra("photoType", 1540);
                                        startActivity(i);
                                        break;
                                    case 2:
                                        //delete profile picture
                                        ParseQuery<ParseObject> queryFotoProfilo = new ParseQuery<ParseObject>("UserPhoto");
                                        queryFotoProfilo.whereEqualTo("username", utente.getUsername());
                                        queryFotoProfilo.findInBackground(new FindCallback<ParseObject>() {
                                            @Override
                                            public void done(List<ParseObject> objects, ParseException e) {
                                                if (e == null) {
                                                    if (objects.size() > 0) {
                                                        objects.get(0).deleteInBackground();
                                                        recreate();
                                                    }
                                                }
                                            }
                                        });
                                        break;
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        final Button edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!isValidEmailAddress(editEmail.getText().toString())) {
                    Snackbar.make(v, R.string.invalid_email, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else if (editAddress.getText().toString()==""&&editWebsite.getText().toString()==""){
                    Snackbar.make(v, R.string.empty_field, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    //inizializzo progressbar di caricamento
                    final ProgressDialog dialog = ProgressDialog.show(EditShopProfileActivity.this, "",
                            getString(R.string.check_data), true);

                    utente.setEmail(editEmail.getText().toString());
                    utente.put("name",editName.getText().toString());
                    try {
                        utente.save();
                        negozio.put("address",editAddress.getText().toString());
                        negozio.put("webSite",editWebsite.getText().toString());
                        negozio.save();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),R.string.data_edited,Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        ProfileUtils.loadProfilePicture(profile_picture,getApplicationContext());
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
