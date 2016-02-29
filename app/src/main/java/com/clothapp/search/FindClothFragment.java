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
import android.widget.ListAdapter;
import android.widget.ListView;
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

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by jack1 on 18/02/2016.
 */
public class FindClothFragment extends Fragment {
    private static final String WOMAN = "woman";
    private static final String ALL = "all";
    private View rootView;
    private ListView listCloth;
    private Context context;
    private String query;
    private boolean canLoad=true;
    private static ArrayList<Image> cloth;
    private ApplicationSupport global;
    private SearchAdapterImage adapter;
    private int skip=0;
    private String sex;
    private Float prezzoDa;
    private Float prezzoA;
    private String order;

    String POPOLARITA="Popolarità";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_cloth, container, false);
        listCloth = (ListView) rootView.findViewById(R.id.clothlist);
        global = (ApplicationSupport) getActivity().getApplicationContext();

        System.out.println("create");

        cloth=global.getCloth();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listCloth.setAdapter(adapter);
        search();

        //setto il listener sullo scroller quando arrivo in fondo
        listCloth.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && cloth.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (cloth != null) {
                            canLoad = false;
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

    public void search(){
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query=query.trim().toLowerCase();

        //faccio la query a Parse
        //final ArrayList<Image> cloth=  SearchUtility.searchCloth(query, rootView);

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
        if(order==null) order=POPOLARITA;
        if(order.equals(POPOLARITA))queryFoto.addDescendingOrder("nLike");
        //if(order.equals() queryFoto.addAscendingOrder("nLike");
        queryFoto.setSkip(skip);
        queryFoto.setLimit(5);
        skip=skip+5;
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ListIterator<ParseObject> i = objects.listIterator();
                    while (i.hasNext()) {
                        List<String> tag = new ArrayList<String>();
                        ParseObject o = i.next();
                        tag = (ArrayList) o.get("tipo");
                        if (tag == null) tag = new ArrayList<String>(0);
                        for (int j = 0; j < tag.size(); j++) {
                            if (tag.get(j).contains(query)) {
                                final Image image = new Image(o);
                                if (!cloth.contains(image)) {
                                    System.out.println("sex: "+sex);
                                    if(sex!=ALL){
                                        ParseQuery<ParseObject> persona=new ParseQuery<ParseObject>("Persona");
                                        persona.whereEqualTo("username", o.getString("user"));
                                        persona.getFirstInBackground(new GetCallback<ParseObject>() {
                                            @Override
                                            public void done(ParseObject object, ParseException e) {
                                                if (e==null) {
                                                    if(object!=null) {
                                                        String s = object.getString("sex");
                                                        if (s == sex) {
                                                            cloth.add(image);
                                                            global.setCloth(cloth);
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    }
                                                }else check(e.getCode(),rootView,e.getMessage());
                                            }
                                        });
                                    }
                                    else {
                                        System.out.println("aggiungo all");
                                        cloth.add(image);
                                        global.setCloth(cloth);
                                        adapter.notifyDataSetChanged();
                                        //global.setLastCloth(o.getCreatedAt());
                                    }
                                }

                                break;

                            }
                        }
                    }
                    canLoad = true;

                } else check(e.getCode(), rootView, e.getMessage());
            }
        });




        listCloth.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                i.putExtra("classe", "FindCloth");
                i.putExtra("position", position);
                startActivity(i);
            }
        });



    }

    public Fragment newIstance(String query, String sex, Float priceFrom, Float priceTo, Context context, String order) {
        this.context = context;
        final FindClothFragment f = new FindClothFragment();
        final Bundle args = new Bundle();
        args.putString("query", query);
        args.putString("sex",sex);
        args.putFloat("prezzoDa", priceFrom);
        args.putFloat("prezzoA",priceTo);
        args.putString("order",order);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getArguments()!=null ? (String) getArguments().getString("query") : null;
        this.sex=getArguments()!=null ?(String) getArguments().getString("sex") :null;
        this.prezzoDa = getArguments()!=null ? (Float) getArguments().getFloat("prezzoDa") : null;
        this.prezzoA=getArguments()!=null ?(Float) getArguments().getFloat("prezzoA") :null;
        this.order=getArguments()!=null ? (String)getArguments().getString("order") :null;

    }

    public void refresh(String query) {
        System.out.println("resfresh");
        this.query=query.trim().toLowerCase();
        skip=0;
        cloth=new ArrayList<>();
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), cloth);
        listCloth.setAdapter(adapter);
        global.setTag(cloth);
        search();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        System.out.println("onresume");
        adapter.notifyDataSetChanged();
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            System.out.println("visibile");
            onResume();
        }
        else {
            System.out.println("nonvisibile");
        }
    }

    public static ArrayList<Image> getCloth() {
        return cloth;
    }
}
