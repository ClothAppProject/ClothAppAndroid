package com.clothapp;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.clothapp.profilepicture.*;
import com.clothapp.resources.BitmapUtil;
import com.clothapp.resources.ExceptionCheck;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/01/16.
 */
public class ProfileActivity extends BaseActivity {

    private static LruCache<String, Bitmap> mMemoryCache;
    LinearLayout myGallery;
    Context mContext;
    ParseUser user;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String nome = getIntent().getExtras().getString("user");
        try {
            getSupportActionBar().setTitle(nome);
        } catch (NullPointerException e) {
            Log.d("ProfileActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }
        final View vi = new View(this.getApplicationContext());

        //ottengo user
        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("username", nome);
        try {
            user = queryUser.find().get(0);
        } catch (ParseException e) {
            ExceptionCheck.check(e.getCode(), vi, e.getMessage());
        }


        //imposto immagine profilo a metà schermo
        final ImageView profilepicture = (ImageView) findViewById(R.id.profilepicture);
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int width = (metrics.widthPixels) / 2;
        profilepicture.getLayoutParams().height = width;
        profilepicture.getLayoutParams().width = width;

        // Create side menu
        setUpMenu();

        final TextView nfoto = (TextView) findViewById(R.id.nfoto);
        final TextView nfollowing = (TextView) findViewById(R.id.nfollowing);
        final TextView nfollowers = (TextView) findViewById(R.id.nfollowers);

        final TextView username = (TextView) findViewById(R.id.username_field);
        final TextView name = (TextView) findViewById(R.id.name_field);
        final Button follow_edit = (Button) findViewById(R.id.follow_edit);

        //tasto "segui" se profilo non tuo, "modifica profilo" se profilo tuo
        if (user.getUsername().toString()==ParseUser.getCurrentUser().getUsername().toString()) {
            follow_edit.setText("Modifica Profilo");
        }else{
            follow_edit.setText("Segui");
        }

        //inizializzo memoria cache e galleria
        mContext = this;
        myGallery = (LinearLayout) findViewById(R.id.personal_gallery);
        // Get memory class of this device, exceeding this amount will throw an
        // OutOfMemory exception.
        final int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = 1024 * 1024 * memClass / 8;

        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in bytes rather than number of items.
                return bitmap.getByteCount();
            }
        };

        // Get Parse user
        username.setText(user.getUsername());
        name.setText(capitalize(user.get("name").toString()));

