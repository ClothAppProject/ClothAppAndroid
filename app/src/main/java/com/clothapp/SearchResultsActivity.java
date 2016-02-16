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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            ListView listView=(ListView)findViewById(R.id.user_find);

            //faccio la query a Parse
            List<User> user= SearchUtility.searchUser(query,rootView);

            for(int i=0;i<user.size();i++){
                System.out.println(user.get(i).getUsername());
            }
            System.out.println("ricerca finita di:"+query);

            //chiama l'adattatore che inserisce gli item nella listview
            SearchAdapter adapter =new SearchAdapter(getBaseContext(),user);
            listView.setAdapter(adapter);

            //allungo l'altezza della list view
            setListViewHeightBasedOnItems(listView);
        }
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
