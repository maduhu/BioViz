package com.bioviz.ricardo.bioviz.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;

/**
 * A simple {@link Fragment} subclass.
 */
public class iNatMediaFragment extends Fragment {


    public iNatMediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inat_media, container, false);
    }


    public static iNatMediaFragment newInstance(iNatObservation observationItem) {
        return null;
    }
}
