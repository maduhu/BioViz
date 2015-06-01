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
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.media.iNatMediaAdapter;
import com.bioviz.ricardo.bioviz.model.iNat.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNat.iNatSpecies;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * A simple {@link Fragment} subclass.
 */
public class iNatMediaFragment extends Fragment implements OnItemClickListener {

    private iNatSpecies item;
    private iNatMediaAdapter mAdapter;

    public iNatMediaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_inat_media, container, false);

        // Inflate the layout for this fragment
        final RecyclerView descriptionList = (RecyclerView) rootView.findViewById(R.id.list_descriptions);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        mAdapter = new iNatMediaAdapter(item.getTaxon_photos(), this, getActivity());
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        return rootView;
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

        item = response;
        Toast.makeText(getActivity(), "GOT. " + response.getTaxon_photos().size(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }
}
