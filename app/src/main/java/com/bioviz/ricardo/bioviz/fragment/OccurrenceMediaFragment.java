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
import com.bioviz.ricardo.bioviz.Interface.OnOccurrenceResponseListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.media.SpeciesMediaAdapter;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFMediaElement;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIF.Responses.GBIFMediaLookupResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class OccurrenceMediaFragment extends Fragment implements OnOccurrenceResponseListener, OnItemClickListener {

    private static GBIFOccurrence occurrenceItem;
    private ArrayList<GBIFMediaElement> items;
    private SpeciesMediaAdapter mAdapter;

    public static OccurrenceMediaFragment newInstance(GBIFOccurrence item) {
        OccurrenceMediaFragment fragment = new OccurrenceMediaFragment();

        Bundle args = new Bundle();
        args.putString("item", new Gson().toJson(item));
        fragment.setArguments(args);
        return fragment;
    }

    public OccurrenceMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            occurrenceItem = new Gson().fromJson(getArguments().getString(
                            "item"),
                    GBIFOccurrence.class);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_occurrence_details, container, false);

        final RecyclerView descriptionList = (RecyclerView) rootView.findViewById(R.id.list_descriptions);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        mAdapter = new SpeciesMediaAdapter(items, occurrenceItem, this, getActivity());
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        GBIFMediaLookupResponse response = new Gson().fromJson(jsonObject.toString(), GBIFMediaLookupResponse.class);
        if (response.getResults() == null) {
            Toast.makeText(getActivity(), "No descriptions available for this species", Toast.LENGTH_SHORT).show();
            return;
        }

        items.addAll(response.getResults());
        items.add(new GBIFMediaElement());

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
    }
}