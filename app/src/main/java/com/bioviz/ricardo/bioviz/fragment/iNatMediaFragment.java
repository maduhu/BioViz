package com.bioviz.ricardo.bioviz.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatSpecies;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

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
        iNatMediaFragment fragment = new iNatMediaFragment();

        Bundle args = new Bundle();
        args.putString("item", new Gson().toJson(observationItem));
        fragment.setArguments(args);
        return fragment;
    }

    public void onResponse(JSONArray jsonArray) {

        iNatSpecies response;
        try {
            response = new Gson().fromJson(jsonArray.get(0).toString(), iNatSpecies.class);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Unable to load the resources", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Toast.makeText(getActivity(), "GOT. " + response.getTaxon_photos().size(), Toast.LENGTH_SHORT).show();
    }
}
