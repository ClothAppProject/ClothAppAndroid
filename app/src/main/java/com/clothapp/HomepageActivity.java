package com.clothapp;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Image;
import com.clothapp.resources.ImageGridViewAdapter;
import com.clothapp.settings.SettingsActivity;
import com.clothapp.upload.UploadCameraActivity;
import com.clothapp.upload.UploadGalleryActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.io.File;
import java.util.List;
import static com.clothapp.resources.ExceptionCheck.check;


public class HomepageActivity extends BaseActivity {

    ApplicationSupport photos;
    String name = ParseUser.getCurrentUser().getString("name");
    String username = ParseUser.getCurrentUser().getUsername();
    FloatingActionsMenu menuMultipleActions;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageGridViewAdapter imageGridViewAdapter;
    View vi;
    Boolean canLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        photos = (ApplicationSupport) getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

         vi = new View(this);
        final GridView gridview = (GridView) findViewById(R.id.galleria_homepage);

        try {
            getSupportActionBar().setTitle(R.string.homepage_button);
        } catch (NullPointerException e) {
            Log.d("HomepageActivity", "Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Create a side menu
        setUpMenu();



        //istanzio lo swipe to refresh
        // find the layout
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //query che prende tutte le foto più nuove rispetto a quelle già in memoria
                ParseQuery<ParseObject> updatePhotos = new ParseQuery<ParseObject>("Photo");
                updatePhotos.whereGreaterThan("createdAt", photos.getFirstDate());
                updatePhotos.orderByDescending("createdAt");
                updatePhotos.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e==null) {
                            if (objects.size()>0) {
                                //modifico la data della prima foto
                                photos.setFirstDate(objects.get(0).getCreatedAt());
                                for (int i = objects.size() - 1; i >= 0; i--) {
                                    ParseFile f = objects.get(i).getParseFile("thumbnail");
                                    try {
                                        //ottengo la foto e la aggiungo per prima
                                        photos.addFirstPhoto(new Image(f.getFile(), objects.get(i).getObjectId()));
                                    } catch (ParseException e1) {
                                        check(e1.getCode(), vi, e1.getMessage());
                                    }
                                    //aggiorno la galleria
                                    loadSplashImage(gridview);
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                            check(e.getCode(), vi, e.getMessage());
                        }
                    }
                });
            }
        });
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.background, R.color.orange);

        // UploadCameraActivity a new photo button menu initialization
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.upload_action);

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

        //listener sullo scrollview della gridview
        gridview.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad)  {
                        canLoad = false;
                        int toDownload = 10;
                        if (photos.getPhotos().size() % 2 == 0) toDownload = 11;
                        ParseQuery<ParseObject> updatePhotos = new ParseQuery<ParseObject>("Photo");
                        updatePhotos.whereLessThan("createdAt", photos.getLastDate());
                        updatePhotos.orderByDescending("createdAt");
                        updatePhotos.setLimit(toDownload);
                        updatePhotos.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        int i;
                                        for (i = 0; i < objects.size(); i++) {
                                            ParseFile f = objects.get(i).getParseFile("thumbnail");
                                            try {
                                                //ottengo la foto e la aggiungo per ultima
                                                Image toAdd = new Image(f.getFile(), objects.get(i).getObjectId());
                                                photos.addLastPhoto(toAdd);
                                                //notifico l'image adapter di aggiornarsi
                                                imageGridViewAdapter.notifyDataSetChanged();
                                            } catch (ParseException e1) {
                                                check(e1.getCode(), vi, e1.getMessage());
                                            }
                                        }
                                        canLoad = true;
                                        //modifico la data dell'utlima foto
                                        photos.setLastDate(objects.get(i - 1).getCreatedAt());
                                    }
                                } else {
                                    check(e.getCode(), vi, e.getMessage());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){}
        });
    }

    private void loadSplashImage(final GridView gridview) {
        imageGridViewAdapter = new ImageGridViewAdapter(HomepageActivity.this, photos.getPhotos());
        gridview.setAdapter(imageGridViewAdapter);

        //listener su ogni foto della gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if(menuMultipleActions.isExpanded()) {
                    menuMultipleActions.collapse();
                }else {
                    Intent toPass = new Intent(getApplicationContext(), ImageFragment.class);
                    toPass.putExtra("objectId", photos.getPhotos().get(position).getObjectId());
                    startActivity(toPass);
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
        final ImageView imageView = (ImageView) findViewById(R.id.ppMenu);

        TextView textView = (TextView) findViewById(R.id.nameMenu);
        textView.setText(name);

        TextView textView2 = (TextView) findViewById(R.id.nameUsername);
        textView2.setText(username);


        final View vi = new View(this.getApplicationContext());

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
        queryFoto.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //  if the user has a profile pic it will be shown in the side menu
                    //  else the app logo will be shown
                    if (objects.size() != 0) {
                        ParseFile f = objects.get(0).getParseFile("profilePhoto");

                        try {
                            File file = f.getFile();
                            Glide.with(getApplicationContext())
                                    .load(file)
                                    .centerCrop()
                                    .transform(new CircleTransform(HomepageActivity.this))
                                    .into(imageView);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                i.putExtra("user",ParseUser.getCurrentUser().getUsername());
                startActivity(i);
                finish();
            }
        });

        LinearLayout l = (LinearLayout) findViewById(R.id.drawer);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //va all'activity settings solo per prova, dovremo decidere poi cosa fare
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

}