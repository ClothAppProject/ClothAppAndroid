package com.clothapp.search;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

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
    private boolean canLoad=true;
    private ArrayList<User> user;
    ApplicationSupport global;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listUser = (ListView) rootView.findViewById(R.id.userlist);
        //String query=getArguments().getString("name");
        global = (ApplicationSupport) getActivity().getApplicationContext();

        search();

        listUser.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                System.out.println("user "+firstVisibleItem +"+"+ visibleItemCount +">="+ totalItemCount);
                if (firstVisibleItem + visibleItemCount >=totalItemCount) {
                    //se ho raggiunto l'ultima immagine in basso carico altre immagini
                    if (canLoad && user.size() > 0) { //controllo se size>0 perchè altrimenti chiama automaticamente all'apertura dell'activity
                        if (user != null) {
                            canLoad = false;
                            ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
                            queryUser.addAscendingOrder("username");
                            queryUser.whereContains("username", query);
                            //queryUser.orderByDescending("username");
                            queryUser.setLimit(6);
                            queryUser.whereGreaterThan("username", global.getLastUser().getUsername());
                            queryUser.findInBackground(new FindCallback<ParseUser>() {
                                @Override
                                public void done(final List<ParseUser> o, ParseException e) {
                                    if (e == null) {
                                        User u = null;

                                        for (int i = 0; i < o.size(); i++) {
                                            //System.out.println(o.get(i)+"aggiunto"+o.get(i).getString("username"));
                                            final ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("UserPhoto");
                                            queryFoto.whereEqualTo("username", o.get(i).getString("username"));

                                            u = new User(o.get(i));


                                            try {

                                                ParseObject object = queryFoto.getFirst();
                                                u.setProfilo(object.getParseFile("profilePhoto").getFile());
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }


                                            user.add(u);
                                            setListViewHeightBasedOnItems();


                                        }
                                        global.setLastUser(u);
                                    } else check(e.getCode(), rootView, e.getMessage());
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

        return rootView;
    }

    public void search(){
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query=query.trim();



        //faccio la query a Parse
       // List<User> user= SearchUtility.searchUser(query, rootView);


        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.addAscendingOrder("username");
        queryUser.whereContains("username", query);
        //queryUser.orderByDescending("username");
        queryUser.setLimit(9);
        user=new ArrayList<User>();

        queryUser.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(final List<ParseUser> o, ParseException e) {
                if(e==null) {
                    User u=null;
                    for (int i = 0; i < o.size(); i++) {
                        //System.out.println(o.get(i)+"aggiunto"+o.get(i).getString("username"));
                        final ParseQuery<ParseObject> queryFoto=new ParseQuery<ParseObject>("UserPhoto");
                        queryFoto.whereEqualTo("username", o.get(i).getString("username"));
                        u=new User(o.get(i));
                        try {
                            ParseObject object=queryFoto.getFirst();
                            u.setProfilo(object.getParseFile("profilePhoto").getFile());
                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }
                        user.add(u);
                        setListViewHeightBasedOnItems();

                    }
                    canLoad = true;
                    global.setLastUser(u);
                }
                else check(e.getCode(), rootView, e.getMessage());
            }
        });



        //chiama l'adattatore che inserisce gli item nella listview
        final SearchAdapterUser adapter =new SearchAdapterUser(getActivity().getBaseContext(),user);
        listUser.setAdapter(adapter);

        //se clicco su un user mi apre il suo profilo
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
    }

    public void refresh(String query) {
        this.query=query;
        // The reload fragment code here !
        if (! this.isDetached()) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

    public boolean setListViewHeightBasedOnItems() {

        ListAdapter listAdapter = listUser.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            for (itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listUser);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listUser.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listUser.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listUser.setLayoutParams(params);
            listUser.requestLayout();

            return true;

        } else {
            return false;
        }

    }


}

