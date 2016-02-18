package com.clothapp.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

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
public class FindTagFragment extends Fragment {
    private View rootView;
    private ListView listTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listTag = (ListView) rootView.findViewById(R.id.userlist);
        SearchResultsActivity activity = (SearchResultsActivity) getActivity();
        String query = activity.getQuery();
        search(query);
        return rootView;
    }

    public void search(String query){
        //se si utilizzano altre tastiere (come swiftkey) viene aggiunto uno spazio quindi lo tolgo
        query=query.trim();





        //faccio la query a Parse
        //List<User> user= SearchUtility.searchUser(query, rootView);
        final ArrayList<Image> tag= SearchUtility.searchHashtag(query, rootView);
        //final ArrayList<Image> cloth=  SearchUtility.searchCloth(query, rootView);




       //tag
        final SearchAdapterImage adapterI=new SearchAdapterImage(getActivity().getBaseContext(),tag);
        listTag.setAdapter(adapterI);
        listTag.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity().getApplicationContext(), ImageFragment.class);
                i.putExtra("position", position);
                //passo la lista delle foto al fragment
                i.putExtra("lista", tag);
                startActivity(i);
            }
        });


        //allungo l'altezza della list view
        //setListViewHeightBasedOnItems(listView);
    }
}
