package com.clothapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.clothapp.home_gallery.HomeActivity;
import com.clothapp.login_signup.MainActivity;
import com.clothapp.profile.ProfileActivity;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.resources.NavDrawerItem;
import com.clothapp.resources.NavDrawerListAdapter;
import com.clothapp.settings.SettingsActivity;
import com.parse.ParseUser;

import java.util.ArrayList;


public class BaseActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    //TODO qui (todo che mi sono serviti a vedere delle cose nel codice per non sbagliare) Roberto
    private LinearLayout drawerll;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer);
        // if (savedInstanceState == null) {
        // // on first time display view for first nav item
        // // displayView(0);
        // }
    }

    public void set(String[] navMenuTitles, TypedArray navMenuIcons, int selected) {
        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        //TODO qui (todo che mi sono serviti a vedere delle cose nel codice per non sbagliare) Roberto
        drawerll = (LinearLayout) findViewById(R.id.drawerll);
        //
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // adding nav drawer items

        boolean flag;

        if (navMenuIcons == null) {
            for (int i = 0; i < navMenuTitles.length; i++) {
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i]));
            }
        } else {
            for (int i = 0; i < navMenuTitles.length; i++) {
                //System.out.println("debug: i = " + i);
                if(i==selected) flag=true;
                else flag=false;
                navDrawerItems.add(new NavDrawerItem(navMenuTitles[i], navMenuIcons.getResourceId(i, -1),flag));
            }
        }

        // Recycle the typed array
        navMenuIcons.recycle();

        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(),
                navDrawerItems);
        mDrawerList.setAdapter(adapter);

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setElevation(0);
        // getSupportActionBar().setIcon(R.drawable.ic_drawer);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                null, //R.mipmap.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                //TODO qua e sotto titolo del menu che cambia all'apertura del menu ma poi torna quello dell'activity precedente
                //getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //getSupportActionBar().setTitle(mDrawerTitle);
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getSupportMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
        //TODO qui (todo che mi sono serviti a vedere delle cose nel codice per non sbagliare) Roberto
            if (mDrawerLayout.isDrawerOpen(drawerll)) {
                mDrawerLayout.closeDrawer(drawerll);
            } else {
                mDrawerLayout.openDrawer(drawerll);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        // boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     * */
    public void displayView(int position) {
        // update the main content by replacing fragments
        Intent i = null;
        switch (position) {
            case 0:
                //homepage
                i = new Intent(this, HomeActivity.class);
                break;
            case 1:
                //profilo
                i = new Intent(this, ProfileActivity.class);
                i.putExtra("user", ParseUser.getCurrentUser().getUsername().toString());
                break;
            case 2:
                //settings
                i = new Intent(this, SettingsActivity.class);
                break;
            case 3:
                //logout
                final ProgressDialog dialog = ProgressDialog.show(BaseActivity.this, "",
                        "Logging out. Please wait...", true);
                Thread logout = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ParseUser.logOut();
                        System.out.println("debug: logout eseguito");
                    }
                });
                logout.start();
                i = new Intent(this, MainActivity.class);
                dialog.dismiss();
                break;
            default:
                break;
        }
        startActivity(i);
        finish();
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerList.setSelection(position);
        mDrawerLayout.closeDrawer(drawerll);
    }


    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void closeDrawer(){
        mDrawerLayout.closeDrawer(drawerll);
    }

    public boolean isOpen(){
        return mDrawerLayout.isDrawerOpen(drawerll);
    }
}