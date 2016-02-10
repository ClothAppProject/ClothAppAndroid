package com.clothapp.home_gallery;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

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
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by giacomoceribelli on 01/02/16.
 */
public class HomeMostRecentFragment extends Fragment {
    ApplicationSupport global;
    ArrayList<Image> photos;
    SwipeRefreshLayout swipeRefreshLayout;
    ImageGridViewAdapter imageGridViewAdapter;
    Boolean canLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View vi = inflater.inflate(R.layout.fragment_home_last, container, false);

        global = (ApplicationSupport) getActivity().getApplicationContext();
        photos = global.getPhotos();
        final GridView gridview = (GridView) vi.findViewById(R.id.galleria_homepage);

        //istanzio lo swipe to refresh
        // find the layout
        swipeRefreshLayout = (SwipeRefreshLayout) vi.findViewById(R.id.swipe_container);
        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //query che prende tutte le foto più nuove rispetto a quelle già in memoria
                ParseQuery<ParseObject> updatePhotos = new ParseQuery<ParseObject>("Photo");
                updatePhotos.whereGreaterThan("createdAt", global.getFirstDate());
                updatePhotos.orderByDescending("createdAt");
                updatePhotos.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                //modifico la data della prima foto
                                global.setFirstDate(objects.get(0).getCreatedAt());
                                for (int i = objects.size() - 1; i >= 0; i--) {
                                    ParseFile f = objects.get(i).getParseFile("thumbnail");
                                    try {
                                        //ottengo la foto e la aggiungo per prima
                                        global.addFirstPhoto(new Image(f.getFile(), objects.get(i).getObjectId(),objects.get(i).getString("user"),objects.get(i).getList("like")));
                                    } catch (ParseException e1) {
                                        check(e1.getCode(), vi, e1.getMessage());
                                    }
                                    //aggiorno la galleria
                                    loadSplashImage(gridview);
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                        } else {
                            swipeRefreshLayout.setRefreshing(false);
                            check(e.getCode(), vi, e.getMessage());
                        }
                    }
                });
            }
        });
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.background, R.color.orange);

        //questa va chiamata solo la prima volta
        loadSplashImage(gridview);

        //listener sullo scrollview della gridview
        gridview.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad) {
                        canLoad = false;
                        int toDownload = 10;
                        if (photos.size() % 2 == 0) toDownload = 11;
                        Toast.makeText(getActivity(),"ATTENDERE, CARICAMENTO FOTO...",Toast.LENGTH_SHORT).show();
                        ParseQuery<ParseObject> updatePhotos = new ParseQuery<ParseObject>("Photo");
                        updatePhotos.whereLessThan("createdAt", global.getLastDate());
                        updatePhotos.orderByDescending("createdAt");
                        updatePhotos.setLimit(toDownload);
                        updatePhotos.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    if (objects.size() > 0) {
                                        int i;
                                        for (i = 0; i < objects.size(); i++) {
                                            ParseFile f = objects.get(i).getParseFile("thumbnail");
                                            try {
                                                //ottengo la foto e la aggiungo per ultima
                                                Image toAdd = new Image(f.getFile(), objects.get(i).getObjectId(),objects.get(i).getString("user"),objects.get(i).getList("like"));
                                                global.addLastPhoto(toAdd);
                                                //notifico l'image adapter di aggiornarsi
                                                imageGridViewAdapter.notifyDataSetChanged();
                                            } catch (ParseException e1) {
                                                check(e1.getCode(), vi, e1.getMessage());
                                            }
                                        }
                                        canLoad = true;
                                        Toast.makeText(getActivity(),"CARICAMENTO COMPLETATO",Toast.LENGTH_SHORT).show();
                                        //modifico la data dell'utlima foto
                                        global.setLastDate(objects.get(i - 1).getCreatedAt());
                                    }
                                } else {
                                    check(e.getCode(), vi, e.getMessage());
                                }
                            }
                        });
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });
        return vi;
    }

    private void loadSplashImage(final GridView gridview) {
        imageGridViewAdapter = new ImageGridViewAdapter(getActivity().getApplicationContext(), photos);
        gridview.setAdapter(imageGridViewAdapter);

        //listener su ogni foto della gridview
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if (HomeActivity.menuMultipleActions.isExpanded()) {
                    HomeActivity.menuMultipleActions.collapse();
                } else {
                    Intent toPass = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                    toPass.putExtra("position", position);
                    //passo la lista delle foto al fragment
                    toPass.putExtra("lista",photos);
                    startActivity(toPass);
                }
            }

        });
    }
}