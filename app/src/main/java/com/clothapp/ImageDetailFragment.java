package com.clothapp;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.clothapp.home_gallery.MyListAdapter;
import com.clothapp.resources.Cloth;
import com.clothapp.resources.Image;
import com.clothapp.resources.MyCardListAdapter;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetFileCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ProgressCallback;

import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.clothapp.resources.ExceptionCheck.check;

public class ImageDetailFragment extends Fragment {

    private String user;
    private ImageView v;
    private TextView t;
    private DonutProgress donutProgress;
    private String Id;
    private static Context context;
    private List<Cloth> vestiti;
    private ListView listView;
    private TextView hashtag;

    public static ImageDetailFragment newInstance(String id, Context c) {
        context = c;
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("ID", id);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.Id = getArguments() != null ? getArguments().getString("ID") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        t=(TextView)rootView.findViewById(R.id.user);
        v=(ImageView) rootView.findViewById(R.id.photo);
        listView=(ListView)rootView.findViewById(R.id.listInfo);
        hashtag=(TextView)rootView.findViewById(R.id.hashtag);

        //trovo le info delle foto e le inserisco nella view
        //findInfoPhoto();

        //donutProgress = (DonutProgress) rootView.findViewById(R.id.donut_progress);
        return rootView;
    }

    //TODO:fare la query giusta! questa è fittizia
    public void findInfoPhoto(){
        //qui scarico le foto
        final View vi = new View(getActivity().getApplicationContext());
        final ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", this.Id);



    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        //faccio query al database per scaricare la foto
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Photo");
        query.whereEqualTo("objectId", Id);
        query.orderByDescending("createdAt");
        try {
            List<ParseObject> objects = query.find();

            user = objects.get(0).getString("user");
            t.setText(user);
            t.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getActivity().getApplicationContext(), ProfileActivity.class);
                    i.putExtra("user", user);
                    startActivity(i);
                    getActivity().finish();
                }
            });

            //setto gli hashtag
            ArrayList tag=(ArrayList)objects.get(0).get("hashtag");
            String s=" ";
            if(tag!=null) {
                for (int i = 0; i < tag.size(); i++) {
                    s += tag.get(i).toString() + " ";
                }
            }
            hashtag.setText((CharSequence) s);

            //per ogni vestito cerco le informazioni
            ArrayList arrayList= (ArrayList) objects.get(0).get("vestiti");
            if(arrayList==null) arrayList=new ArrayList<Cloth>();
            vestiti=new ArrayList<Cloth>(arrayList.size());
            for(int i=0;i<arrayList.size();i++) {
                ParseQuery<ParseObject> query1 = new ParseQuery<ParseObject>("Vestito");
                query1.whereEqualTo("codice", arrayList.get(i));
                query1.getFirstInBackground(new GetCallback<ParseObject>() {
                    @Override
                    public void done(ParseObject info, ParseException e) {
                        if (e == null) {
                            Cloth c = new Cloth(info.getString("tipo"),
                                    info.getString("luogoAcquisto"),
                                    info.getString("prezzo"),
                                    info.getString("shop"),
                                    info.getString("brand"));

                            vestiti.add(c);
                            MyCardListAdapter adapter = new MyCardListAdapter(getActivity().getApplicationContext(), vestiti);
                            listView.setAdapter(adapter);
                            setListViewHeightBasedOnItems(listView);
                        }

                    }
                });

            }

            objects.get(0).getParseFile("photo").getFileInBackground(new GetFileCallback() {
                @Override
                public void done(File file, ParseException e) {
                    if (e == null) {
                        //nascondo caricamento mostro immagine
                        //donutProgress.setVisibility(View.INVISIBLE);
                        //v.setVisibility(View.VISIBLE);
                        Glide.with(context)
                                .load(file)
                                .into(v);


                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer percentDone) {
                    //passo percentuale
                    // donutProgress.setProgress(percentDone);
                }
            });



        } catch (ParseException e) {
            //errore chiamata
        }
    }

    public static boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            int itemPos;
            for (itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                item.measure(0, 0);
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight;
            listView.setLayoutParams(params);
            listView.requestLayout();

            return true;

        } else {
            return false;
        }

    }

}
