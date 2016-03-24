package com.clothapp.search;

/**
 * Created by Jacopo on 24/03/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;

import com.clothapp.resources.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by jack1 on 18/02/2016.
 */
public class FindBrandFragment extends Fragment {
    private static final String WOMAN = "woman";
    private static final String ALL = "all";

    private View rootView;
    private ListView listCloth;
    private Context context;
    private String query;
    private boolean canLoad = true;
    private static ArrayList<Image> cloth;
    private ApplicationSupport global;
    private SearchAdapterImage adapter;
    private int skip = 0;
    private String sex;
    private Float prezzoDa;
    private Float prezzoA;
    private String order;
    private boolean first = true;

    String POPOLARITA = "Most Popolar";
    private static final String OLD = "Old";
    private static final String RECENT = "Most Recent";
    private ProgressBar progressBar;
    private TextView notfound;
    private int count=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_cloth, container, false);
        listCloth = (ListView) rootView.findViewById(R.id.clothlist);
        global = (ApplicationSupport) getActivity().getApplicationContext();

        progressBar=(ProgressBar)rootView.findViewById(R.id.progressbar);
        notfound=(TextView)rootView.findViewById(R.id.notfound);

        //System.out.println("create");

        //prendo la lista da global.
        /*L'utilizzo di global è necessario (questo vale per tutti e 3 i fragment)
         poichè la lista dei vestiti viene cancellata se il fragment non è più in cache. In particolare un PagerView tiene in cache solo 2 fragment.
         Questo causava un BUG: cioè quando si tornava su quel fragment la lista era vuota.
        */

        cloth = global.getCloth();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listCloth.setAdapter(adapter);
        notfound.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        search();

        //setto il listener sullo scroller quando arrivo in fondo
        listCloth.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && (cloth.size() > 0) || (!first && cloth.size() == 0)) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (cloth != null) {
                            canLoad = false;
                            notfound.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            search();

                        }
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
        });

        return rootView;
    }

    public void search() {
        SearchResultsActivity.tabClothesResultCount.setText(""+adapter.getCount());
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query = query.trim().toLowerCase();
        System.out.println("search "+query);


        listCloth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("click");
                Intent i = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                i.putExtra("classe", "FindCloth");
                i.putExtra("position", position);
                startActivity(i);
            }
        });

        //faccio la query a Parse delle foto
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Vestito");
        if (order == null) order = POPOLARITA;
        //queste sono verifiche dovute al filtro di ricerca (in base all'ordine)
        if (order.equals(POPOLARITA)) queryFoto.addDescendingOrder("nLike");
        if (order.equals(OLD)) queryFoto.addAscendingOrder("createdAt");
        if (order.equals(RECENT)) queryFoto.addDescendingOrder("createdAt");
        //setto il limit e lo skip della query
        queryFoto.setSkip(skip);
        queryFoto.setLimit(10);
        skip = skip + 10;
        //System.out.println(order + " " + query + " " + skip);
        queryFoto.whereContains("brand",query);
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null && objects!=null){
                    for(int i=0;i<objects.size();i++){
                        cloth.add(new Image(objects.get(i)));
                        //global.setUsers(user);
                        adapter.notifyDataSetChanged();
                        //TODO: scrivere la query
                    }
                }
            }
        });





    }


    public Fragment newIstance(String query, String sex, Float priceFrom, Float priceTo, Context context, String order) {
        this.context = context;
        final FindBrandFragment f = new FindBrandFragment();
        final Bundle args = new Bundle();
        args.putString("query", query);
        args.putString("sex", sex);
        args.putFloat("prezzoDa", priceFrom);
        args.putFloat("prezzoA", priceTo);
        args.putString("order", order);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getArguments() != null ? (String) getArguments().getString("query") : null;
        this.sex = getArguments() != null ? (String) getArguments().getString("sex") : null;
        this.prezzoDa = getArguments() != null ? (Float) getArguments().getFloat("prezzoDa") : null;
        this.prezzoA = getArguments() != null ? (Float) getArguments().getFloat("prezzoA") : null;
        this.order = getArguments() != null ? (String) getArguments().getString("order") : null;

    }

    public void refresh(String query) {
        //System.out.println("resfresh");
        this.query = query.trim().toLowerCase();
        skip = 0;
        cloth = new ArrayList<>();
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listCloth.setAdapter(adapter);
        global.setTag(cloth);
        search();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //System.out.println("onresume");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("visibile");
            adapter.notifyDataSetChanged();
            //checkLast();
        } else {
            //System.out.println("nonvisibile");
        }
    }

    synchronized private void checkLast(){
        System.out.println("check "+listCloth.getCount());

        if(adapter.getCount()<5 && skip<500){
            System.out.println("yes");
            adapter.notifyDataSetChanged();
            search();
        }

    }

    public static ArrayList<Image> getCloth() {
        return cloth;
    }
}
