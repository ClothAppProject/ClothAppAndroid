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
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Image;
import com.clothapp.upload.UploadProfilePictureActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;
import static com.clothapp.resources.RegisterUtil.isValidEmailAddress;



/**
 * Created by giacomoceribelli on 09/01/16.
 */
public class EditProfileActivity extends AppCompatActivity {
    private ParseObject persona;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //inizializzo layout e tasto indietro
        final ParseUser utente = ParseUser.getCurrentUser();
        getSupportActionBar().setTitle(utente.getUsername());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //show user info
        final EditText editEmail = (EditText) findViewById(R.id.email);
        editEmail.setText(utente.getEmail());

        final EditText editName = (EditText) findViewById(R.id.name);
        editName.setText(utente.getString("name"));

        final EditText editLastname = (EditText) findViewById(R.id.lastname);
        final EditText editDay = (EditText) findViewById(R.id.edit_day);
        final EditText editMonth = (EditText) findViewById(R.id.edit_month);
        final EditText editYear = (EditText) findViewById(R.id.edit_year);
        ParseQuery<ParseObject> queryInfo = ParseQuery.getQuery("Persona");
        queryInfo.whereEqualTo("username", utente.getUsername());
        queryInfo.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    persona = object;
                    editLastname.setText(object.getString("lastname"));
                    Date birthday = object.getDate("date");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(birthday);
                    editDay.setText(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
                    editMonth.setText(Integer.toString(cal.get(Calendar.MONTH)+1));
                    editYear.setText(Integer.toString(cal.get(Calendar.YEAR)));
                }
            }
        });

        //show profile picutre
        final ImageView profile_picture = (ImageView) findViewById(R.id.profile_picture);
        ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
        query.whereEqualTo("username", utente.getUsername());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject photo, ParseException e) {
                if (e == null) {
                    ParseFile parseFile = photo.getParseFile("profilePhoto");
                    parseFile.getFileInBackground(new GetFileCallback() {
                        @Override
                        public void done(File file, ParseException e) {
                            if (e == null) {
                                Glide.with(getApplicationContext())
                                        .load(file)
                                        .transform(new CircleTransform(getApplicationContext()))
                                        .into(profile_picture);
                            }
                        }
                    });
                }
            }
        });
        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
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
                } else if (editName.getText().toString()==""||editLastname.getText().toString()==""||
                        editDay.getText().toString()==""||editMonth.getText().toString()==""||editYear.getText().toString()==""){
                    Snackbar.make(v, R.string.empty_field, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    //inizializzo progressbar di caricamento
                    final ProgressDialog dialog = ProgressDialog.show(EditProfileActivity.this, "",
                            getString(R.string.check_data), true);

                    utente.setEmail(editEmail.getText().toString());
                    utente.put("name",editName.getText().toString());
                    try {
                        utente.save();
                        persona.put("lastname",editLastname.getText().toString());

                        // Formatto data
                        final String edit_date = editYear.getText().toString() + "-" + editMonth.getText().toString() + "-" + editDay.getText().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = null;
                        try {
                             date = sdf.parse(edit_date);
                        } catch (java.text.ParseException e) {
                            e.printStackTrace();
                        }
                        persona.put("date",date);
                        persona.save();
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
    public void onBackPressed() {

        // Reinderizzo l'utente alla main activity
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);

        finish();
    }

    //funzione di supporto per il tasto indietro
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/HomeActivity button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
