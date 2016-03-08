package com.clothapp.upload;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.home.HomeActivity;
import com.clothapp.http.Get;
import com.clothapp.resources.BitmapUtil;
import com.clothapp.resources.Cloth;
import com.google.android.gms.common.ConnectionResult;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.graphics.BitmapCompat.getAllocationByteCount;
import static com.clothapp.resources.ExceptionCheck.check;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.location.places.Places;

public class UploadPhotoActivity extends AppCompatActivity implements OnConnectionFailedListener {

    private final int REQUEST_CAMERA = 101;

    static final int RESULT_LOAD_IMG = 1540;
    static final int CAPTURE_IMAGE_ACTIVITY = 2187;

    // ATTENZIONE Roberto! Possibili spoiler su Star Wars VII

    // Qui si apre un piccolo excursus: perchè CAPRUTE_IMAGE_ACTIVITY è settato a 2187 ? FN2187 è il numero di serie dell'assolatore
    // del personaggio di Finn nell'ultimo Star Wars episodio VII prima di diventare un dei "buoni"
    // inoltre 2187 corrisponde anche al numero di cella dove è stata rinchiusa la pricipessa Leia dopo essere stata catturata
    // da Dart Vather in una delle prima scene di Star Wars episodio IV

    /* --------------------------------------- */
    boolean first = true;
    static final String directoryName = "ClothApp";
    private static String photoFileName = new SimpleDateFormat("'IMG_'yyyyMMdd_hhmmss'.jpg'", Locale.US).format(new Date());
    Uri takenPhotoUri;
    ImageView imageView = null;
    private static Bitmap imageBitmap = null;
    static int photoType;
    /* --------------------------------------- */

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private static GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_photo);

        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();



        // Controllo se ci sono savedIstance: se ce ne sono vuol dire che questa non activity era già stata creata e stoppata a causa
        // dell'apertura della fotocamera
        if (savedInstanceState != null) {
            first = savedInstanceState.getBoolean("first");
            photoFileName = savedInstanceState.getString("photoFileName");

            //Log.d("UploadActivity", "First è false, quindi non avvia la fotocamera");
            // Inizializzo parse perchè l'activity è stata chiusa
        }

        if (first) {
            //controllo da dove andare a prendere la foto galleria/camera
            photoType = getIntent().getIntExtra("photoType", 0);
            if (photoType == CAPTURE_IMAGE_ACTIVITY) {
                // Non faccio direttamente il controllo su savedIstance perchè magari in futuro potremmo passare altri parametri
                // questa è la prima volta che questa activity viene aperta, quindi richiamo direttamente la fotocamera
                //Log.d("UploadCamera", "E' il first");

                // Creo un intent specificando che voglio un'immagine full size e il nome dell'uri dell'immagine
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                // TODO: Check if getPhotoFileUri returns null

                // Set the image file name
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA);
                }

                try {
                    // If fintanto che il resolveActivity di quell'intent non è null significa che la foto non è ancora stata scattata e
                    // quindi devo chiamare la fotocamera
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        // Starto l'attivity di cattura della foto passandogli l'intent
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY);
                    }
                } catch (Exception e) {
                    //Log.d("UploadActivity", "Exception: " + e.getMessage());
                }
            } else if (photoType == RESULT_LOAD_IMG) {
                //inizializzo immagine da prendere in galleria
                //Log.d("UploadGallery", "E' il first");
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        }

        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    // Questa funzione serve a prendere la foto dopo che è stata scattata dalla fotocamera, e mette l'immagine nella ImageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("UploadActivity", "Siamo arrivati alla onActivityResult");
        //decodifico com bitmapfactory a 3
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;

        // Controllo che l'immagine sia stata catturata correttamente
        if (requestCode == CAPTURE_IMAGE_ACTIVITY && resultCode == RESULT_OK) {
            takenPhotoUri = getPhotoFileUri(photoFileName);

            // A questo punto l'immagine è stata salvata sullo storage
            imageBitmap = BitmapFactory.decodeFile(takenPhotoUri.getPath(), options);

            // Inserisco l'immagine nel bitmap
            // Prima però controllo in che modo è stata scattata (rotazione)
            imageBitmap = BitmapUtil.rotateImageIfRequired(imageBitmap, takenPhotoUri);

        } else if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
            takenPhotoUri = data.getData();

            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(takenPhotoUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            imageBitmap = BitmapFactory.decodeFile(picturePath, options);

            imageBitmap = BitmapUtil.rotateGalleryImage(picturePath, imageBitmap);
        } else {
            // Errore della fotocamera
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

            Log.d("UploadActivity", "L'Immagine non è stata presa");

            // termino
            finish();
        }

        //imageView.setImageBitmap(BitmapUtil.scala(imageBitmap));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        
    }

    /**
     * A placeholder fragment containing a simple view.
     */
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
            View rootView;

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
                        AlertDialog.Builder back = new AlertDialog.Builder(getActivity());
                        back.setTitle(R.string.ask_back_from_upload);
                        back.setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //annullo l'upload
                                        if (photoType != RESULT_LOAD_IMG) deleteImage();
                                        getActivity().finish();
                                    }
                                });
                        back.setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //annullata
                                    }
                                });
                        AlertDialog dialogDelete = back.create();
                        // display dialog
                        dialogDelete.show();
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
                retake.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (photoType != RESULT_LOAD_IMG) deleteImage();
                        Intent intent = getActivity().getIntent();
                        getActivity().finish();
                        startActivity(intent);
                    }
                });
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
                Button previous = (Button) rootView.findViewById(R.id.previous);
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
                listView.setAdapter(infoListAdapter);
                //listener bottone add clothing
                Button add = (Button) rootView.findViewById(R.id.add);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        infoListAdapter.addCard();
                        infoListAdapter.notifyDataSetChanged();
                        setListViewHeightBasedOnItems(listView);

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
                Button upload = (Button) rootView.findViewById(R.id.upload);
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO: aggiungere le operazioni di upload
                        System.out.println(sectionsPagerAdapter.getDescription() + ":" + sectionsPagerAdapter.getHashtag() + ":");
                        infoListAdapter.notifyDataSetChanged();
                        System.out.println(infoListAdapter.getListCloth());
                        ArrayList<String> id = new ArrayList<>();
                        for (int i = 0; i < infoListAdapter.getCount(); i++) {
                            Cloth c = infoListAdapter.getItem(i);
                            if (!c.isEmpty()) {
                                final ParseObject vestito = new ParseObject("Vestito");
                                if (c.getCloth() != null) vestito.put("tipo", c.getCloth());
                                if (c.getShop() != null) vestito.put("shop", c.getShop());
                                if (c.getBrand() != null) vestito.put("brand", c.getBrand());
                                if (c.getPrice() != null) vestito.put("prezzo", c.getPrice());
                                if (c.getAddress()!=null) vestito.put("luogoAcquisto",c.getAddress());
                                try {
                                    vestito.save();
                                    //se lo shop è registrato inserisco il nome nel campo shopusername
                                    ParseQuery<ParseObject> shop=new ParseQuery<ParseObject>("LocalShop");
                                    shop.whereEqualTo("name", c.getShop());
                                    shop.whereEqualTo("address",c.getAddress());
                                    System.out.println(c.getShop()+":"+c.getAddress()+":");
                                    shop.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject object, ParseException e) {
                                            if (e == null && object != null) {
                                                System.out.println("trovato"+object);
                                                vestito.put("shopUsername", object.getString("username"));
                                                try {
                                                    vestito.save();
                                                } catch (ParseException e1) {
                                                    e1.printStackTrace();
                                                }
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

                        //btnSend.setVisibility(View.INVISIBLE);
                        //progressBar.setVisibility(View.VISIBLE);
                        //percentuale.setVisibility(View.VISIBLE);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        // e la funzione a fine file checkToCompress()

                        int toCompress = BitmapUtil.checkToCompress(imageBitmap);
                        Log.d("UploadActivity", "toCompress = " + toCompress);

                        imageBitmap.compress(Bitmap.CompressFormat.JPEG, toCompress, stream);
                        byte[] byteImg = stream.toByteArray();
                        Log.d("UploadActivity", "Dimensione del file: " + getAllocationByteCount(imageBitmap));


                        // Creazione di un ParseFile
                        ParseFile file = new ParseFile(photoFileName, byteImg);

                        // Save the file to Parse
                        file.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("UploadActivity", "File inviato correttamente");
                                } else {
                                    // Chiamata ad altra classe per verificare qualsiasi tipo di errore dal server
                                    check(e.getCode(), vi, e.getMessage());
                                    Log.d("UploadActivity", "Errore durante l'invio del file");
                                }
                            }
                        }, new ProgressCallback() {
                            public void done(Integer percentDone) {
                                // Update your progress spinner here. percentDone will be between 0 and 100.
                                //percentuale.setText("Caricamento: " + percentDone + "%");
                                //progressBar.setProgress(percentDone);
                            }
                        });

                        // Creazione di un ParseObject da inviare
                        final ParseObject picture = new ParseObject("Photo");
                        picture.put("user", ParseUser.getCurrentUser().getUsername());
                        picture.put("photo", file);
                        String[] hashtags = sectionsPagerAdapter.getHashtag().split(" ");
                        picture.put("hashtag", Arrays.asList(hashtags));
                        picture.put("nLike", 0);
                        picture.put("vestiti", id);

                        // Invio ParseObject (immagine) al server
                        picture.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("UploadActivity", "Oggetto immagine inviato correttamente");

                                    //chiamata get per salvare il thumbnail
                                    String url = "http://clothapp.parseapp.com/createthumbnail/" + picture.getObjectId();
                                    Get g = new Get();
                                    g.execute(url);

                                    getActivity().finish();
                                } else {
                                    // Chiama ad altra classe per verificare qualsiasi tipo di errore dal server
                                    check(e.getCode(), vi, e.getMessage());

                                    Log.d("UploadActivity", "Errore durante l'invio dell'oggetto immagine");
                                }
                            }
                        });

                    }
                    /*View view=infoListAdapter.getItem(i);
                    System.out.println(view);
                    AutoCompleteTextView tipo=(AutoCompleteTextView)view.findViewById(R.id.cloth);
                    EditText shop=(EditText)view.findViewById(R.id.shop);
                    EditText brand=(EditText)view.findViewById(R.id.brand);
                    EditText address=(EditText)view.findViewById(R.id.address);
                    EditText price=(EditText)view.findViewById(R.id.price);
                    Cloth c=new Cloth();
                    c.setCloth(tipo.getText().toString());
                    c.setShop(shop.getText().toString());
                    c.setBrand(brand.getText().toString());
                    c.setAddress(address.getText().toString());
                    if(!price.getText().toString().equals("")) c.setPrize(Float.parseFloat(price.getText().toString()));
                    System.out.println(c);
*/


                });


            }
            // TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            // textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
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


    private void startCamera() {
        // Creo un intent specificando che voglio un'immagine full size e il nome dell'uri dell'immagine
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // TODO: Check if getPhotoFileUri returns null

        // Set the image file name
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If fintanto che il resolveActivity di quell'intent non è null significa che la foto non è ancora stata scattata e
        // quindi devo chiamare la fotocamera
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Starto l'attivity di cattura della foto passandogli l'intent
            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!

                    startCamera();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
        }
    }

    //funzione che cancella l'imamgine scattata
    public static void deleteImage() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + directoryName;
        File f = new File(path, photoFileName);
        // Controllo se esiste
        if (f.exists() && !f.isDirectory()) {
            // Se esiste lo elimino
            f.delete();
            Log.d("UploadActivity", "File eliminato");
        }
    }

    // In caso sia premuto il pulsante indietro, eliminiamo l'immagine creata e torniamo alla home activity
    @Override
    public void onBackPressed() {
        AlertDialog.Builder back = new AlertDialog.Builder(this);
        back.setTitle(R.string.ask_back_from_upload);
        back.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //annullo l'upload
                        if (photoType != RESULT_LOAD_IMG) deleteImage();
                        finish();
                    }
                });
        back.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //annullata
                    }
                });
        AlertDialog dialogDelete = back.create();
        // display dialog
        dialogDelete.show();
    }

    // Ritorna l'Uri dell'immagine su disco
    public Uri getPhotoFileUri(String fileName) {
        // Continua solamente se la memoria SD è montata
        if (isExternalStorageAvailable()) {

            // Get safe storage directory for photos
            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), directoryName);

            // Creo la directory di storage se non esiste
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d("UploadActivity", "Impossibile creare cartella");
            }

            // Ritorna l'uri alla foto in base al fileName
            return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
        }
        return null;
    }

    // Funzione per controllare che lo storage esterno sia disponibile
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();

        return state.equals(Environment.MEDIA_MOUNTED);
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
