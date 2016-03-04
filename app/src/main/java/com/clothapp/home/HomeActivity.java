package com.clothapp.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.resources.CircleTransform;
import com.clothapp.settings.SettingsActivity;
import com.clothapp.upload.UploadActivity;
import com.clothapp.upload.UploadPhotoActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    public static Context context;
    public static Activity activity;

    private DrawerLayout mDrawerLayout;
    public static FloatingActionsMenu menuMultipleActions;

    // This file will always be the same. Make it static so it can be accessed by multiple instances
    // of the HomeActivity.
    public static File drawerProfilePhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        context = HomeActivity.this;
        activity = this;

        // Initialize Toolbar.
        initToolbar();

        // Initialize ViewPager and TabLayout. This also initializes the navigation drawer.
        initViewPagerAndTabs();

        // Initialize FloatingActionButton.
        setupFloatingButton();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));

        // Initialize the navigation drawer.
        initDrawer(mToolbar);
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.home_viewpager);
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFragment(MostRecentFragment.newInstance(), "Most Recent");
        pagerAdapter.addFragment(TopRatedFragment.newInstance(), "Top Rated");
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void initDrawer(Toolbar toolbar) {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.open_navigation, R.string.close_navigation);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.home_nav_view);

        // Setup OnClickListener for the navigation drawer.
        navigationView.setNavigationItemSelectedListener(new HomeNavigationItemSelectedListener());

        // Get drawer header
        View headerLayout = navigationView.getHeaderView(0);

        // Get the image view containing the user profile photo
        final ImageView drawerProfile = (ImageView) headerLayout.findViewById(R.id.navigation_drawer_profile_photo);
        TextView drawerUsername = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_username);
        TextView drawerRealName = (TextView) headerLayout.findViewById(R.id.navigation_drawer_profile_real_name);

        // Set the user profile photo to the just created rounded image
        Glide.with(context)
                .load(R.drawable.com_facebook_profile_picture_blank_square)
                .transform(new CircleTransform(context))
                .into(drawerProfile);

        ParseUser currentUser = ParseUser.getCurrentUser();
        drawerUsername.setText(capitalize(currentUser.getUsername()));
        drawerRealName.setText(capitalize(currentUser.getString("name")));

        if (drawerProfilePhotoFile == null) {
            ParseQuery<ParseObject> query = new ParseQuery<>("UserPhoto");
            query.whereEqualTo("username", currentUser.getUsername());

            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject photo, ParseException e) {

                    if (e == null) {
                        Log.d("HomeActivity", "ParseObject for profile image found!");

                        ParseFile parseFile = photo.getParseFile("thumbnail");
                        parseFile.getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(File file, ParseException e) {

                                if (e == null) {
                                    Log.d("HomeActivity", "File for profile image found!");

                                    drawerProfilePhotoFile = file;

                                    // Set the user profile photo to the just created rounded image
                                    Glide.with(context)
                                            .load(file)
                                            .transform(new CircleTransform(context))
                                            .into(drawerProfile);

                                } else {
                                    Log.d("HomeActivity", "Error: " + e.getMessage());
                                }
                            }
                        });

                    } else {
                        Log.d("HomeActivity", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            // Set the user profile photo to the just created rounded image
            Glide.with(context)
                    .load(drawerProfilePhotoFile)
                    .transform(new CircleTransform(context))
                    .into(drawerProfile);
        }
    }

    private String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private void setupFloatingButton(){
        menuMultipleActions = (FloatingActionsMenu) findViewById(R.id.upload_action);

        com.getbase.floatingactionbutton.FloatingActionButton camera = new com.getbase.floatingactionbutton.FloatingActionButton(getBaseContext());
        camera.setTitle("Camera");
        camera.setIcon(R.mipmap.camera_icon);
        camera.setColorNormal(Color.rgb(210,36,37));
        camera.setColorPressed(Color.RED);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), UploadPhotoActivity.class);
                i.putExtra("photoType", 2187);
                startActivity(i);
            }
        });
        com.getbase.floatingactionbutton.FloatingActionButton gallery = new com.getbase.floatingactionbutton.FloatingActionButton(getBaseContext());
        gallery.setTitle("Gallery");
        gallery.setIcon(R.mipmap.gallery_icon);
        gallery.setColorNormal(Color.rgb(210,36,37));
        gallery.setColorPressed(Color.RED);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirect the user to the upload activity and upload a photo
                Intent i = new Intent(getApplicationContext(), UploadPhotoActivity.class);
                i.putExtra("photoType", 1540);
                startActivity(i);
            }
        });
        menuMultipleActions.addButton(camera);
        menuMultipleActions.addButton(gallery);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            // Clicked on the home (three horizontal lines icon).
            case android.R.id.home:
                // Log.d("HomeActivity", "android.R.id.home");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            // Clicked on the settings button.
            case R.id.action_settings:
                Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
                startActivity(intent);
                // Log.d("HomeActivity", "R.id.action_settings");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        // Close the floating action button if it is open
        if(menuMultipleActions.isExpanded()) {
            menuMultipleActions.collapse();
            return;
        }

        // Close the navigation drawer if it is open
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }

        // Otherwise use default behavior.
        super.onBackPressed();
    }

    // This class handles click to each item of the navigation drawer
    class HomeNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            Intent intent;

            switch (item.getItemId()) {

                // Clicked on "Home" page button.
                // Do nothing since we already are in the home page.
                case R.id.nav_home:
                    break;

                case R.id.nav_settings:
                    Log.d("HomeActivity", "Clicked on R.id.nav_settings");

                    intent = new Intent(HomeActivity.this, SettingsActivity.class);
                    startActivity(intent);
                    break;

                // Clicked on "My Profile" item.
                case R.id.nav_profile:

                    Log.d("HomeActivity", "Clicked on R.id.nav_profile");

                    String currentUser = ParseUser.getCurrentUser().getUsername();
                    intent = new Intent(HomeActivity.activity, UserProfileActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                    break;

                // Clicked on "Logout" item.
                case R.id.nav_logout:

                    Log.d("HomeActivity", "Clicked on R.id.nav_logout");

                    final ProgressDialog dialog = ProgressDialog.show(HomeActivity.this, "", "Logging out. Please wait...", true);
                    Thread logout = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ParseUser.logOut();
                            System.out.println("debug: logout eseguito");
                        }
                    });
                    logout.start();

                    intent = new Intent(HomeActivity.activity, MainActivity.class);
                    dialog.dismiss();
                    startActivity(intent);

                    finish();
                    break;

            }

            // Close the navigation drawer after item selection.
            HomeActivity.this.mDrawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    }
}
