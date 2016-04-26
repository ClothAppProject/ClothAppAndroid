package com.clothapp.home;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import com.clothapp.Menu;
import com.clothapp.R;
import com.clothapp.settings.SettingsActivity;
import com.clothapp.upload.UploadPhotoActivity;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.io.File;

public class HomeActivity extends AppCompatActivity {

    public static Context context;
    public static Activity activity;

    private DrawerLayout mDrawerLayout;
    public static FloatingActionsMenu menuMultipleActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_home);

        context = getApplicationContext();
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
        ////////////////////////////
        // Initialize the navigation drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.open_navigation, R.string.close_navigation);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.home_nav_view);
        Menu.initMenu(mDrawerLayout, context, navigationView, toggle, "home", null, HomeActivity.this);
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


    private void setupFloatingButton()  {
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
                menuMultipleActions.collapse();
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
                menuMultipleActions.collapse();
                startActivity(i);
            }
        });
        menuMultipleActions.addButton(camera);
        menuMultipleActions.addButton(gallery);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
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

    public class ScrollingFABBehavior extends FloatingActionButton.Behavior {


        private static final String TAG = "ScrollingFABBehavior";

        public ScrollingFABBehavior(Context context, AttributeSet attrs) {
            super();
            // Log.e(TAG, "ScrollAwareFABBehavior");
        }


        public boolean onStartNestedScroll(CoordinatorLayout parent, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {

            return true;
        }

        @Override
        public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton child, View dependency) {
            if (dependency instanceof RecyclerView)
                return true;

            return false;
        }

        @Override
        public void onNestedScroll(CoordinatorLayout coordinatorLayout,
                                   FloatingActionButton child, View target, int dxConsumed,
                                   int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
            // TODO Auto-generated method stub
            super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed,
                    dxUnconsumed, dyUnconsumed);
            //Log.e(TAG, "onNestedScroll called");
            if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
                //   Log.e(TAG, "child.hide()");
                child.hide();
            } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
                //  Log.e(TAG, "child.show()");
                child.show();
            }
        }}

}
