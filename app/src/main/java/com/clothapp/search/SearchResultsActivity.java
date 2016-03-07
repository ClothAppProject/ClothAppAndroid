package com.clothapp.search;

/**
 * Created by nc94 on 2/15/16.
 */
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import com.clothapp.R;
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
    private String sex="all";
    private Float pricefrom= -1f;
    private Float priceto=-1f;

    private String[] titles;
    private SearchAdapter searchAdapter;
    private String order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //setto la toolbar
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        final ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setElevation(0);


        titles = getResources().getStringArray(R.array.search_titles);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        //gestisco gli intent
        query= handleIntent(getIntent());
        System.out.println("la query è "+query);
        if(query==null)query="";
        System.out.println("invio sex:"+sex);

        //set adapter to  ViewPager
        searchAdapter=new SearchAdapter(getSupportFragmentManager(), titles ,query, getApplicationContext(),sex,pricefrom,priceto,order);
        viewPager.setAdapter(searchAdapter);
        //viewPager.setCurrentItem(1);
        //viewPager.setCurrentItem(1);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        //Log.d("SearchA","oncreate");


    }

//aggiungo le icone del menu all'appbar
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater inflater = getMenuInflater();
        //inflater.inflate(R.menu.home_appbar, menu);

        getMenuInflater().inflate(R.menu.searchbar, menu);


        final MenuItem searchItem = menu.findItem(R.id.action_search);

        searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);


/*
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                return false;
            }
        });
*/
        //quando faccio una nuova ricerca aggiorno questa activity invece di crearne una nuova
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query1) {
                System.out.println("nuova query");
                query=query1;
                searchAdapter.setQuery(query1);
                searchAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                return false;
            }
        });



        // Define the listener il listener sulla searchview
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
        MenuItem actionMenuItem = menu.findItem(R.id.action_search);

        // Assign the listener to that action item
        MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);

        // Any other things you have to do when creating the options menu…

        return true;
    }

    //quando un item nell'appbar viene selezionato
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante indietro termino semplicemente l'activity
            case android.R.id.home:
                onBackPressed();
                break;
            //premuto settings
            case R.id.action_settings:
                Intent i=new Intent(getBaseContext(),SettingsActivity.class);
                startActivity(i);
                break;
            //premuto filter
            case R.id.filter:
                Intent j=new Intent(getBaseContext(),FilterActivity.class);
                j.putExtra("query",query);
                startActivity(j);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

//gestisco gli intent
    private String handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            return intent.getStringExtra(SearchManager.QUERY);
        }
        if(intent.getStringExtra("query")!=null){
            System.out.println("intent:"+intent.getStringExtra("sex"));
            sex = intent.getStringExtra("sex");
            pricefrom=intent.getFloatExtra("prezzoDa", 0);
            priceto=intent.getFloatExtra("prezzoA",Float.MAX_VALUE);
            order=intent.getStringExtra("order");
            return intent.getStringExtra("query");
        }
        return null;
    }



}