        //imposto n° followers, n° following, n° foto
        if (user.getList("followers") != null) {
            //TODO risolvere problema della lista di followers e following
            //nfollowers.setText((user.getList("followers").size()));
        }
        if (user.getList("following") != null) {
            //nfollowing.setText((user.getList("following").size()));
        }
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("user", user.getUsername());
        queryFoto.orderByDescending("createdAt");
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        myGallery.addView(insertPhoto(objects.get(i)));
                    }
                    if (objects != null) {
                        nfoto.setText("" + objects.size());
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });


        //Get Parse user profile picture
        ParseQuery<ParseObject> queryProfilePicture = new ParseQuery<ParseObject>("UserPhoto");
        queryProfilePicture.whereEqualTo("username", user.getUsername());
        queryProfilePicture.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() == 0) {
                        profilepicture.setImageResource(R.mipmap.profile);
                    } else {
                        ParseFile picture = objects.get(0).getParseFile("profilePhoto");
                        picture.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    profilepicture.setImageBitmap(BitmapUtil.scala(imageBitmap));
                                    profilepicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                } else {
                                    check(e.getCode(), vi, e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });

        //listener sull'imageview dell'immagine del profilo
        if (user.getUsername().toString() == ParseUser.getCurrentUser().getUsername().toString()) {
            profilepicture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(R.string.choose_profile_picture)
                            //.set
                            .setItems(R.array.profile_picture_options, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            // Redirect the user to the ProfileCameraActivity Activity
                                            Intent intentCamera = new Intent(getApplicationContext(), ProfileCameraActivity.class);
                                            startActivity(intentCamera);
                                            break;
                                        case 1:
                                            // Redirect the user to the ProfileGalleryActivity Activity
                                            Intent intentGallery = new Intent(getApplicationContext(), ProfileGalleryActivity.class);
                                            startActivity(intentGallery);
                                            break;
                                        case 2:
                                            //delete profile picture
                                            ParseQuery<ParseObject> queryFotoProfilo = new ParseQuery<ParseObject>("UserPhoto");
                                            queryFotoProfilo.whereEqualTo("username", user.getUsername());
                                            queryFotoProfilo.findInBackground(new FindCallback<ParseObject>() {
                                                @Override
                                                public void done(List<ParseObject> objects, ParseException e) {
                                                    if (e == null) {
                                                        if (objects.size() > 0) {
                                                            objects.get(0).deleteInBackground();
                                                            finish();
                                                            startActivity(getIntent());
                                                        }
                                                    } else {
                                                        check(e.getCode(), vi, e.getMessage());
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
        }
    }

    // In caso sia premuto il pulsante indietro torniamo alla home activity
    @Override
    public void onBackPressed() {
        // Redirect the user to the Homepage Activity
        Intent i = new Intent(getApplicationContext(), HomepageActivity.class);
        startActivity(i);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    // Capitalize the first character of a string.
    public String capitalize(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    // Create a side menu
    private void setUpMenu() {
        String[] navMenuTitles;
        TypedArray navMenuIcons;
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items); // load titles from strings.xml

        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);//load icons from strings.xml

        set(navMenuTitles, navMenuIcons, 1);
    }

    private String formatDate(String s) {
        String[] dataArray = s.split(" ");
        s = dataArray[2] + "/" + formatMonth(dataArray[1]) + "/" + dataArray[5];
        return s;
    }

    private String formatMonth(String s) {
        switch (s) {
            case "Jan":
                return "01";
            case "Feb":
                return "02";
            case "Mar":
                return "03";
            case "Apr":
                return "04";
            case "May":
                return "05";
            case "Jun":
                return "06";
            case "Jul":
                return "07";
            case "Aug":
                return "08";
            case "Sep":
                return "09";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
        }
        return "";
    }


    // ROBA DELLA CACHE
    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) mMemoryCache.get(key);
    }

    //ROBA DELLA GALLERIA
    private View insertPhoto(ParseObject p) {

        LinearLayout layout = new LinearLayout(getApplicationContext());
        layout.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
        layout.setGravity(Gravity.CENTER);

        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setLayoutParams(new LinearLayout.LayoutParams(400, 400));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        final String imageKey = String.valueOf(p);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            if (cancelPotentialWork(p, imageView)) {
                //imageView.setImageResource(R.drawable.image_placeholder);

                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable = new AsyncDrawable(mContext.getResources(), null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(p);
            }
        }

        //setto listener su ogni imageview
        final ParseObject idToPass = p;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toPass = new Intent(getApplicationContext(), ImageFragment.class);
                toPass.putExtra("objectID", idToPass.getObjectId().toString());
                startActivity(toPass);
            }
        });
        layout.addView(imageView);
        return layout;
    }

    static class AsyncDrawable extends BitmapDrawable {

        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    public static boolean cancelPotentialWork(ParseObject toLoad, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final ParseObject bitmapData = bitmapWorkerTask.pic;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == null || bitmapData != toLoad) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    //ROBA DEL BITMAPWORKERTASK
    public static class BitmapWorkerTask extends AsyncTask<ParseObject, Void, Bitmap> {

        private final WeakReference<ImageView> imageViewReference;
        public ParseObject pic;

        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground(ParseObject... params) {
            //TODO check parameters of the following method
            final Bitmap bitmap = decodeSampledBitmap(params[0], 400, 400);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }
            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = (ImageView) imageViewReference.get();
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        //private static byte[] bit;
        public Bitmap decodeSampledBitmap(ParseObject photo, int reqWidth, int reqHeight) {

            byte[] bit = null;
            ParseFile imageFile = (ParseFile) photo.get("photo");
            try {
                bit = imageFile.getData();

            } catch (ParseException e) {
                //errore nel reperire gli oggetti Photo dal database
                // check(e.getCode(), view, e.getMessage());
                // TODO CHECK ON ERRORS
                // TODO va passata la view per controllare gli errori di parse
            }

            Bitmap bm = null;
            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //BitmapFactory.decodeFile(path, options);
            BitmapFactory.decodeByteArray(bit, 0, bit.length, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            //bm = BitmapFactory.decodeFile(path, options);
            bm = BitmapFactory.decodeByteArray(bit, 0, bit.length, options);

            return bm;
        }

        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float) height / (float) reqHeight);
                } else {
                    inSampleSize = Math.round((float) width / (float) reqWidth);
                }
            }

            return inSampleSize;
        }
    }
}
