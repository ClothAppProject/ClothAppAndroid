package com.clothapp;

/**
 * Created by nc94 on 2/15/16.
 */
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clothapp.profile.ProfileActivity;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.resources.SearchUtility;
import com.clothapp.resources.SearchUtility;
import com.clothapp.resources.User;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SearchResultsActivity extends AppCompatActivity {
    private SearchView searchView;
    private ListView listUser;
    private ListView listCloth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            @Override
            public boolean onQueryTextSubmit(String query) {
                research(searchView.getQuery().toString());
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
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
            query=query.trim();

            //use the query to search
            View v=(View)findViewById(R.id.searchview);

            //prendo la listview e la rootView
            RelativeLayout rootView=(RelativeLayout)findViewById(R.id.searchview);
            listUser=(ListView)findViewById(R.id.user_find);
            listCloth=(ListView)findViewById(R.id.image_find);

            //faccio la query a Parse
            List<User> user= SearchUtility.searchUser(query,rootView);
            //List<Image> cloth=SearchUtility.searchCloth(query,rootView);

            //stampa di DEBUG
            for(int i=0;i<user.size();i++){
                System.out.println("utente"+user.get(i).getUsername()+"id"+user.get(i));
            }
            System.out.println("ricerca finita di:"+query);

            //chiama l'adattatore che inserisce gli item nella listview
            final SearchAdapter adapter =new SearchAdapter(getBaseContext(),user);
            listUser.setAdapter(adapter);

            //se clicco su un user mi apre il suo profilo
            listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                    i.putExtra("user", adapter.getItem(position).getUsername());
                    startActivity(i);
                    finish();
                }
            });

            //allungo l'altezza della list view
            //setListViewHeightBasedOnItems(listView);
        }
    }

    //funzione di ausilio per una nuova ricerca
    public void research(String query){

        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query=query.trim();

        //use the query to search
        View v=(View)findViewById(R.id.searchview);

        //prendo la listview e la rootView
        RelativeLayout rootView=(RelativeLayout)findViewById(R.id.searchview);
        ListView listView=(ListView)findViewById(R.id.user_find);

        //faccio la query a Parse
        List<User> user= SearchUtility.searchUser(query,rootView);

        for(int i=0;i<user.size();i++){
            System.out.println("utente"+user.get(i).getUsername()+"id"+user.get(i));
        }
        System.out.println("ricerca finita di:"+query);

        //chiama l'adattatore che inserisce gli item nella listview
        SearchAdapter adapter =new SearchAdapter(getBaseContext(),user);
        listView.setAdapter(adapter);

        //allungo l'altezza della list view
        //setListViewHeightBasedOnItems(listView);
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
