package com.clothapp.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.clothapp.ImageFragment;
import com.clothapp.R;
import com.clothapp.profile.utils.ProfileUtils;
import com.clothapp.resources.Image;

import com.clothapp.resources.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jack1 on 18/02/2016.
 */
public class FindUserFragment extends Fragment {

    private View rootView;
    private ListView listUser;
    private Context context;
    private String query;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listUser = (ListView) rootView.findViewById(R.id.userlist);
        //String query=getArguments().getString("name");

        search();
        return rootView;
    }

    public void search(){
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query=query.trim();



        //faccio la query a Parse
        List<User> user= SearchUtility.searchUser(query, rootView);



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
}

