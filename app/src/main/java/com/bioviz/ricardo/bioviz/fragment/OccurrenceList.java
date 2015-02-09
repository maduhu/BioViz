package com.bioviz.ricardo.bioviz.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.MainActivity;
import com.bioviz.ricardo.bioviz.activity.OccurrenceDetails;
import com.bioviz.ricardo.bioviz.adapters.OccurrenceListAdapter;
import com.bioviz.ricardo.bioviz.model.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.OccurrenceLookupResponse;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class OccurrenceList extends Fragment implements OnItemClickListener, Response.Listener<JSONObject>, Response.ErrorListener {

    private RecyclerView mRecyclerView;
    private LinearLayout llQueryButtons;
    private Button btRandomQuery;
    private Button btTailoredQuery;
    private ProgressBar queryProgress;

    private OccurrenceListAdapter mAdapter;
    private ArrayList<GBIFOccurrence> items;
    private int offset;

    public static OccurrenceList newInstance() {
        OccurrenceList fragment = new OccurrenceList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public OccurrenceList() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_occurrence_list,
                container, false);

        setHasOptionsMenu(true);

        offset = 0;


        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_occurrences);
        llQueryButtons = (LinearLayout) rootView.findViewById(R.id.list_occurrences_buttons);
        btRandomQuery = (Button) rootView.findViewById(R.id.list_occurrences_random);
        btTailoredQuery = (Button) rootView.findViewById(R.id.list_occurrences_search);
        queryProgress = (ProgressBar) rootView.findViewById(R.id.list_occurrences_progress);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new OccurrenceListAdapter(items, this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.setVisibility(View.GONE);
        queryProgress.setIndeterminate(false);
        llQueryButtons.setVisibility(View.VISIBLE);

        btRandomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeQuery("");
            }
        });

        btTailoredQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeQuery("&limit=2");
            }
        });


        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        //TODO launch preview activity
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onResponse(JSONObject response) {
        queryProgress.setIndeterminate(false);

        OccurrenceLookupResponse responseObj =
                new Gson().fromJson(response.toString(), OccurrenceLookupResponse.class);

        llQueryButtons.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);

        items = responseObj.getResults();
        mAdapter = new OccurrenceListAdapter(items, this, getActivity());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        queryProgress.setIndeterminate(false);

        Toast.makeText(getActivity(), "Something went wrong :o ", Toast.LENGTH_SHORT).show();
        llQueryButtons.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_occurrence_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                executeQuery("");
                return true;
            case R.id.action_search:
                executeQuery("&limit=2");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void executeQuery(String params) {

        queryProgress.setIndeterminate(true);
        String request = Values.GBIFBaseAddr + Values.GBIFOccurrence + "/search?mediaType=StillImage";
        offset += 10;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                request + "&offset=" + offset + params, null,
                this, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "occurrence_list_request");
    }
}
