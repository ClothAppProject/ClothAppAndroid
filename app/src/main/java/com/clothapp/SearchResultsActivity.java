package com.clothapp;

/**
 * Created by nc94 on 2/15/16.
 */
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;

import com.clothapp.resources.SearchUtility;
import com.clothapp.resources.SearchUtility;
import com.clothapp.resources.User;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class SearchResultsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_appbar, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search
            View v=(View)findViewById(R.id.searchview);

            TextView t=(TextView)findViewById(R.id.user_find);
            List<User> results=SearchUtility.searchUser(query, v);
            ListIterator<User> i=results.listIterator();
           String res="niente";
            while(i.hasNext()){
           res=res.concat(i.next().getUsername()+"\n");
        }
      t.setText(res);
         }
    }}
