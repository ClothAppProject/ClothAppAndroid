package com.clothapp.search;

/**
 * Created by nc94 on 2/15/16.
 */
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.home_gallery.HomeAdapter;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.Image;
import com.clothapp.resources.User;
import com.clothapp.settings.SettingsActivity;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private SearchView searchView;
    private ListView listUser;
    private ListView listCloth;
    private ListView listTag;
    private ViewPager viewPager;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String[] titles = getResources().getStringArray(R.array.search_titles);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        //set adapter to  ViewPager
        viewPager.setAdapter(new SearchAdapter(getSupportFragmentManager(), titles));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        Log.d("SearchA","oncreate");
        handleIntent(getIntent());
    }


    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.home_appbar, menu);

        getMenuInflater().inflate(R.menu.home_appbar, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        //quando faccio una nuova ricerca aggiorno questa activity invece di crearne una nuova
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public String queryL;

            @Override
            public boolean onQueryTextSubmit(String query) {
                this.queryL=searchView.getQuery().toString();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        MenuItem actionMenuItem = menu.findItem(R.id.menu_search);

        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);

        // Any other things you have to do when creating the options menu…

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante indietro termino semplicemente l'activity
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                Intent i=new Intent(getBaseContext(),SettingsActivity.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("SearchA",query);
        }
    }


    public String getQuery() {
        return query;
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            for (itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
