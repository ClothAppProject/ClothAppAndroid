package com.clothapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.home.MostRecentAdapter;
import com.clothapp.home.MostRecentFragment;
import com.clothapp.home.TopRatedAdapter;
import com.clothapp.home_gallery.HomeMostRecentFragment;
import com.clothapp.home_gallery.HomeTopRatedFragment;
import com.clothapp.profile.UserProfileActivity;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.profile.fragments.ProfileUploadedPhotosFragment;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.resources.MyCardListAdapter;
import com.clothapp.search.FindClothFragment;
import com.clothapp.search.FindTagFragment;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class ImageFragment extends AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private boolean load=true;
    private int n;
    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;
    static ArrayList<Image> lista;
    private String classe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classe = getIntent().getStringExtra("classe");
        //setto pulsante indietro
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switch (classe)   {
            case "MostRecentPhotos":
                lista = (ArrayList<Image>) MostRecentAdapter.itemList;
                break;

            case "TopRatedPhotos":
                lista = (ArrayList<Image>) TopRatedAdapter.itemList;
                break;

            case "profilo":
                lista = (ArrayList<Image>) ProfileUploadedPhotosAdapter.photos;
                break;

            case "FindCloth":
                lista = FindClothFragment.getCloth();
                break;

            case "FindTag":
                lista = FindTagFragment.getCloth();
                break;
        }
        setContentView(R.layout.fragment_image);
        mPager = (ViewPager) findViewById(R.id.pager);



        //creo adattatore da pasare al ViewPager
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        // Set the current item based on the extra passed in to this activity
        final int extraCurrentItem = getIntent().getIntExtra("position", -1);
        if (extraCurrentItem != -1) {
            mPager.setCurrentItem(extraCurrentItem);
        }
    }
    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //se ultima o penultima foto della lista, carico altra foto
            if (position>=lista.size()-2)   {
                addPhotoToEnd();
            }
            return new ImageDetailFragment().newInstance(lista.get(position),getApplicationContext());
        }

        @Override
        public int getCount() {
            return lista.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante sulla toolbar
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addPhotoToEnd() {
        //TODO bisogna impostare il controllo sulle liste quando si scarica
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        switch (classe) {
            case "MostRecentPhotos":
                query.setSkip(lista.size());
                query.orderByDescending("createdAt");
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject object, ParseException e) {
                        if (object!=null) {
                            object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                            object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                            object.getList("vestiti"), object.getList("tipo"));
                                    lista.add(toAdd);
                                    mPagerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
                break;
            case "TopRatedPhotos":
                query.setSkip(lista.size());
                query.orderByDescending("nLike");
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject object, ParseException e) {
                        if (object!=null) {
                            if (!lista.contains(new Image(null,object.getObjectId(),null,null,0,null,null,null))) {
                                object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                    @Override
                                    public void done(File file, ParseException e) {
                                        Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                                object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                                object.getList("vestiti"), object.getList("tipo"));
                                        lista.add(toAdd);
                                        mPagerAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                });
                break;
            case "profilo":
                query.setSkip(lista.size());
                query.whereEqualTo("user",lista.get(0).getUser());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject object, ParseException e) {
                        if (object!=null) {
                            object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                            object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                            object.getList("vestiti"), object.getList("tipo"));
                                    lista.add(toAdd);
                                    mPagerAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                });
                break;
        }
    }
}