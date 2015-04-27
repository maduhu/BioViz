package com.bioviz.ricardo.bioviz.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.Interface.OnOccurrenceResponseListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.descriptions.SpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFSpeciesDescription;
import com.bioviz.ricardo.bioviz.model.GBIF.Responses.GBIFSpeciesDescriptionLookupResponse;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link OccurrenceDescriptionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OccurrenceDescriptionsFragment extends Fragment implements OnItemClickListener, OnOccurrenceResponseListener {

    private static GBIFOccurrence occurrenceItem;
    private ArrayList<GBIFSpeciesDescription> items;
    private SpeciesDescriptionAdapter mAdapter;

    public static OccurrenceDescriptionsFragment newInstance(GBIFOccurrence item) {
        OccurrenceDescriptionsFragment fragment = new OccurrenceDescriptionsFragment();

        Bundle args = new Bundle();
        args.putString("item", new Gson().toJson(item));
        fragment.setArguments(args);
        return fragment;
    }

    public OccurrenceDescriptionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
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
        mAdapter = new SpeciesDescriptionAdapter(items, occurrenceItem, this, getActivity());
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getActivity(), "Click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Toast.makeText(getActivity(), "Long click", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        GBIFSpeciesDescriptionLookupResponse response = new Gson().fromJson(jsonObject.toString(), GBIFSpeciesDescriptionLookupResponse.class);
        ArrayList<GBIFSpeciesDescription> fetchedDescriptions = response.getResults();
        if (response.getResults() == null) {
            Toast.makeText(getActivity(), "No descriptions available for this species", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean[] settings = AppController.getStates();

        for (GBIFSpeciesDescription description : fetchedDescriptions) {
            if (description.getDescription() != null &&
                    !description.getDescription().equals("")) {

                if (settings[2]) {
                    if (description.getLanguage().equals("eng")) {
                        items.add(description);
                    }
                } else {
                    items.add(description);
                }
            }
        }

        items.add(new GBIFSpeciesDescription());
        mAdapter.notifyDataSetChanged();
    }
}
