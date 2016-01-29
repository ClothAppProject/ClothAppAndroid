package com.clothapp;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.clothapp.resources.ImageGridViewAdapter;
import com.clothapp.upload.UploadCameraActivity;
import com.clothapp.upload.UploadGalleryActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import java.util.List;
import java.util.logging.Handler;

public class HomepageActivity extends BaseActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        final View vi = new View(this);
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
                //codice da eseguire quando si aggiorna la galleria
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.background, R.color.orange);

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
        //prendo la lista delle immagini da caricare dalla variabile globale
        final List <Image> photos = ((ApplicationSupport) getApplicationContext()).getPhotos();
        gridview.setAdapter(new ImageGridViewAdapter(HomepageActivity.this, photos));

        //listener su ogni foto della gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent toPass = new Intent(getApplicationContext(), ImageFragment.class);
                toPass.putExtra("objectId", photos.get(position).getObjectId());
                startActivity(toPass);
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
        //ImageView imageView = (ImageView) findViewById(R.id.cerchio);
        /*Picasso.with(this)
                .load("http://th.cineblog.it/x__GR2Et_Bnq8lTBH-8E4IrZN5U=/fit-in/655xorig/http://media.cineblog.it/c/caa/suicide-squad-nuove-foto-dal-set-e-altri-regali-al-cast-dal-joker-di-jared-leto.jpg")
                .transform(new CircleTransform())
                .into(imageView);*/

    }

}
