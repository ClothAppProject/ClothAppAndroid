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

import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.ApplicationSupport;
import com.clothapp.resources.User;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

/**
 * Created by jack1 on 18/02/2016.
 */
public class FindUserFragment extends Fragment {

    private View rootView;
    private ListView listUser;
    private Context context;
    private String query;
    private List<File> foto=new ArrayList<File>();
    private boolean canLoad=false;
    private ArrayList<User> user;
    ApplicationSupport global;
    int skip=0;
    private SearchAdapterUser adapter;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //System.out.println("onCreateView"+query);
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listUser = (ListView) rootView.findViewById(R.id.userlist);
        //String query=getArguments().getString("name");
        global = (ApplicationSupport) getActivity().getApplicationContext();

        user=global.getUsers();
        //chiama l'adattatore che inserisce gli item nella listview
        adapter = new SearchAdapterUser(getActivity().getBaseContext(), user);
        listUser.setAdapter(adapter);
        search();


        listUser.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && user.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (user != null) {
                            canLoad = false;
                            //faccio la query a Parse
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



        final int[] i = {0};

        //faccio la query a Parse. Questa in background è più veloce ma non consente di metterli ordinati alfabeticamente
        //BUG: foto profilo

        final ParseQuery<ParseUser>username=ParseUser.getQuery();
        username.addAscendingOrder("lowercase");
        username.orderByAscending("lowercase");
        username.whereContains("lowercase", query);
        username.setSkip(skip);
        username.setLimit(10);
        skip=skip+10;
        username.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (ParseUser parseUser : objects) {
                        final User u = new User();
                        //setto il profilo
                        u.setUser(parseUser);
                        //cero la foto profilo
                        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
                        //System.out.println(parseUser.getUsername());
                        queryFoto.whereEqualTo("username", parseUser.getUsername());
                        queryFoto.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {

                                //se trovo la foto profilo...
                                if (object != null) try {
                                    ParseFile parseFile = object.getParseFile("thumbnail");
                                    // System.out.println(parseFile);
                                    if (parseFile != null) u.setProfilo(parseFile.getFile());
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }


                                //aggiungo l'utente nella lista
                                if (!user.contains(u)) {
                                    user.add(u);
                                    global.setUsers(user);
                                    adapter.notifyDataSetChanged();
                                }


                            }
                        });

                        i[0]++;
                        if (i[0] == objects.size() - 1) canLoad = true;
                    }
                } else check(e.getCode(), rootView, e.getMessage());


            }
        });

        listUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = ProfileUtils.goToProfile(getActivity().getApplicationContext(), adapter.getItem(position).getUsername());
                startActivity(i);
            }
        });



    }



    public Fragment newIstance(String query, Context context) {
        this.context = context;
        final FindUserFragment f = new FindUserFragment();
        final Bundle args = new Bundle();
        args.putString("query", query);
        f.setArguments(args);
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.query = getArguments()!=null ? (String) getArguments().getString("query") : null;
        //System.out.println("onCreate");
    }

    public void refresh(String query) {
        this.query=query.trim().toLowerCase();
        skip=0;
        user=new ArrayList<>();
        adapter = new SearchAdapterUser(getActivity().getBaseContext(), user);
        listUser.setAdapter(adapter);
        search();
    }
    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        //System.out.println("onresume"+user);
        adapter.notifyDataSetChanged();
    }


    public void setQuery(String query) {
        this.query=query;
       //
    }
}

