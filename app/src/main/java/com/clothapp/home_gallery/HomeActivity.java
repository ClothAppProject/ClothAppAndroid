package com.clothapp.home_gallery;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.BaseActivity;
import com.clothapp.ProfileActivity;
import com.clothapp.R;
import com.clothapp.resources.CircleTransform;
import com.clothapp.settings.SettingsActivity;
import com.clothapp.upload.UploadCameraActivity;
import com.clothapp.upload.UploadGalleryActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/02/16.
 */
public class HomeActivity extends BaseActivity implements ActionBar.TabListener{
    static FloatingActionsMenu menuMultipleActions;
    private ViewPager mViewPager;
    private HomeAdapter mAdapter;
    // Tab titles

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        getSupportActionBar().setTitle(R.string.homepage_button);
        setUpMenu();
        getSupportActionBar().setHomeButtonEnabled(false);

        // mostriamo tabs nella actionbar
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // setto l'adattatore passandogli le varie cose
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new HomeAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                //Quando si fa lo swipe tra le sezioni cambia la posizione
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });

        // Aggiungo una tab per ogni sezione
        String[] titles = getResources().getStringArray(R.array.home_titles);
        for (int i = 0; i < mAdapter.getCount(); i++) {
            //creo una tab con testo corrispondente ed interfaccia tablistener
            getSupportActionBar().addTab(
                    getSupportActionBar().newTab()
                            .setText(titles[i])
                            .setTabListener(this));
        }

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
    }

    //tabs stuff
    @Override
    public void onTabSelected(ActionBar.Tab selectedtab, FragmentTransaction arg1) {
        mViewPager.setCurrentItem(selectedtab.getPosition()); //update tab position on tap
    }
    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {}
    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {}

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
        textView.setText(ParseUser.getCurrentUser().getString("name"));

        TextView textView2 = (TextView) findViewById(R.id.nameUsername);
        textView2.setText(ParseUser.getCurrentUser().getUsername());


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
                                    .transform(new CircleTransform(HomeActivity.this))
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