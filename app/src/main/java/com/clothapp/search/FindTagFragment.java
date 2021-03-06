package com.clothapp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clothapp.image_detail.ImageActivity;
import com.clothapp.R;

import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.Image;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by jack1 on 18/02/2016.
 */
public class FindTagFragment extends Fragment {
    private static final String ALL = "all";
    String POPOLARITA = "Most Popolar";
    private static final String OLD = "Old";
    private static final String RECENT = "Most Recent";
    private View rootView;
    private ListView listTag;
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
    private ProgressBar progressBar;
    private TextView notfound;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listTag = (ListView) rootView.findViewById(R.id.userlist);
        global = (ApplicationSupport) getActivity().getApplicationContext();
        cloth = global.getTag();

        progressBar=(ProgressBar)rootView.findViewById(R.id.progressbar);
        notfound=(TextView)rootView.findViewById(R.id.notfound);
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listTag.setAdapter(adapter);
        notfound.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        search();

        listTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), ImageActivity.class);
                i.putExtra("classe", "FindCloth");
                i.putExtra("position", position);
                startActivity(i);
            }
        });

        //setto il listener sullo scroller quando arrivo in fondo
        listTag.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && cloth.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
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
        SearchResultsActivity.tabHashtagResultCount.setText(""+adapter.getCount());
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query = query.trim().toLowerCase();

        //faccio la query a Parse delle foto
        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        if (order == null) order = POPOLARITA;
        //queste sono verifiche dovute al filtro di ricerca
        if (order.equals(POPOLARITA)) queryFoto.addDescendingOrder("nLike");
        if (order.equals(OLD)) queryFoto.addAscendingOrder("createdAt");
        if (order.equals(RECENT)) queryFoto.addDescendingOrder("createdAt");
        //if(order.equals() queryFoto.addAscendingOrder("nLike");
        //setto il limit e lo skip della query
        queryFoto.setSkip(skip);
        queryFoto.setLimit(10);
        skip = skip + 10;
        //System.out.println(order + " " + query + " " + skip);
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    progressBar.setVisibility(View.INVISIBLE);

                    //System.out.println("done" + objects);
                    ListIterator<ParseObject> i = objects.listIterator();

                    while (i.hasNext()) {

                        List<String> tag = new ArrayList<String>();
                        final ParseObject o = i.next();
                        //prendo i tipi di vestito e li metto nella lista tag
                        tag = (ArrayList) o.get("hashtag");
                        if (tag == null) tag = new ArrayList<String>(0);
                        //una foto puù avere più vestiti quindi itero su ogni vestito
                        for (int j = 0; j < tag.size(); j++) {
                            //se la query matcha (contenuta) in un tipo di vestito allora è una candidata per essere inserita in lista
                            //System.out.println(tag.get(j).trim().toLowerCase()+": "+query);
                            if (tag.get(j).trim().toLowerCase().contains(query)) {

                                final Image image = new Image(o);
                                //un ulteriore controllo= se la foto è già in lista la ignoro per evitare che ci sia 2 volte
                                if (!cloth.contains(image)) {
                                    //System.out.println("sex:" + sex + ":");
                                    //------------------inizio filtro sesso-----------------
                                    //SPIEGAZIONE:ci sono 3 tipi di sesso man, woman e all. Con all si prendono tutte le foto
                                    // quindi se sex==ALL posso evitare di fare controlli inutili sul sesso

                                    if (!sex.equals(ALL)) {
                                        //System.out.println("1");
                                        //faccio una query a parse nella tabella Persona per sapere il sesso dell'user

                                        // LE FOTO DEI NEGOZI vengono scartate!!!!!

                                        ParseQuery<ParseObject> persona = new ParseQuery<ParseObject>("Persona");
                                        persona.whereEqualTo("username", o.getString("user"));
                                        final List<String> finalTag = tag;
                                        final int finalJ = j;

                                        persona.getFirstInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                if (e == null) {
                                                    if (object != null) {
                                                        String s = object.getString("sex");

                                                        if (s.equals("m")) s = "man";
                                                        if (s.equals("f")) s = "woman";
                                                        //System.out.println(s+"="+sex);
                                                        //se il sesso dell'user corrisponde allora la foto è una candidata per entrare in lista
                                                        if (s.equals(sex)) {
                                                            //----------filtro prezzo---------------
                                                            //System.out.println("sesso uguale");

                                                            // Per ogni vestito nella foto devo controllare il prezzo facendo un'altra query a parse nella tabella Vestito


                                                            //System.out.println(prezzoDa + " A " + prezzoA);

                                                            //prima di procedere e fare controlli inutili, controllo se il filtro del prezzo è stato impostato
                                                            //Il valore -1 indica che non è stato impostato

                                                            if (prezzoDa != -1 || prezzoA != -1) {
                                                                ArrayList<String> v = (ArrayList) o.get("vestiti");
                                                                for (String st : v) {
                                                                    ParseQuery<ParseObject> vestito = new ParseQuery<ParseObject>("Vestito");
                                                                    vestito.whereEqualTo("objectId", st);
                                                                    vestito.getFirstInBackground(new GetCallback<ParseObject>() {
                                                                        @Override
                                                                        public void done(ParseObject object, ParseException e) {
                                                                            if (e == null) {
                                                                                if (object != null) {
                                                                                    //trovo il prezzo
                                                                                    Object obj = object.get("prezzo");
                                                                                    String p;
                                                                                    if (obj != null)
                                                                                        p = object.get("prezzo").toString();
                                                                                    else
                                                                                        p = "-1";
                                                                                    Float prezzo = Float.parseFloat(p);
                                                                                    //se prezzoA non è stato impostato(=-1) allora non ho un limite quindi lo setto a maxValue
                                                                                    if (prezzoA == -1f)
                                                                                        prezzoA = Float.MAX_VALUE;
                                                                                    //confronto il prezzo con i paramentri del filtro
                                                                                    //System.out.println("prezzo=" + prezzo + " prezzoDa" + prezzoDa + " PrezzoA" + prezzoA);
                                                                                    if (prezzo >= prezzoDa && prezzo <= prezzoA) {
                                                                                        //System.out.println("prezzo giusto");
                                                                                        if (!cloth.contains(image)) {
                                                                                            cloth.add(image);
                                                                                            global.setCloth(cloth);
                                                                                            adapter.notifyDataSetChanged();
                                                                                            SearchResultsActivity.tabHashtagResultCount.setText("" + adapter.getCount());
                                                                                        }
                                                                                        checkLast();
                                                                                    }

                                                                                }//else{
                                                                                // cloth.add(image);
                                                                                //global.setCloth(cloth);
                                                                                //adapter.notifyDataSetChanged();
                                                                                // }
                                                                            } else
                                                                                check(e.getCode(), rootView, e.getMessage());
                                                                        }
                                                                    });
                                                                }
                                                                //nel caso in cui non c'è un filtro del prezzo
                                                            } else {
                                                                if (!cloth.contains(image)) {
                                                                    cloth.add(image);
                                                                    global.setCloth(cloth);
                                                                    adapter.notifyDataSetChanged();
                                                                    SearchResultsActivity.tabHashtagResultCount.setText("" + adapter.getCount());
                                                                }
                                                                checkLast();

                                                            }
                                                        }
                                                        //fine filtro prezzo
                                                    }

                                                } else

                                                {
                                                    //System.out.println("4");
                                                    check(e.getCode(), rootView, e.getMessage());
                                                }
                                            }
                                        });
                                    }
                                    //-------------fine filtro sesso----------


                                    // Caso in cui non c'è il filtro del sesso, Salto tutti i controlli a riguardo

                                    else {
                                        //.out.println("aggiungo all");
                                        //------------------------filtro prezzo-------------------------
                                        //qui valgono le stesse considerazione fatte prima (ho fatto copia e incolla)
                                        ArrayList<String> v = (ArrayList) o.get("vestiti");
                                        for (String st : v) {
                                            //System.out.println(prezzoDa + " A " + prezzoA);
                                            if (prezzoDa != -1 || prezzoA != -1) {
                                                ParseQuery<ParseObject> vestito = new ParseQuery<ParseObject>("Vestito");
                                                vestito.whereEqualTo("objectId", st);
                                                vestito.getFirstInBackground(new GetCallback<ParseObject>() {
                                                    @Override
                                                    public void done(ParseObject object, ParseException e) {
                                                        if (e == null) {
                                                            if (object != null) {
                                                                //trovo il prezzo
                                                                Object obj = object.get("prezzo");
                                                                String p;
                                                                if (obj != null)
                                                                    p = object.get("prezzo").toString();
                                                                else p = "-2";
                                                                Float prezzo = Float.parseFloat(p);

                                                                if (prezzoA == -1f)
                                                                    prezzoA = Float.MAX_VALUE;
                                                                //confronto il prezzo con i paramentri del filtro
                                                                //System.out.println("prezzo=" + prezzo + " prezzoDa" + prezzoDa + " PrezzoA" + prezzoA);
                                                                if (prezzo >= prezzoDa && prezzo <= prezzoA) {
                                                                    //System.out.println("prezzo giusto");
                                                                    if (!cloth.contains(image)) {
                                                                        cloth.add(image);
                                                                        global.setCloth(cloth);
                                                                        adapter.notifyDataSetChanged();
                                                                        SearchResultsActivity.tabHashtagResultCount.setText("" + adapter.getCount());
                                                                    }
                                                                    checkLast();
                                                                }

                                                            }//else{
                                                            // cloth.add(image);
                                                            //global.setCloth(cloth);
                                                            //adapter.notifyDataSetChanged();
                                                            // }
                                                        } else {
                                                            //System.out.println("3");
                                                            check(e.getCode(), rootView, e.getMessage());
                                                        }

                                                    }
                                                });

                                                //caso in cui non devo fare nessun controllo

                                            } else {
                                                if (!cloth.contains(image)) {
                                                    cloth.add(image);
                                                    global.setCloth(cloth);
                                                    adapter.notifyDataSetChanged();
                                                    SearchResultsActivity.tabHashtagResultCount.setText("" + adapter.getCount());
                                                }
                                                checkLast();

                                            }
                                        }
                                        //fine filtro prezzo
                                    }
                                }

                                break;

                            }
                        }
                    }
                    checkLast();
                    canLoad = true;
                    if (global.getCloth().size() == 0) notfound.setVisibility(View.VISIBLE);
                } else

                {
                    //System.out.println("errore");
                    check(e.getCode(), rootView, e.getMessage());
                }
            }
        });






    }


    public Fragment newIstance(String query, String sex, Float priceFrom, Float priceTo, Context context, String order) {
        this.context = context;
        final FindTagFragment f = new FindTagFragment();
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
        this.query = query.trim().toLowerCase();
        skip = 0;
        cloth = new ArrayList<>();
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listTag.setAdapter(adapter);
        global.setTag(cloth);
        search();
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //System.out.println("onresume"+user);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //System.out.println("visibile");
            adapter.notifyDataSetChanged();
            if(adapter.getCount()==0) search();
        } else {
            //System.out.println("nonvisibile");
        }
    }

    synchronized private void checkLast(){
        //System.out.println("check "+listCloth.getCount());

        if(adapter.getCount()<5 && skip<500){
            //System.out.println("yes");
            adapter.notifyDataSetChanged();
            search();
        }

    }

    public static ArrayList<Image> getCloth() {
        return cloth;
    }
}
