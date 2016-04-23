package com.clothapp.image_detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.home.MostRecentAdapter;
import com.clothapp.home.MostRecentFragment;
import com.clothapp.http.Get;
import com.clothapp.parse.notifications.NotificationsUtils;
import com.clothapp.resources.BitmapUtil;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.settings.UserSettingsUtil;
import com.clothapp.upload.InfoListAdapter;
import com.clothapp.upload.UploadPhotoActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.support.v4.graphics.BitmapCompat.getAllocationByteCount;
import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 21/04/16.
 */
public class EditImageActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static Image immagine;
    private static ParseObject parseImmagine;
    private static ArrayList<ParseObject> parseVestiti;
    private static ArrayList<Cloth> vestiti;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    Uri takenPhotoUri;
    private static GoogleApiClient mGoogleApiClient;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);
        id = getIntent().getStringExtra("objectId");
        System.out.println("id="+id);

        vestiti=new ArrayList<>();
        parseVestiti=new ArrayList<>();
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        queryFoto.whereEqualTo("objectId",id);
        queryFoto.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null && object!=null) {
                    parseImmagine=object;
                    immagine = new Image(object);
                    for (String s : (ArrayList<String>) object.get("vestiti")) {
                        ParseQuery<ParseObject> queryVestiti = new ParseQuery<ParseObject>("Vestito");
                        queryVestiti.whereEqualTo("objectId", s);
                        try {
                            ParseObject object1= queryVestiti.getFirst();
                            vestiti.add(new Cloth(object1));
                            parseVestiti.add(object1);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }



                    }
                    takenPhotoUri=Uri.fromFile(immagine.getFile());
                    // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    // setSupportActionBar(toolbar);
                    // Create the adapter that will return a fragment for each of the three
                    // primary sections of the activity.
                    mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                    // Set up the ViewPager with the sections adapter.
                    mViewPager = (ViewPager) findViewById(R.id.container);
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                    mViewPager.setOffscreenPageLimit(3);
                    System.out.println(immagine.getFile());
                }
                else if(object==null) System.out.println("oggetto null");
                else System.out.println("errore");
            }
        });



        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        PlaceholderFragment page1;
        PlaceholderFragment page2;
        PlaceholderFragment page3;

        String hashtag;
        private String description;


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 0) {
                if (page1 == null)
                    page1 = PlaceholderFragment.newInstance(position + 1, takenPhotoUri, this);
                return page1;
            }
            if (position == 1) {
                if (page2 == null)
                    page2 = PlaceholderFragment.newInstance(position + 1, takenPhotoUri, this);
                return page2;
            }
            if (position == 2) {
                if (page3 == null)
                    page3 = PlaceholderFragment.newInstance(position + 1, takenPhotoUri, this);
                return page3;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }

        public String getHashtag() {
            return hashtag;
        }

        public void setHashtag(String hashtag) {
            this.hashtag = hashtag;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }



    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private EditText hashtag;
        private EditText description;


        public void setSectionsPagerAdapter(SectionsPagerAdapter sectionsPagerAdapter) {
            this.sectionsPagerAdapter = sectionsPagerAdapter;
        }

        SectionsPagerAdapter sectionsPagerAdapter;


        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber, Uri uri, SectionsPagerAdapter sectionsPagerAdapter) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putParcelable("uri", uri);
            fragment.setSectionsPagerAdapter(sectionsPagerAdapter);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void setUserVisibleHint(boolean isVisibleToUser) {
            super.setUserVisibleHint(isVisibleToUser);
            if (isVisibleToUser) {
                //System.out.println("visibile");
            } else {
                //System.out.println("nonvisibile");
                if (hashtag != null && sectionsPagerAdapter != null)
                    sectionsPagerAdapter.setHashtag(hashtag.getText().toString());
                if (description != null && sectionsPagerAdapter != null)
                    sectionsPagerAdapter.setDescription(description.getText().toString());
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
            final View rootView;

            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            Uri uri = (Uri) getArguments().getParcelable("uri");

            //fragment 1
            if (sectionNumber == 1) {
                rootView = inflater.inflate(R.layout.fragment_upload_photo_page_1, container, false);
                //listener sul pulsante annulla
                Button cancel = (Button) rootView.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
                //listener sul bottone next
                Button next = (Button) rootView.findViewById(R.id.next);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager viewPager = (ViewPager) container.findViewById(R.id.container);
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                });
                Button retake = (Button) rootView.findViewById(R.id.retake);
                retake.setVisibility(View.INVISIBLE);
                ImageView foto = (ImageView) rootView.findViewById(R.id.imageView);
                Glide.with(getActivity().getApplicationContext())
                        .load(uri)
                        .placeholder(R.mipmap.gallery_icon)
                        .into(foto);


                //fragment 2
            } else if (sectionNumber == 2) {
                rootView = inflater.inflate(R.layout.fragment_upload_photo_page_2, container, false);
                ImageView foto = (ImageView) rootView.findViewById(R.id.fragment_upload_photo_page_2_thumbnail);
                Glide.with(getActivity().getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.2f)
                        .placeholder(R.mipmap.gallery_icon)
                        .into(foto);

                //listener bottone previous
                Button previous = (Button) rootView.findViewById(R.id.previous);
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager viewPager = (ViewPager) container.findViewById(R.id.container);
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                });

                description = (EditText) rootView.findViewById(R.id.description);
                hashtag = (EditText) rootView.findViewById(R.id.hashtag);
                hashtag.setText(immagine.getHashtagToString());


                //listener bottone next
                Button next = (Button) rootView.findViewById(R.id.next);
                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sectionsPagerAdapter.setHashtag(hashtag.getText().toString());
                        ViewPager viewPager = (ViewPager) container.findViewById(R.id.container);
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    }
                });


                //fragment 3
            } else {
                rootView = inflater.inflate(R.layout.fragment_upload_photo_page_3, container, false);
                ImageView foto = (ImageView) rootView.findViewById(R.id.fragment_upload_photo_page_3_thumbnail);
                Glide.with(getActivity().getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .thumbnail(0.2f)
                        .placeholder(R.mipmap.gallery_icon)
                        .into(foto);
                final ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.fragment_upload_photo_page_3_scrollview);

                //listener bottone previous
                final Button previous = (Button) rootView.findViewById(R.id.previous);
                previous.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewPager viewPager = (ViewPager) container.findViewById(R.id.container);
                        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                    }
                });
                //listview delle card per aggiungere le info dei vestiti
                final ListView listView = (ListView) rootView.findViewById(R.id.listView);
                final InfoListAdapter infoListAdapter = new InfoListAdapter(getContext(),mGoogleApiClient);
                infoListAdapter.setCard(vestiti);
                setListViewHeightBasedOnItems(listView);
                listView.setAdapter(infoListAdapter);
                setListViewHeightBasedOnItems(listView);
                //listener bottone add clothing
                final Button add = (Button) rootView.findViewById(R.id.add);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infoListAdapter.addCard();
                        setListViewHeightBasedOnItems(listView);
                        //infoListAdapter.notifyDataSetChanged();


                    }
                });
                //listener bottone remove clothing
                Button remove = (Button) rootView.findViewById(R.id.remove);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infoListAdapter.deleteCard();
                        infoListAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(listView);

                    }
                });
                //listener bottone upload
                final Button upload = (Button) rootView.findViewById(R.id.upload);
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        System.out.println("onClick");
                        //nascondo pulsanti
                        boolean cloth_isPresent = true;
                        boolean cloth_isInserted=false;

                        for (int i = 0; i < infoListAdapter.getCount(); i++) {
                            Cloth c = infoListAdapter.getItem(i);
                            System.out.println("address "+c.getAddress());
                            System.out.println("insert "+cloth_isInserted);
                            if (c.getCloth() == "" && !c.isEmpty())
                                cloth_isPresent = false;
                            if(!c.isEmpty()) cloth_isInserted=true;
                        }
                        System.out.println("cloth_isInsert "+cloth_isInserted);
                        if(!cloth_isInserted){
                            Snackbar.make(getView(),"Aggiungi almeno un vestito ", Snackbar.LENGTH_LONG).show();
                            infoListAdapter.notifyDataSetChanged();
                        }
                        else {
                            if (!cloth_isPresent) {
                                Snackbar.make(getView(), "Il nome del vestito non può essere vuoto", Snackbar.LENGTH_LONG).show();
                            }
                            else {
                                upload.setVisibility(View.INVISIBLE);
                                previous.setVisibility(View.INVISIBLE);
                                rootView.findViewById(R.id.fragment_upload_photo_page_1_indicator_center).setVisibility(View.INVISIBLE);
                                rootView.findViewById(R.id.fragment_upload_photo_page_1_indicator_left).setVisibility(View.INVISIBLE);
                                rootView.findViewById(R.id.fragment_upload_photo_page_1_indicator_right).setVisibility(View.INVISIBLE);
                                final TextView percentuale = (TextView) rootView.findViewById(R.id.percentuale);
                                percentuale.setVisibility(View.VISIBLE);
                                percentuale.setText("Caricamento: 0%");
                                final ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
                                progressBar.setVisibility(View.VISIBLE);

                                System.out.println(sectionsPagerAdapter.getDescription() + ":" + sectionsPagerAdapter.getHashtag() + ":");
                                infoListAdapter.notifyDataSetChanged();

                                //cancello le vecchie informazioni dal db
                                parseImmagine.put("vestiti", new ArrayList<String>());
                                try {
                                    parseImmagine.save();
                                    for(ParseObject o:parseVestiti){
                                        o.delete();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }


                                final ArrayList<String> tipi = new ArrayList<String>();
                                final ArrayList<String> id = new ArrayList<>();
                                for (int i = 0; i < infoListAdapter.getCount(); i++) {
                                    Cloth c = infoListAdapter.getItem(i);
                                    if (!c.isEmpty()) {
                                        final ParseObject vestito = new ParseObject("Vestito");
                                        if (c.getCloth() != null) {
                                            vestito.put("tipo", c.getCloth());
                                            tipi.add(c.getCloth());
                                        }
                                        if (c.getShop() != null) vestito.put("shop", c.getShop());
                                        if (c.getBrand() != null)
                                            vestito.put("brand", c.getBrand());
                                        if (c.getPrice() != null)
                                            vestito.put("prezzo", c.getPrice());
                                        if (c.getAddress() != null)
                                            vestito.put("luogoAcquisto", c.getAddress());
                                        try {
                                            vestito.save();
                                            //se lo shop è registrato inserisco il nome nel campo shopusername
                                            ParseQuery<ParseObject> address = ParseQuery.getQuery("LocalShop");
                                            address.whereEqualTo("address", c.getAddress());
                                            address.whereEqualTo("username", c.getShop().trim());

                                            final ParseQuery<ParseObject> website = ParseQuery.getQuery("LocalShop");
                                            website.whereEqualTo("webSite", c.getAddress());
                                            website.whereEqualTo("username", c.getShop().trim());

                                            address.getFirstInBackground(new GetCallback<ParseObject>() {
                                                @Override
                                                public void done(ParseObject object, ParseException e) {
                                                    if (e == null && object != null) {
                                                        //System.out.println("debug: trovato address " + object);
                                                        vestito.put("shopUsername", object.getString("username"));
                                                        try {
                                                            vestito.save();
                                                        } catch (ParseException e1) {
                                                            e1.printStackTrace();
                                                        }
                                                    } else if (e.getCode() == 101) {
                                                        //System.out.println("debug codice 101 oggetto con address non trovato");
                                                        website.getFirstInBackground(new GetCallback<ParseObject>() {
                                                            @Override
                                                            public void done(ParseObject object, ParseException e) {
                                                                if (e == null && object != null) {
                                                                    vestito.put("shopUsername", object.getString("username"));

                                                                    try {
                                                                        vestito.save();
                                                                    } catch (ParseException e1) {
                                                                        e1.printStackTrace();
                                                                    }

                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        id.add(vestito.getObjectId());
                                    }
                                }

                                final View vi = v;


                                parseImmagine.put("vestiti",id);
                                final String[] hashtags = sectionsPagerAdapter.getHashtag().split(" ");
                                parseImmagine.put("hashtag",Arrays.asList(hashtags));
                                try {
                                    parseImmagine.save();
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                getActivity().finish();


                            }
                        }
                    }



                });


            }
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
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
