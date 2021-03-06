package com.clothapp.image_detail;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clothapp.R;
import com.clothapp.http.Get;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.ExceptionCheck;
import com.clothapp.resources.Image;
import com.clothapp.parse.notifications.LikeRes;
import com.clothapp.resources.User;
import com.clothapp.search.SearchAdapterUser;
import com.clothapp.upload.UploadPhotoActivity;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

//import uk.co.senab.photoview.PhotoViewAttacher;

import static com.clothapp.resources.ExceptionCheck.check;

public class ImageDetailFragment extends Fragment {

    private ImageView imageView;
    private ImageView heartAnim;
    private TextView t;
    private String Id;
    private Image immagine;
    private static Context context;
    private ArrayList<Cloth> vestiti;
    private List<User> likeList;
    private SearchAdapterUser likeAdapter;
    private boolean canLoad = false;
    private ProgressBar progressBar;
    private ListView listView;
    private TextView hashtag;
    private ImageView person;
    private ImageView share;
    private ImageView cuore;
    private TextView like;
    private ArcProgress percentuale;
    private ImageView profilePic;
    private ParseObject parseObject;
    private ViewGroup rootView;

    //private PhotoViewAttacher mAttacher;

    public ImageDetailFragment newInstance(Image image, Context c) {
        final ImageDetailFragment f = new ImageDetailFragment();
        f.immagine = image;
        f.context = c;
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t = (TextView) rootView.findViewById(R.id.user);
        imageView = (ImageView) rootView.findViewById(R.id.photo);
        heartAnim = (ImageView) rootView.findViewById(R.id.heart_anim);
        listView = (ListView) rootView.findViewById(R.id.listInfo);
        hashtag = (TextView) rootView.findViewById(R.id.hashtag);
        person = (ImageView) rootView.findViewById(R.id.person);
        share = (ImageView) rootView.findViewById(R.id.share);
        cuore = (ImageView) rootView.findViewById(R.id.heart);
        like = (TextView) rootView.findViewById(R.id.like);
        profilePic = (ImageView) rootView.findViewById(R.id.pic);
        percentuale = (ArcProgress) rootView.findViewById(R.id.bar_percentuale);
        //percentuale.setBottomText(getResources().getString(R.string.load));
        percentuale.setTextColor(getResources().getColor(R.color.background));
        percentuale.setUnfinishedStrokeColor(getResources().getColor(R.color.accentred));
        percentuale.setFinishedStrokeColor(getResources().getColor(R.color.darkred));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (immagine!=null) {
            //setto il listener sull'icona persona
            person.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileUtils.goToProfile(getActivity().getApplicationContext(), immagine.getUser());

                }
            });

            ParseQuery<ParseObject> queryFoto = new ParseQuery<>("UserPhoto");
            queryFoto.whereEqualTo("username", immagine.getUser());
            queryFoto.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        ParseFile profile_img = object.getParseFile("thumbnail");
                        if (profile_img == null || profile_img.getUrl() == null) {
                            //if thumbnail not already created
                            profile_img = object.getParseFile("profilePhoto");

                            //chiamata get per salvare il thumbnail
                            String url = "http://clothapp.westeurope.cloudapp.azure.com/createprofilethumbnail/"+object.getObjectId();
                            Get g = new Get();
                            g.execute(url);
                        }
                        Glide.with(context)
                                .load(profile_img.getUrl())
                                .placeholder(R.drawable.com_facebook_profile_picture_blank_circle)
                                .centerCrop()
                                .transform(new CircleTransform(context))
                                .into(profilePic);
                    }
                }
            });


            //faccio query al database per scaricare la foto
            ParseQuery<ParseObject> query = new ParseQuery<>("Photo");
            query.whereEqualTo("objectId", immagine.getObjectId());
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject object, ParseException e) {
                    if (e == null) {
                        parseObject = object;

                        object.getParseFile("photo").getFileInBackground(new GetFileCallback() {
                            @Override
                            public void done(final File file, ParseException e) {
                                if (e == null) {
                                    //Gesture Detector for detecting double tap
                                    //code is at the end of page
                                    final GestureDetector gd = doubleTapGesture(file.toURI().toString());

                                    Glide.with(context)
                                            .load(file)
                                            .placeholder(R.mipmap.gallery_icon)
                                            .fitCenter()
                                            .into(imageView);
                                    imageView.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View v, MotionEvent event) {
                                            return gd.onTouchEvent(event);
                                        }
                                    });

                                /*
                                // The MAGIC happens here!
                                mAttacher = new PhotoViewAttacher(imageView);
*/

                                    //setto il listener sull'icona share
                                    share.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Bitmap icon = BitmapFactory.decodeFile(file.getPath());
                                            Intent share = new Intent(Intent.ACTION_SEND);
                                            share.setType("image/jpeg");
                                            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                            icon.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                                            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file.jpg");
                                            try {
                                                f.createNewFile();
                                                FileOutputStream fo = new FileOutputStream(f);
                                                fo.write(bytes.toByteArray());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temporary_file.jpg"));
                                            startActivity(Intent.createChooser(share, "Share Image"));
                                    /*
                                    Intent shareIntent = new Intent();
                                    shareIntent.setAction(Intent.ACTION_SEND);
                                    Log.d("Share", file.toURI().toString());
                                    //context.grantUriPermission (String.valueOf(contentUri), contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    shareIntent.putExtra(Intent.EXTRA_STREAM, file.toURI());
                                    shareIntent.setType("image/jpeg");
                                    shareIntent.setFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
                                    */
                                        }
                                    });

                                }
                            }
                        }, new ProgressCallback() {
                            @Override
                            public void done(Integer percentDone) {
                                //passo percentuale
                                if (percentDone == 100) {
                                    percentuale.setVisibility(View.INVISIBLE);
                                }
                                percentuale.setProgress(percentDone);
                            }
                        });
                    } else {
                        ExceptionCheck.check(e.getCode(), getView(), e.getMessage());
                    }
                }
            });

            //setto username e listener
            t.setText(immagine.getUser());
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileUtils.goToProfile(getActivity().getApplicationContext(), immagine.getUser());
                }
            });

            //listener on the profile pic
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProfileUtils.goToProfile(getActivity().getApplicationContext(), immagine.getUser());
                }
            });

            //setto gli hashtag
            hashtag.setText(immagine.getHashtagToString());

            //per ogni vestito cerco le informazioni
            List arrayList = immagine.getIdVestiti();
            System.out.println(arrayList+ " arraylist");
            if (arrayList == null) arrayList = new ArrayList<>();
            vestiti = new ArrayList<>(arrayList.size());
            synchronized (vestiti) {
                System.out.println(arrayList.size() + " array list size");
                for (int i = 0; i < arrayList.size(); i++) {
                    System.out.println(i + " i");
                    ParseQuery<ParseObject> query1 = new ParseQuery<>("Vestito");
                    query1.whereEqualTo("objectId", arrayList.get(i));
                    final int finalI = i;
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject info, ParseException e) {
                            if (e == null) {
                                System.out.println(info.getString("tipo") + " trovato");
                                Float fl = null;
                                if (info.get("prezzo") != null) {
                                    if (info.get("prezzo").getClass() != Float.class)
                                        fl = Float.parseFloat(info.get("prezzo").toString());
                                    else fl = (float) info.get("prezzo");
                                }
                                Cloth c = new Cloth(info.getString("tipo"),
                                        info.getString("luogoAcquisto"),
                                        fl,
                                        info.getString("shop"),
                                        info.getString("shopUsername"),
                                        info.getString("brand"));
                                c.setObjectId(info.getObjectId());
                                if (!isPresent(vestiti,c)) {
                                    System.out.println("aggiungo "+c);
                                    vestiti.add(c);
                                }else {
                                    System.out.println("no");
                                    System.out.println(c+" is in "+vestiti);
                                }
                                MyCardListAdapter adapter = new MyCardListAdapter(context, vestiti);
                                listView.setAdapter(adapter);
                                setListViewHeightBasedOnItems(listView);
                            }
                            else System.out.println("errore");
                        }
                    });
                }
            }

            //chiamo funzione del testo dei like
            setTextLike();

            //controllo se ho messo like sull'attuale foto
            final String username = ParseUser.getCurrentUser().getUsername();
            if (immagine.getLike().contains(username)) {
                cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
            } else {
                cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
            }
            //metto i listener sul cuore
            cuore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (immagine.getLike().contains(username)) {
                        //rimuovo il like chiamando deleteLike
                        LikeRes.deleteLike(immagine.getObjectId(), immagine, username);

                        cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
                    } else {
                        //aggiungo like chiamando addLike
                        LikeRes.addLike(immagine.getObjectId(), immagine, username);

                        cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
                    }
                    //  se ho zero likes scrivo like sennò likes
                    setTextLike();
                }
            });
        }
    }

    private boolean isPresent(ArrayList<Cloth> l,Cloth c){
        for(int i=0;i<l.size();i++){
            if(l.get(i).getObjectId().equals(c.getObjectId())) return true;
        }
        return false;
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        onActivityCreated(getArguments());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.image_fragment, menu);
        if (immagine != null) {
            menu.findItem(R.id.report).setVisible(!immagine.getUser().equals(ParseUser.getCurrentUser().getUsername()));
            menu.findItem(R.id.edit).setVisible(immagine.getUser().equals(ParseUser.getCurrentUser().getUsername()));
            menu.findItem(R.id.delete).setVisible(immagine.getUser().equals(ParseUser.getCurrentUser().getUsername()));
            menu.findItem(R.id.set_profile_picture).setVisible(immagine.getUser().equals(ParseUser.getCurrentUser().getUsername()));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // In caso sia premuto il pulsante sulla toolbar
            case R.id.report:
                //cliccato su segnala foto, creo dialog per segnalare foto
                AlertDialog.Builder report = new AlertDialog.Builder(getActivity());
                // Get the layout inflater
                LayoutInflater inflater = getActivity().getLayoutInflater();
                // Inflate and set the layout for the dialog
                View dialogView = inflater.inflate(R.layout.dialog_report, null);
                report.setView(dialogView);

                final EditText comment = (EditText) dialogView.findViewById(R.id.comment);
                final Spinner spinner = (Spinner) dialogView.findViewById(R.id.select_reason);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                        R.array.select_reason, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);
                // Add action buttons
                report.setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        ParseObject segnalazione = new ParseObject("Report");
                        segnalazione.put("comment", comment.getText().toString());
                        segnalazione.put("from_username", ParseUser.getCurrentUser().getUsername());
                        segnalazione.put("reason", spinner.getSelectedItem());
                        segnalazione.put("photo", immagine.getObjectId());
                        segnalazione.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    check(e.getCode(), getView(), e.getMessage());
                                } else {

                                    Toast.makeText(getActivity().getApplicationContext(), R.string.report_sent, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        dialog.dismiss();
                    }
                });
                report.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //segnalazione annullata
                    }
                });
                AlertDialog dialogReport = report.create();
                // display dialog
                dialogReport.show();
                return true;
            case R.id.delete:
                //cliccato su elimina foto, creo dialog per conferma elimina
                AlertDialog.Builder delete = new AlertDialog.Builder(getActivity());
                delete.setTitle(R.string.ask_photo_delete);
                delete.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //prendo i vestiti della foto e li elimino
                                if (parseObject.getList("vestiti") != null) {
                                    for (int i = 0; i < parseObject.getList("vestiti").size(); i++) {
                                        ParseObject vestito = ParseObject.createWithoutData("Vestito", parseObject.getList("vestiti").get(i).toString());
                                        vestito.deleteInBackground();
                                    }
                                }
                                //prendo la foto e la elimino
                                parseObject.deleteInBackground();
                                //la tolgo dalla lista passata all'image fragment
                                ImageActivity.lista.remove(immagine);
                                getActivity().finish();
                            }
                        });
                delete.setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //eliminazione foto annullata
                            }
                        });
                AlertDialog dialogDelete = delete.create();
                // display dialog
                dialogDelete.show();
                return true;
            case R.id.edit:
                //cliccato su "modifica foto" apro activity passandogli objectID della foto
                Intent i = new Intent(context, EditImageActivity.class);
                //System.out.println("id1="+immagine.getObjectId());
                i.putExtra("objectId", immagine.getObjectId());
                i.putExtra("image", immagine);
                startActivity(i);
                return true;
            case R.id.set_profile_picture:
                //cliccato su "Usa come immagine profilo" apro activity passandogli objectID della foto
                final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", "Imposto Immagine del Profilo...", true);
                ParseQuery<ParseObject> checkPhoto = new ParseQuery("UserPhoto");
                checkPhoto.whereEqualTo("username",ParseUser.getCurrentUser().getUsername());
                checkPhoto.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        if (e==null || e.getCode()==101) {
                            ParseObject profile_pic;
                            if (e == null) {
                                profile_pic = object;
                            } else {
                                profile_pic = new ParseObject("UserPhoto");
                            }
                            //get parseFiles from actual image
                            ParseQuery<ParseObject> getPhoto = new ParseQuery("Photo");
                            getPhoto.whereEqualTo("objectId",immagine.getObjectId());
                            try {
                                ParseObject oggetto = getPhoto.getFirst();
                                //save parameters
                                profile_pic.put("username",ParseUser.getCurrentUser().getUsername());
                                profile_pic.put("profilePhoto",oggetto.getParseFile("photo"));
                                profile_pic.put("thumbnail",oggetto.getParseFile("thumbnail"));
                                profile_pic.save();
                                Toast.makeText(getActivity(),"Immagine del Profilo impostata!",Toast.LENGTH_SHORT).show();
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                        }
                        dialog.dismiss();
                    }
                });
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            System.out.println(numberOfItems+" number item");
            for (itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                //View a=item.findViewById(R.id.address);
                //a.measure(0, 0);
                //int address=a.getMeasuredHeight();
                //System.out.println(address);
                //int shop=item.findViewById(R.id.shop).getMeasuredHeight();
                //int brand=item.findViewById(R.id.brand).getMeasuredHeight();
                //int price=item.findViewById(R.id.price).getMeasuredHeight();
                totalItemsHeight += item.getMeasuredHeight();
                System.out.println(totalItemsHeight+" totalhaight");
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

    private void setTextLike()  {
        if (immagine.getLike().isEmpty()) {
            like.setText(" 0 like");
        }else {
            final int numLike = immagine.getNumLike();
            String text;
            if (numLike>2)  {
                text = getString(R.string.he_likes) + " " + immagine.getLike().get(immagine.getLike().size()-1).toString()
                        + " " + getString(R.string.and_others) + " " + Integer.toString(numLike-1);
            }else if(numLike==2){
                text = getString(R.string.he_likes) + " " + immagine.getLike().get(0).toString()
            //            + " " + getString(R.string.and_others) + " " + Integer.toString(numLike-1);
                         + " e a un'altra persona";
            } else {
                text = Integer.toString(numLike) + " " + getString(R.string.likes);
            }
            like.setText(text);
            //listener che apre dialog per persone che hanno messo like
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder lista_like = new AlertDialog.Builder(getActivity());
                    // Get the layout inflater
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    // Inflate and set the layout for the dialog
                    final View dialogView = inflater.inflate(R.layout.dialog_like_list, null);
                    lista_like.setView(dialogView);

                    TextView numLikeText = (TextView) dialogView.findViewById(R.id.numLike);
                    numLikeText.setText(Integer.toString(numLike) + " " + (numLike == 0 || numLike == 1 ? "like" : "likes"));

                    ListView listLike = (ListView) dialogView.findViewById(R.id.lista_like);
                    progressBar = (ProgressBar) dialogView.findViewById(R.id.progressbar);
                    //chiama l'adattatore che inserisce gli item nella listview
                    likeList = new ArrayList<User>();
                    likeAdapter = new SearchAdapterUser(getActivity().getBaseContext(), likeList);
                    listLike.setAdapter(likeAdapter);
                    getLikeUserList();
                    listLike.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                                //se ho raggiunto l'ultima immagine in basso carico altre immagini
                                if (canLoad && likeList.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                                    if (likeList != null) {
                                        canLoad = false;
                                        //faccio la query a Parse
                                        getLikeUserList();
                                    }
                                }
                            }
                        }
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {
                        }
                    });

                    final AlertDialog dialog_like_list = lista_like.create();
                    // display dialog
                    dialog_like_list.show();

                    //listener su ogni persona
                    listLike.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ProfileUtils.goToProfile(getActivity().getApplicationContext(), likeAdapter.getItem(position).getUsername());
                            dialog_like_list.dismiss();
                        }
                    });

                    //listener on the close button of dialog
                    ImageView close_dialog = (ImageView) dialogView.findViewById(R.id.close_dialog);
                    close_dialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog_like_list.dismiss();
                        }
                    });
                }
            });
        }
    }

    //funzione per ottenere la lista di user dai like nell'immagine
    private void getLikeUserList()    {
        int size = likeList.size();
        int max;
        //controllo se posso caricarne altre 15 oppure solo fino alla fine
        if (size + 15>immagine.getLike().size()) max = immagine.getLike().size();
        else max = size+15;
        for (int i=size;i<max;i++) {
            final User u = new User(immagine.getLike().get(i).toString(),null,null);
            if (!likeList.contains(u)) {
                //aggiungiamo l'utente
                likeList.add(u);

                ParseQuery<ParseObject> queryLike = new ParseQuery<>("UserPhoto");
                queryLike.whereEqualTo("username", immagine.getLike().get(i));
                queryLike.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject object, ParseException e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        if (e == null) {
                            ParseFile f = object.getParseFile("thumbnail");
                            if (f == null || f.getUrl() == null) {
                                //if thumbnail not already created
                                f = object.getParseFile("profilePhoto");

                                //chiamata get per salvare il thumbnail
                                String url = "http://clothapp.westeurope.cloudapp.azure.com/createprofilethumbnail/"+object.getObjectId();
                                Get g = new Get();
                                g.execute(url);
                            }
                            f.getFileInBackground(new GetFileCallback() {
                                @Override
                                public void done(File file, ParseException e) {
                                    if (e == null) {
                                        u.setProfilo(file);
                                        likeAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }
        canLoad = true;
    }

    //funzione che ritorna il gestureDetector per il doubletap
    public GestureDetector doubleTapGesture(final String file) {
        return new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e){
                Intent i=new Intent(context,ZoomPhoto.class);
                i.putExtra("url",file);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                final String username = ParseUser.getCurrentUser().getUsername();
                if (immagine.getLike().contains(username)) {
                    //chiamo delete like
                    LikeRes.deleteLike(immagine.getObjectId(), immagine, username);

                    cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
                } else {
                    //chiamo addlike
                    LikeRes.addLike(immagine.getObjectId(), immagine, username);

                    cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
                }
                //  se ho zero likes scrivo like sennò likes
                try{
                    setTextLike();
                }catch (Exception err){
                    like.setText(" error!");
                }

                //inizializzo animazione del cuore che entra ed esce
                Animation pulse_fade = AnimationUtils.loadAnimation(context, R.anim.pulse_fade_in);
                pulse_fade.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        heartAnim.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        heartAnim.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                heartAnim.startAnimation(pulse_fade);
                //likeImg.setImageDrawable(context.getResources().getDrawable(R.drawable.like_active));
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                return true;
            }


        });
    }
}