package com.clothapp.home_gallery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.clothapp.R;

/**
 * Created by giacomoceribelli on 02/02/16.
 */
public class HomePopularShopsFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_home_popular, container, false);

            return rootView;
        }


}
