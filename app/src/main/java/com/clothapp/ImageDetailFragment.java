package com.clothapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.CircleTransform;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.resources.LikeRes;
import com.clothapp.resources.MyCardListAdapter;
import com.parse.FindCallback;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class ImageDetailFragment extends Fragment {

    private ImageView imageView;
    private ImageView heartAnim;
    private TextView t;
    private String Id;
    private Image immagine;
    private static Context context;
    private List<Cloth> vestiti;
    private ListView listView;
    private TextView hashtag;
    private ImageView person;
    private ImageView share;
    private ImageView cuore;
    private TextView like;
    private TextView percentuale;
    private ImageView profilePic;
    private View vi;
    private ParseObject parseObject;

    public ImageDetailFragment newInstance(Image image, Context c) {
        this.context = c;
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putParcelable("ID", image);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.immagine = getArguments()!=null ? (Image) getArguments().getParcelable("ID") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t=(TextView)rootView.findViewById(R.id.user);
        imageView=(ImageView) rootView.findViewById(R.id.photo);
        heartAnim=(ImageView) rootView.findViewById(R.id.heart_anim);
        listView=(ListView)rootView.findViewById(R.id.listInfo);
        hashtag=(TextView)rootView.findViewById(R.id.hashtag);
        person=(ImageView)rootView.findViewById(R.id.person);
        share=(ImageView)rootView.findViewById(R.id.share);
        cuore=(ImageView)rootView.findViewById(R.id.heart);
        like=(TextView)rootView.findViewById(R.id.like);
        profilePic = (ImageView)rootView.findViewById(R.id.pic);
        percentuale = (TextView)rootView.findViewById(R.id.percentuale);

        vi = new View(context);
        //trovo le info delle foto e le inserisco nella view
        //findInfoPhoto();
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setto il listener sull'icona persona
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = ProfileUtils.goToProfile(getActivity().getApplicationContext(),immagine.getUser());
                startActivity(i);

            }
        });

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
        queryFoto.whereEqualTo("username", immagine.getUser());
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    //  if the user has a profile pic it will be shown in the side menu
                    //  else the app logo will be shown
                    if (objects.size() != 0) {
                        ParseFile f = objects.get(0).getParseFile("profilePhoto");
                        try {
                            File file = f.getFile();
                            Glide.with(context)
                                    .load(file)
                                    .centerCrop()
                                    .transform(new CircleTransform(context))
                                    .into(profilePic);
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    check(e.getCode(), vi, e.getMessage());
                }
            }
            });


        //faccio query al database per scaricare la foto
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", immagine.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                parseObject = object;
                //setto username e listener
                t.setText(immagine.getUser());
                t.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = ProfileUtils.goToProfile(getActivity().getApplicationContext(),immagine.getUser());
                        startActivity(i);
                    }
                });

                //listener on the profile pic
                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = ProfileUtils.goToProfile(getActivity().getApplicationContext(),immagine.getUser());
                        startActivity(i);
                        getActivity().finish();
                    }
                });

                //setto gli hashtag
                ArrayList tag = (ArrayList) object.get("hashtag");
                String s = " ";
                if (tag != null) {
                    for (int i = 0; i < tag.size(); i++) {
                        s += tag.get(i).toString() + " ";
                    }
                }

                hashtag.setText((CharSequence) s);

                //per ogni vestito cerco le informazioni
                ArrayList arrayList = (ArrayList) object.get("vestiti");
                if (arrayList == null) arrayList = new ArrayList<Cloth>();
                vestiti = new ArrayList<Cloth>(arrayList.size());
                for (int i = 0; i < arrayList.size(); i++) {
                    ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Vestito");
                    query1.whereEqualTo("objectId", arrayList.get(i));
                    query1.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject info, ParseException e) {
                            if (e == null) {
                                Float fl=0.0f;
                                if(info.get("prezzo")!=null){
                                    if(info.get("prezzo").getClass()!=Float.class)
                                        fl=Float.parseFloat(info.get("prezzo").toString());
                                    else fl=(float)info.get("prezzo");
                                }
                                Cloth c = new Cloth(info.getString("tipo"),
                                    info.getString("luogoAcquisto"),
                                        fl,
                                    info.getString("shop"),
                                    info.getString("shopUsername"),
                                    info.getString("brand"));
                                vestiti.add(c);
                                MyCardListAdapter adapter = new MyCardListAdapter(context, vestiti);
                                listView.setAdapter(adapter);
                                setListViewHeightBasedOnItems(listView);
                            }
                        }
                    });
                }

                object.getParseFile("photo").getFileInBackground(new GetFileCallback() {
                    @Override
                    public void done(final File file, ParseException e) {
                        if (e == null) {
                            //Gesture Detector for detecting double tap
                            //code is at the end of page
                            final GestureDetector gd = doubleTapGesture(object);

                            Glide.with(context)
                                    .load(file)
                                    .into(imageView);
                            imageView.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    return gd.onTouchEvent(event);
                                }
                            });

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
                        if (percentDone==100)   {
                            percentuale.setVisibility(View.INVISIBLE);
                        }
                        percentuale.setText(percentDone+"%");
                    }
                });

                //mostro il numero di like
                int numLikes = object.getInt("nLike");
                //  se ho zero likes scrivo like sennò likes
                String singPlur = numLikes == 0 || numLikes == 1? "like" : "likes";

                like.setText(Integer.toString(numLikes) + " " + singPlur);

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
                            //possibile problema di concorrenza sull'oggetto in caso più persone stiano mettendo like contemporaneamente
                            //rimuovo il like e cambio la lista
                            LikeRes.deleteLike(object,immagine,username);

                            cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);

                            int numLikes = object.getInt("nLike");
                            //  se ho zero likes scrivo like sennò likes
                            String singPlur = numLikes == 0 || numLikes == 1 ? "like" : "likes";
                            like.setText(Integer.toString(numLikes) + " " + singPlur);
                        } else {
                            //aggiungo like e aggiorno anche in parse
                            LikeRes.addLike(object,immagine,username);
                            cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
                        }
                        //aggiorno il numero di like
                        int numLikes = object.getInt("nLike");
                        //  se ho zero likes scrivo like sennò likes
                        String singPlur = numLikes == 0 || numLikes == 1 ? "like" : "likes";
                        like.setText(Integer.toString(numLikes) + " " + singPlur);
                    }
                });
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.image_fragment, menu);
        MenuItem deletePhoto = menu.findItem(R.id.delete);
        deletePhoto.setVisible(immagine.getUser().equals(ParseUser.getCurrentUser().getUsername()));
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
                                segnalazione.put("comment",comment.getText().toString());
                                segnalazione.put("from_username",ParseUser.getCurrentUser().getUsername());
                                segnalazione.put("reason",spinner.getSelectedItem());
                                segnalazione.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e!=null)    {
                                            check(e.getCode(),getView(),e.getMessage());
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
                                if (parseObject.getList("vestiti")!=null) {
                                    for (Object id : parseObject.getList("vestiti")) {
                                        ParseObject vestito = ParseObject.createWithoutData("Vestito", (String) id);
                                        vestito.deleteInBackground();
                                    }
                                }
                                //prendo la foto e la elimino
                                parseObject.deleteInBackground();
                                //la tolgo dalla lista passata all'image fragment
                                ImageFragment.lista.remove(immagine);
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

    //funzione che ritorna il gestureDetector per il doubletap
    public GestureDetector doubleTapGesture(final ParseObject object)   {
        return new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                final String username = ParseUser.getCurrentUser().getUsername();
                if (immagine.getLike().contains(username)) {
                    //possibile problema di concorrenza sull'oggetto in caso più persone stiano mettendo like contemporaneamente
                    //rimuovo il like e cambio la lista
                    LikeRes.deleteLike(object,immagine,username);

                    cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);

                    int numLikes = object.getInt("nLike");
                    //  se ho zero likes scrivo like sennò likes
                    String singPlur = numLikes == 0 || numLikes == 1 ? "like" : "likes";
                    like.setText(Integer.toString(numLikes) + " " + singPlur);
                } else {
                    //aggiungo like e aggiorno anche in parse
                    LikeRes.addLike(object,immagine,username);
                    cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
                }
                //aggiorno il numero di like
                int numLikes = object.getInt("nLike");
                //  se ho zero likes scrivo like sennò likes
                String singPlur = numLikes == 0 || numLikes == 1 ? "like" : "likes";
                like.setText(Integer.toString(numLikes) + " " + singPlur);

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
