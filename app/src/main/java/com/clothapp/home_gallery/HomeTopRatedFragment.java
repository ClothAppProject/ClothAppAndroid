package com.clothapp.home_gallery;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 02/02/16.
 */
public class HomeTopRatedFragment extends Fragment {
    private List<CardView>cardViews;
    private View rootView;
    SwipeRefreshLayout swipeRefreshLayout;
    MyListAdapter adapter;
    private ListView l;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static ArrayList<Image> photos = new ArrayList<>();
    Boolean canLoad = true;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_home_top_rated, container, false);
        l=(ListView) rootView.findViewById(R.id.toplist);
        findRated(12);

        //istanzio lo swipe to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                findRated(photos.size());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.background, R.color.orange);

        //setto il listener sullo scroller quando arrivo in fondo
        l.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && photos.size()>0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (photos != null) {
                            canLoad = false;
                            int toDownload = 10;
                            final int maxNumLike = photos.get(photos.size() - 1).getNumLike();
                            ParseQuery<ParseObject> updatePhotos = new ParseQuery<ParseObject>("Photo");
                            updatePhotos.whereLessThanOrEqualTo("nLike", maxNumLike);
                            updatePhotos.orderByDescending("nLike");
                            updatePhotos.setLimit(toDownload);
                            updatePhotos.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null) {
                                        if (objects.size() > 0) {
                                            int i;
                                            for (i = 0; i < objects.size(); i++) {
                                                ParseObject object = objects.get(i);
                                                //faccio un controllo, se ho stesso numero di like dell'ultima foto e poi se è già
                                                //contenuta all'interno della lista di foto, allora passo alla prossima evitando di fare chiamate per parse
                                                if (object.getInt("nLike") == maxNumLike) {
                                                    if (photos.contains(new Image(null, objects.get(i).getObjectId(), null, null,0)))
                                                        continue;
                                                }
                                                ParseFile f = objects.get(i).getParseFile("thumbnail");
                                                try {
                                                    //ottengo la foto e la aggiungo
                                                    Image toAdd = new Image(f.getFile(), object.getObjectId(), object.getString("user"), object.getList("like"),object.getInt("nLike"));
                                                    photos.add(toAdd);
                                                    //notifico l'image adapter di aggiornarsi
                                                    adapter.notifyDataSetChanged();
                                                } catch (ParseException e1) {
                                                    check(e1.getCode(), rootView, e1.getMessage());
                                                }
                                            }
                                            canLoad = true;
                                        }
                                    } else {
                                        check(e.getCode(), rootView, e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
        //setCardViews(findTopRated(),container);
        return rootView;
    }
    /*
        public void setCardViews(List<Image>images,ViewGroup container){
            int i;
            for(i=0;i<images.size();i++){
               // Card c=new Card();
                ImageView imageView= (ImageView) getActivity().findViewById(R.id.topfoto);
                l.addView(imageView);
                TextView user=(TextView)rootView.findViewById(R.id.user);
                user.setText(images.get(i).getUser());
                Glide.with(this)
                        .load(images.get(i).getFile())
                        .into(imageView);
            }
        }
    */
    public void findRated(int n)    {
        photos = new ArrayList<>();
        //qui scarico le foto
        final View vi = new View(getActivity().getApplicationContext());
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.setLimit(n);
        query.orderByDescending("nLike");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> fotos, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < fotos.size(); i++) {
                        ParseObject obj = fotos.get(i);
                        ParseFile file = obj.getParseFile("thumbnail");
                        try {
                            //inserisco le foto in una lista
                            photos.add(new com.clothapp.resources.Image(file.getFile(), obj.getObjectId(),obj.getString("user"),obj.getList("like"),obj.getInt("nLike")));

                        } catch (ParseException e1) {
                            check(e.getCode(), vi, e.getMessage());
                        }
                    }
                    adapter=new MyListAdapter(getActivity().getApplicationContext(), photos);
                    l.setAdapter(adapter);
                    l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (HomeActivity.menuMultipleActions.isExpanded()) {
                                HomeActivity.menuMultipleActions.collapse();
                            } else {
                                Intent toPass = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                                toPass.putExtra("classe","topRated");
                                toPass.putExtra("position", position);
                                startActivity(toPass);
                            }
                        }
                    });
                } else {
                    //errore nel reperire gli oggetti Photo dal database
                    check(e.getCode(), vi, e.getMessage());
                }
            }
        });
    }

}