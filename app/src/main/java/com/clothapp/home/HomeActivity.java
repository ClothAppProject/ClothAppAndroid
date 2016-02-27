package com.clothapp.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
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

import com.clothapp.R;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.upload.UploadCameraActivity;
import com.clothapp.upload.UploadGalleryActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.parse.ParseUser;

public class HomeActivity extends AppCompatActivity {

    public static Context context;
    public static Activity activity;

    private DrawerLayout mDrawerLayout;
    private static FloatingActionsMenu menuMultipleActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        context = HomeActivity.this;
        activity = this;

        initToolbar();
        initViewPagerAndTabs();
        setupFloatingButton();
    }

    private void initToolbar() {
        Toolbar mToolbar = (Toolbar) findViewById(R.id.home_toolbar);
        setSupportActionBar(mToolbar);
        setTitle(getString(R.string.app_name));
        // mToolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        initDrawer(mToolbar);
    }

    private void initViewPagerAndTabs() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.home_viewpager);
        HomePagerAdapter pagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
//        pagerAdapter.addFragment(MostRecentFragment.newInstance(20), "Most Recent");
//        pagerAdapter.addFragment(MostRecentFragment.newInstance(4), "Tab 2");
        pagerAdapter.addFragment(MostRecentFragment.newInstance(), "Most Recent");
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
        navigationView.setNavigationItemSelectedListener(new HomeNavigationItemSelectedListener());
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
                Intent i = new Intent(getApplicationContext(), UploadCameraActivity.class);
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
                Intent i = new Intent(getApplicationContext(), UploadGalleryActivity.class);
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

            case android.R.id.home:
                // Log.d("HomeActivity", "android.R.id.home");
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_settings:
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

        super.onBackPressed();
    }

    class HomeNavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {

        @SuppressWarnings("StatementWithEmptyBody")
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {

            Intent intent;

            switch (item.getItemId()) {

                case R.id.nav_home:
                    break;

                case R.id.nav_profile:

                    Log.d("HomeActivity", "Clicked on R.id.nav_profile");

                    String currentUser = ParseUser.getCurrentUser().getUsername();
                    intent = new Intent(HomeActivity.activity, UserProfileActivity.class);
                    intent.putExtra("user", currentUser);
                    startActivity(intent);
                    break;

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

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }
}
