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
    private ArrayList<Image> tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_user, container, false);
        listTag = (ListView) rootView.findViewById(R.id.userlist);
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
                            int toDownload = 6;
                            final int maxNumLike = tag.get(tag.size() - 1).getNumLike();
                            ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");
                            queryFoto.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if(e==null){
                                        ListIterator<ParseObject> i = objects.listIterator();
                                        while (i.hasNext()) {
                                            List<String> hashtag = new ArrayList<String>();
                                            ParseObject o = i.next();
                                            hashtag = (ArrayList) o.get("hashtag");
                                            if (hashtag == null) hashtag = new ArrayList<String>(0);
                                            for (int j = 0; j < hashtag.size(); j++) {
                                                if (hashtag.get(j).contains(query)) {

                                                    tag.add(new Image(o));
                                                    setListViewHeightBasedOnItems();
                                                    break;

                                                }
                                            }
                                        }
                                    }
                                    else check(e.getCode(), rootView, e.getMessage());
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
        //List<User> user= SearchUtility.searchUser(query, rootView);
        //final ArrayList<Image> tag= SearchUtility.searchHashtag(query, rootView);
        //final ArrayList<Image> cloth=  SearchUtility.searchCloth(query, rootView);

        ParseQuery<ParseObject> queryFoto = new ParseQuery<ParseObject>("Photo");

         tag=new ArrayList<Image>();
        List<ParseObject> objects= null;
        queryFoto.setLimit(4);
        queryFoto.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    ListIterator<ParseObject> i = objects.listIterator();
                    while (i.hasNext()) {
                        List<String> hashtag = new ArrayList<String>();
                        ParseObject o = i.next();
                        hashtag = (ArrayList) o.get("hashtag");
                        if (hashtag == null) hashtag = new ArrayList<String>(0);
                        for (int j = 0; j < hashtag.size(); j++) {
                            if (hashtag.get(j).contains(query)) {

                                tag.add(new Image(o));
                                setListViewHeightBasedOnItems();
                                break;

                            }
                        }
                    }
                }
                else check(e.getCode(), rootView, e.getMessage());
            }
        });




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

        TextView t=(TextView)rootView.findViewById(R.id.textView);
        if(adapterI.getCount()==0) t.setVisibility(View.INVISIBLE);

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

        ListAdapter listAdapter = listTag.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            for (itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listTag);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listTag.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listTag.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listTag.setLayoutParams(params);
            listTag.requestLayout();

            return true;

        } else {
            return false;
        }

    }
}
