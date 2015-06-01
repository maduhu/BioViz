package com.bioviz.ricardo.bioviz.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.Interface.OnObservationResponseListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.descriptions.iNatSpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.model.iNat.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNat.iNatSpecies;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class iNatDescriptionFragment extends Fragment implements OnItemClickListener, OnObservationResponseListener{

    private iNatObservation observationItem;
    private ArrayList<String> items;
    private iNatSpeciesDescriptionAdapter mAdapter;

    public iNatDescriptionFragment() {
        // Required empty public constructor
    }

    public static iNatDescriptionFragment newInstance(iNatObservation observationItem) {
        iNatDescriptionFragment fragment = new iNatDescriptionFragment();

        Bundle args = new Bundle();
        args.putString("item", new Gson().toJson(observationItem));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            observationItem = new Gson().fromJson(getArguments().getString(
                            "item"),
                    iNatObservation.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inat_description, container, false);

        final RecyclerView descriptionList = (RecyclerView) rootView.findViewById(R.id.list_inat_descriptions);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        mAdapter = new iNatSpeciesDescriptionAdapter(items, observationItem, this, getActivity());
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        return rootView;
    }




    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onResponse(JSONArray jsonArray) {
        if (jsonArray == null || jsonArray.equals("")) {
            return;
        }

        iNatSpecies response;
        try {
            response = new Gson().fromJson(jsonArray.get(0).toString(), iNatSpecies.class);
        } catch (JSONException e) {
            Toast.makeText(getActivity(), "Unable to load the resources", Toast.LENGTH_SHORT).show();
            return;
        }

        if (items == null)
            items = new ArrayList<>();

        items.add(
                response.getName()
                + " (\""
                + response.getUnique_name()
                + "\")");

        items.add(response.getWikipedia_title() + "\n\n\n" + response.getWikipedia_summary());

        String taxtonNames = "";
        for (iNatSpecies.TaxonNames tn : response.getTaxon_names()) {
            taxtonNames += tn.lexicon + " - " + tn.name;
        }

        items.add(taxtonNames);

        mAdapter.notifyDataSetChanged();
    }
}
