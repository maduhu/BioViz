package com.bioviz.ricardo.bioviz.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bioviz.ricardo.bioviz.biointerface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.biointerface.OnObservationResponseListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.descriptions.iNatSpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.model.iNat.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNat.iNatSpecies;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONArray;

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

        if (getArguments() == null) {
            Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }

        observationItem = new Gson().fromJson(getArguments().getString(
                        "item"),
                iNatObservation.class);

        Toast.makeText(getActivity(), observationItem.getSpecies_guess(), Toast.LENGTH_SHORT).show();
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

        ArrayList<iNatSpecies> response;
        //iNatSpecies response;

        response = new Gson().fromJson(jsonArray.toString(), Values.ARRAY_INAT_SPECIES);


        if (items == null)
            items = new ArrayList<>();

        //well someone out there in the iNat dev team found it useful to
        // allow null values for data. wonderful.
        for (int i = 0; i < response.size(); ++i) {
            iNatSpecies species = response.get(i);

            items.add(species.getName() + " (\""  + species.getUnique_name()  + "\")");
            items.add(
                    (species.getWikipedia_title() == null ? "" : species.getWikipedia_title() + "\n\n") +
                    (species.getWikipedia_summary() == null ? "" : species.getWikipedia_summary() + "\n\n"));

            String lexicon = "";

            for (iNatSpecies.TaxonNames tn : species.getTaxon_names()) {
                if (tn.name == null || tn.lexicon == null)
                    continue;

                lexicon += tn.name + " (" + tn.lexicon + ")\n\n";
            }

            items.add(lexicon);
        }

        mAdapter.notifyDataSetChanged();
    }
}
