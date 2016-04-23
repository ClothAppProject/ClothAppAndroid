package com.clothapp.image_detail;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.clothapp.R;
import com.clothapp.home.MostRecentAdapter;
import com.clothapp.home.TopRatedAdapter;
import com.clothapp.profile.adapters.ProfileUploadedPhotosAdapter;
import com.clothapp.resources.Image;
import com.clothapp.search.FindClothFragment;
import com.clothapp.search.FindTagFragment;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;

public class ImageActivity extends AppCompatActivity {
    /**
     * The number of pages (wizard steps) to show in this demo.
     */

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;
    private boolean load = true;
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

        switch (classe) {
            case "MostRecentPhotos":
                lista = (ArrayList<Image>) MostRecentAdapter.itemList;
                break;

            case "TopRatedPhotos":
                lista = (ArrayList<Image>) TopRatedAdapter.itemList;
                break;

            case "profilo":
                ProfileUploadedPhotosAdapter adattatore = (ProfileUploadedPhotosAdapter) getIntent().getParcelableExtra("photo");
                lista = (ArrayList<Image>) adattatore.photos;
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
            if (position >= lista.size() - 2) {
                addPhotoToEnd();
            }
            return new ImageDetailFragment().newInstance(lista.get(position), getApplicationContext());
        }

        @Override
        public int getCount() {
            if (lista == null || lista.isEmpty()) return 0;
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
                        if (object != null) {
                            object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                            object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                            object.getList("vestiti"), object.getList("tipo"));
                                    if (!lista.contains(toAdd)) {
                                        lista.add(toAdd);
                                        mPagerAdapter.notifyDataSetChanged();
                                    }
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
                        if (object != null) {
                            if (!lista.contains(new Image(null, object.getObjectId(), null, null, 0, null, null, null))) {
                                object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                    @Override
                                    public void done(File file, ParseException e) {
                                        Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                                object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                                object.getList("vestiti"), object.getList("tipo"));
                                        if (!lista.contains(toAdd)) {
                                            lista.add(toAdd);
                                            mPagerAdapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
                break;
            case "profilo":
                query.setSkip(lista.size());
                query.whereEqualTo("user", lista.get(0).getUser());
                query.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(final ParseObject object, ParseException e) {
                        if (object != null) {
                            object.getParseFile("thumbnail").getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    Image toAdd = new Image(file, object.getObjectId(), object.getString("user"),
                                            object.getList("like"), object.getInt("nLike"), object.getList("hashtag"),
                                            object.getList("vestiti"), object.getList("tipo"));
                                    if (!lista.contains(toAdd)) {
                                        lista.add(toAdd);
                                        mPagerAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
                break;
        }
    }
}