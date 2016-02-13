package com.clothapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clothapp.home_gallery.HomeActivity;
import com.clothapp.profile.ProfileActivity;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.resources.MyCardListAdapter;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageDetailFragment extends Fragment {

    private String user;
    private ImageView v;
    private TextView t;
    private DonutProgress donutProgress;
    private String Id;
    private static Context context;
    private List<Cloth> vestiti;
    private ListView listView;
    private TextView hashtag;
    private ImageView person;
    private ImageView share;
    private ImageView cuore;
    private TextView like;

    public static ImageDetailFragment newInstance(String id, Context c) {
        context = c;
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("ID", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.Id = getArguments() != null ? getArguments().getString("ID") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t=(TextView)rootView.findViewById(R.id.user);
        v=(ImageView) rootView.findViewById(R.id.photo);
        listView=(ListView)rootView.findViewById(R.id.listInfo);
        hashtag=(TextView)rootView.findViewById(R.id.hashtag);
        person=(ImageView)rootView.findViewById(R.id.person);
        share=(ImageView)rootView.findViewById(R.id.share);
        cuore=(ImageView)rootView.findViewById(R.id.heart);
        like=(TextView)rootView.findViewById(R.id.like);

        //trovo le info delle foto e le inserisco nella view
        //findInfoPhoto();

        //donutProgress = (DonutProgress) rootView.findViewById(R.id.donut_progress);
        return rootView;
    }

    //TODO:fare la query giusta
    public void findInfoPhoto(){
        //qui scarico le foto

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //setto il listener sull'icona person
        person.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                i.putExtra("user", user);
                startActivity(i);
                getActivity().finish();
            }
        });



        //faccio query al database per scaricare la foto
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", Id);
        query.orderByDescending("createdAt");
        try {
            final List<ParseObject> objects = query.find();

            final Image image=new Image(objects.get(0));

            user = objects.get(0).getString("user");
            t.setText(user);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            //setto gli hashtag
            ArrayList tag=(ArrayList)objects.get(0).get("hashtag");
            String s=" ";
            if(tag!=null) {
                for (int i = 0; i < tag.size(); i++) {
                    s += tag.get(i).toString() + " ";
                }
            }
            hashtag.setText((CharSequence) s);

            //per ogni vestito cerco le informazioni
            ArrayList arrayList= (ArrayList) objects.get(0).get("vestiti");
            if(arrayList==null) arrayList=new ArrayList<Cloth>();
            vestiti=new ArrayList<Cloth>(arrayList.size());
            for(int i=0;i<arrayList.size();i++) {
                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Vestito");
                query1.whereEqualTo("codice", arrayList.get(i));
                query1.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject info, ParseException e) {
                        if (e == null) {
                            Cloth c = new Cloth(info.getString("tipo"),
                                    info.getString("luogoAcquisto"),
                                    info.getString("prezzo"),
                                    info.getString("shop"),
                                    info.getString("brand"));

                            vestiti.add(c);
                            MyCardListAdapter adapter = new MyCardListAdapter(context, vestiti);
                            listView.setAdapter(adapter);
                            setListViewHeightBasedOnItems(listView);
                        }

                    }
                });

            }

            objects.get(0).getParseFile("photo").getFileInBackground(new GetFileCallback() {
                @Override
                public void done(final File file, ParseException e) {
                    if (e == null) {
                        //nascondo caricamento mostro immagine
                        //donutProgress.setVisibility(View.INVISIBLE);
                        //v.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(file)
                                .into(v);

                        //TODO: problemi di permesso di lettura
                    /*
                        //setto il listener sull'icona share
                        share.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                Log.d("Share", file.toURI().toString());

                                //context.grantUriPermission (String.valueOf(contentUri), contentUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, file.toURI());
                                shareIntent.setType("image/jpeg");
                                shareIntent.setFlags( Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));

                            }
                        });

                        */
                            }
                        }

                    }, new ProgressCallback() {
                        @Override
                        public void done(Integer percentDone) {
                            //passo percentuale
                            // donutProgress.setProgress(percentDone);
                        }
                    });

            //mostro il numero di like
            like.setText(objects.get(0).get("nLike").toString());

            //controllo se ho messo like sull'attuale foto
            final String username = ParseUser.getCurrentUser().getUsername();
            if ((image.getLike().contains(username)))    {
                cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
            }else{
                cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
            }

            //metto i listener sul cuore
            cuore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("cuore cliccato");
                    ParseObject point = ParseObject.createWithoutData("Photo", image.getObjectId());
                    //if(HomeActivity.menuMultipleActions.isExpanded()) HomeActivity.menuMultipleActions.collapse();
                    //else {
                        if ((image.getLike().contains(username))) {
                            //possibile problema di concorrenza sull'oggetto in caso più persone stiano mettendo like contemporaneamente
                            //rimuovo il like e cambio la lista
                            image.remLike(username);
                            point.put("like", image.getLike());
                            point.put("nLike", image.getLike().size());
                            point.saveInBackground();
                            cuore.setImageResource(R.mipmap.ic_favorite_border_white_48dp);
                            //aggiorno il numero di like
                            like.setText(objects.get(0).get("nLike").toString());
                            //aggiorno la galleria


                        } else {
                            //aggiungo like e aggiorno anche in parse
                            image.addLike(username);
                            point.add("like", username);
                            point.put("nLike", image.getLike().size());
                            point.saveInBackground();
                            cuore.setImageResource(R.mipmap.ic_favorite_white_48dp);
                            //aggiorno il numero di like
                            like.setText(objects.get(0).get("nLike").toString());

                        }
                    //}
                }
            });






        } catch (ParseException e) {
            //errore chiamata
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
