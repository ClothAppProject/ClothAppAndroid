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
import com.parse.ParseException;
import com.parse.ParseFile;
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
    private View rootView;
    private ListView listTag;
    private Context context;
    private String query;
    private boolean canLoad=true;
    private static ArrayList<Image> tag;
    private ApplicationSupport global;
    private SearchAdapterImage adapter;
    private int skip=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listTag = (ListView) rootView.findViewById(R.id.userlist);
        global = (ApplicationSupport) getActivity().getApplicationContext();
        tag=global.getTag();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), tag);
        listTag.setAdapter(adapter);
        search();

        //setto il listener sullo scroller quando arrivo in fondo
        listTag.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && tag.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (tag != null) {
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
        //List<User> user= SearchUtility.searchUser(query, rootView);
        //final ArrayList<Image> tag= SearchUtility.searchHashtag(query, rootView);
        //final ArrayList<Image> cloth=  SearchUtility.searchCloth(query, rootView);

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");


        List<ParseObject> objects= null;
        queryFoto.addDescendingOrder("nLike");
        queryFoto.setSkip(skip);
        queryFoto.setLimit(5);
        skip = skip + 5;
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    ListIterator<ParseObject> i = objects.listIterator();
                    while (i.hasNext()) {
                        List<String> hashtag = new ArrayList<String>();
                        ParseObject o = i.next();
                        hashtag = (ArrayList) o.get("hashtag");
                        if (hashtag == null) hashtag = new ArrayList<String>(0);
                        if (hashtag == null) hashtag = new ArrayList<String>(0);
                        for (int j = 0; j < hashtag.size(); j++) {
                            if (hashtag.get(j).contains(query)) {
                                Image image = new Image(o);
                                if (!tag.contains(image)) {
                                    tag.add(image);
                                    global.setTag(tag);
                                    adapter.notifyDataSetChanged();
                                    //global.setLastCloth(o.getCreatedAt());
                                }


                                break;

                            }
                        }
                    }
                    canLoad = true;
                } else check(e.getCode(), rootView, e.getMessage());
            }
        });




       //tag
        listTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                i.putExtra("classe", "FindTag");
                i.putExtra("position", position);
                startActivity(i);
            }
        });

;

        //allungo l'altezza della list view
        //setListViewHeightBasedOnItems(listView);
    }

    public Fragment newIstance(String query, Context context) {
        this.context = context;
        final FindTagFragment f = new FindTagFragment();
        final Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getArguments()!=null ? (String) getArguments().getString("query") : null;
    }

    public void refresh(String query) {
        this.query=query.trim().toLowerCase();
        skip=0;
        tag=new ArrayList<>();
        adapter = new SearchAdapterImage(getActivity().getBaseContext(), tag);
        listTag.setAdapter(adapter);
        global.setTag(tag);
        search();
    }


    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //System.out.println("onresume"+user);
        adapter.notifyDataSetChanged();
    }

    public static ArrayList<Image> getCloth() {
        return tag;
    }
}
