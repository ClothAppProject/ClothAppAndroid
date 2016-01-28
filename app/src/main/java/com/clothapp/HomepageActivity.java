package com.clothapp;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;

import com.clothapp.resources.ImageAdapter;
import com.clothapp.resources.ImageGridViewAdapter;
import com.clothapp.resources.ImageGridViewAdapter;
import com.clothapp.upload.UploadCameraActivity;
import com.clothapp.upload.UploadGalleryActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class HomepageActivity extends BaseActivity {

    private static List<ParseObject> photos;
    private static LruCache<String, Bitmap> mMemoryCache;

    //  it is the percentage of VM memory we are reserving for our cache
    //  4 stands for 25% of VM memory
    //  maxMemory/MYCACHE is the formula
    //  needs to be changed according to the device the app is running on
    private static int MYCACHE = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_homepage);
        final GridView gridview = (GridView) findViewById(R.id.galleria_homepage);

        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        System.out.println("maxMemory:"+maxMemory);
        final int cacheSize = maxMemory / MYCACHE;



        try {
            getSupportActionBar().setTitle(R.string.homepage_button);
        } catch (NullPointerException e) {
            Log.d("HomepageActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Create a side menu
        setUpMenu();

        // UploadCameraActivity a new photo button menu initialization
        FloatingActionsMenu menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.upload_action);

        com.getbase.floatingactionbutton.FloatingActionButton camera = new com.getbase.floatingactionbutton.FloatingActionButton(getBaseContext());
        camera.setTitle("Camera");
        camera.setIcon(R.mipmap.camera_icon);
        camera.setColorNormal(Color.RED);
        camera.setColorPressed(Color.RED);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), UploadCameraActivity.class);
                startActivity(i);
                finish();
            }
        });
        com.getbase.floatingactionbutton.FloatingActionButton gallery = new com.getbase.floatingactionbutton.FloatingActionButton(getBaseContext());
        gallery.setTitle("Gallery");
        gallery.setIcon(R.mipmap.gallery_icon);
        gallery.setColorNormal(Color.RED);
        gallery.setColorPressed(Color.RED);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), UploadGalleryActivity.class);
                startActivity(i);
                finish();
            }
        });

        menuMultipleActions.addButton(camera);
        menuMultipleActions.addButton(gallery);

        //questa va chiamata solo la prima volta
        loadSplashImage(gridview);
        //per tutte le altre volte loadImage che è da fare

    }

    private void loadSplashImage(final GridView gridview) {
        File[] file = (File[]) getIntent().getExtras().get("photo");
        gridview.setAdapter(new ImageGridViewAdapter(HomepageActivity.this, file));
        //TODO:bisogna inizializzare photo per applicare i clickListener
      /*  gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent toPass = new Intent(getApplicationContext(), ImageFragment.class);
                toPass.putExtra("url", photos.get(position).getParseFile("photo").getUrl());
                startActivity(toPass);
            }


        });  */
    }

    //da modificare usando file al posto di url
    private void loadImage(final GridView gridview) {
        final View vi = new View(this.getApplicationContext());
        final File[]photo=new File[10];
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.setLimit(10);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fotos, ParseException e) {
                if (e == null) {
                    Log.d("Query", "Retrieved " + fotos.size() + " photos");

                    int i;
                    for (i = 0; i < 10; i++) {
                        ParseObject obj = fotos.get(i);
                        ParseFile file = obj.getParseFile("photo");
                        try {
                            photo[i] = file.getFile();
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        System.out.println(photo[i]);

                    }
                    Intent intent = new Intent(getBaseContext(), HomepageActivity.class);
                    intent.putExtra("photo", photo);
                    startActivity(intent);
                } else {
                    //errore nel reperire gli oggetti Photo dal database
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });

    }


    // This function creates a side menu and populates it with the given elements.
    private void setUpMenu() {

        String[] navMenuTitles;
        TypedArray navMenuIcons;

        // Load titles from string.xml
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        // Load icons from strings.xml
        navMenuIcons = getResources()
                .obtainTypedArray(R.array.nav_drawer_icons);

        set(navMenuTitles, navMenuIcons, 0);

    }

    public static LruCache<String, Bitmap> getCache(){
        return mMemoryCache;
    }

//CACHE
//---------------------------

    public static void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public static Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    //GALLERY STUFF
    //-------------------


    public static void loadBitmap(ParseObject p, ImageView imageView, Resources r) {

        //Bitmap icon = BitmapFactory.decodeResource(r,
        //      R.drawable.logo);

        final String imageKey = String.valueOf(p);
        final Bitmap bitmap = getBitmapFromMemCache(imageKey);

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }else {

            if (cancelPotentialWork(p, imageView)) {
                //imageView.setImageResource(R.drawable.image_placeholder);

                final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                final AsyncDrawable asyncDrawable =
                        new AsyncDrawable(r, null, task);
                imageView.setImageDrawable(asyncDrawable);
                task.execute(p);
            }
        }
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


    // BITMAPWORKER
    //-----------------

    public static class BitmapWorkerTask extends AsyncTask<ParseObject, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;

        public ParseObject pic;


        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(ParseObject... params) {
            final Bitmap bitmap = decodeSampledBitmap(params[0], 300, 300);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }


        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }

        }


        //private static byte[] bit;
        public Bitmap decodeSampledBitmap(ParseObject photo, int reqWidth, int reqHeight) {

            byte[] bit = null;
            ParseFile imageFile = photo.getParseFile("photo");
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

        public int calculateInSampleSize(

                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {
                if (width > height) {
                    inSampleSize = Math.round((float)height / (float)reqHeight);
                } else {
                    inSampleSize = Math.round((float)width / (float)reqWidth);
                }
            }
            return inSampleSize;
        }
    }
}
