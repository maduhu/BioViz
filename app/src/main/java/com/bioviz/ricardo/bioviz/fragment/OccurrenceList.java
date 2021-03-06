package com.bioviz.ricardo.bioviz.fragment;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.biointerface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.biointerface.OnObservationResponseListener;
import com.bioviz.ricardo.bioviz.biointerface.OnOccurrenceResponseListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.GBIFOccurrenceDetails;
import com.bioviz.ricardo.bioviz.activity.iNatObservationDetails;
import com.bioviz.ricardo.bioviz.adapters.QueryResultListAdapter;
import com.bioviz.ricardo.bioviz.model.GBIF.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIF.Responses.OccurrenceLookupResponse;
import com.bioviz.ricardo.bioviz.model.iNat.iNatObservation;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OccurrenceList extends Fragment implements OnItemClickListener, ConnectionCallbacks,
        OnConnectionFailedListener, Response.ErrorListener {

    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private LinearLayout llQueryButtons;

    private SwipeRefreshLayout swipeRefreshLayout;

    private QueryResultListAdapter mAdapter;
    private ArrayList<Object> items;

    private Map<String, String> gbifQuery;
    private Map<String, String> iNatQuery;

    private Dialog dialog;

    private boolean occurrenceReady;
    private boolean observationReady;

    private OnObservationResponseListener iNatResponseListener;
    private OnOccurrenceResponseListener GBIFResponseListener;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

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
        gbifQuery = new HashMap<>();
        iNatQuery = new HashMap<>();
        dialog = new Dialog(getActivity());

        Button btRandomQuery = (Button) rootView.findViewById(R.id.list_occurrences_random);
        Button btTailoredQuery = (Button) rootView.findViewById(R.id.list_occurrences_search);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_occurrences);
        mEmptyView = (LinearLayout) rootView.findViewById(R.id.list_occurrences_empty);
        llQueryButtons = (LinearLayout) rootView.findViewById(R.id.list_occurrences_buttons);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        items = new ArrayList<>();
        mAdapter = new QueryResultListAdapter(items, this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setVisibility(View.GONE);
        llQueryButtons.setVisibility(View.VISIBLE);

        // Check availability of play services
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        mEmptyView.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        iNatResponseListener = new OnObservationResponseListener() {

            @Override
            public void onResponse(JSONArray jsonArray) {

                if (items == null) {
                    items = new ArrayList<>();
                }

                ArrayList<iNatObservation> responses =
                        new Gson().fromJson(jsonArray.toString(), Values.ARRAY_INAT_OBSERVATIONS);

                //Apparently this post processing is required as in some cases has[]=photos is ignored
                // and some of the values can be null
                for (iNatObservation obs : responses) {
                    if (obs.getSpecies_guess() != null
                            && obs.getPlace_guess() != null) {

                        boolean[] settings = AppController.getStates();

                        if (settings[1]) {
                            if (obs.getPhotos().size() != 0) {
                                items.add(obs);
                            }
                        } else {
                            items.add(obs);
                        }

                    }
                }

                observationReady = true;
                notifyResponseReady();
            }
        };

        GBIFResponseListener = new OnOccurrenceResponseListener() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                OccurrenceLookupResponse responseObj =
                        new Gson().fromJson(jsonObject.toString(), OccurrenceLookupResponse.class);

                if (items == null)
                    items = new ArrayList<>();

                items.addAll(responseObj.getResults());

                occurrenceReady = true;
                notifyResponseReady();
            }
        };

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                items.clear();
                mAdapter.notifyDataSetChanged();
                iNatQuery.clear();
                gbifQuery.clear();

                executeGBIFQuery(true);
                executeiNATQuery();
            }
        });

        btRandomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeRefreshLayout.setRefreshing(true);
                executeGBIFQuery(true);
                executeiNATQuery();
            }
        });
        btTailoredQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSearchParams();
            }
        });

        return rootView;
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent myIntent;

        if (items.get(position) instanceof GBIFOccurrence) {
            //myIntent = new Intent(getActivity(), OccurrenceDetails.class);
            if (((GBIFOccurrence) items.get(position)).getSpeciesKey() == null) {
                Toast.makeText(
                        getActivity(),
                        "Sorry, but this item cannon be displayed",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            myIntent = new Intent(getActivity(), GBIFOccurrenceDetails.class);
        } else {
            myIntent = new Intent(getActivity(), iNatObservationDetails.class);
        }

        myIntent.putExtra("item", new Gson().toJson(items.get(position)));
        ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                0, view.getWidth(), view.getHeight());
        getActivity().startActivity(myIntent, options.toBundle());
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_occurrence_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        observationReady = false;
        occurrenceReady = false;
        switch (item.getItemId()) {
            case R.id.action_refresh:
                items.clear();
                executeGBIFQuery(true);
                executeiNATQuery();
                return true;
            case R.id.action_search:
                promptSearchParams();
                return true;
            case R.id.action_near_me:
                updateLocation();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Displays an alert dialog to get the search query form the user
     */
    private void promptSearchParams() {

        dialog.setContentView(R.layout.dialog_occurrence_search);
        dialog.setTitle("Search for an occurrence");
        dialog.setCancelable(false);

        // set the custom dialog components - text, image and button
        final EditText sName = (EditText) dialog.findViewById(R.id.occurrence_search_sname);
        final ImageButton btSubmit = (ImageButton) dialog.findViewById(R.id.occurrence_search_submit);

        (dialog.findViewById(R.id.occurrence_search_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!sName.getText().toString().equals("")) {
                    iNatQuery.put("q", sName.getText().toString());
                    gbifQuery.put("scientificName", sName.getText().toString());
                }

                items.clear();
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(true);

                executeGBIFQuery(false);
                executeiNATQuery();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void executeGBIFQuery(boolean randomQuery) {
        String request = Values.GBIFBaseAddr + Values.GBIFOccurrence + "/search?";
        boolean[] settings = AppController.getStates();
        occurrenceReady = false;

        if (gbifQuery == null) {
            gbifQuery = new HashMap<>();
        }

        //no params == random query, can use the offset to retrieve other results
        if (randomQuery) {
            offset += 10;
            gbifQuery.clear();
            gbifQuery.remove("offset");
            gbifQuery.put("offset", "" + offset);
        } else {
            gbifQuery.remove("offset");
        }

        if (settings[1]) {
            gbifQuery.put("mediaType", "StillImage");
        }

        //apparently the api simply ignores this
        if (settings[2]) {
            gbifQuery.put("language", "en");
        }

        for (Map.Entry<String, String> entry : gbifQuery.entrySet()) {
            request += "&" + entry.getKey() + "=" + entry.getValue();
        }

        Log.e("REQUEST", request);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                request, null,
                GBIFResponseListener, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "occurrence_list_request");
    }

    private void executeiNATQuery() {
        String request = Values.iNATBaseAddr + Values.iNatObservation + ".json" + "?";
        boolean[] settings = AppController.getStates();
        observationReady = false;

        if (settings[1]) {
            if (iNatQuery == null) {
                iNatQuery = new HashMap<>();
            }
            iNatQuery.put("has[]", "photo");
        }
        iNatQuery.put("limit", "20");

        //no params == random query, can use the offset to retrieve other results
        if (iNatQuery != null && !iNatQuery.isEmpty()) {
            for (Map.Entry<String, String> entry : iNatQuery.entrySet()) {
                request += "&" + entry.getKey() + "=" + entry.getValue();
            }
        }

        Log.e("REQUEST", request);

        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                request, iNatResponseListener, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "occurrence_list_request");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Check google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getActivity(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    /**
     * Get location coordinates and launch the api request
     * */
    private void updateLocation() {

        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Toast.makeText(getActivity(), latitude + ", " + longitude, Toast.LENGTH_SHORT).show();

            DecimalFormat df = new DecimalFormat("#.#");
            gbifQuery.put("decimalLatitude", df.format(latitude));
            gbifQuery.put("decimalLongitude", df.format(longitude));

            if (gbifQuery.containsKey("mediaType")) {
                gbifQuery.remove("mediaType");
            }

            if (gbifQuery.containsKey("offset")) {
                gbifQuery.remove("offset");
            }

            executeGBIFQuery(false);
            double geoThreshold = 1d;
            iNatQuery.put("swlat", df.format(latitude - geoThreshold));
            iNatQuery.put("swlng", df.format(longitude - geoThreshold));
            iNatQuery.put("nelat", df.format(latitude + geoThreshold));
            iNatQuery.put("nelng", df.format(longitude + geoThreshold));
            executeiNATQuery();
            //TODO execute iNat query with geo bound box

        } else {
            Toast.makeText(getActivity(), "Couldn't get the location. Make sure location is enabled on the device", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Update results list, if applicable
     */
    private void notifyResponseReady() {

        if (!occurrenceReady  || !observationReady)
            return;

        swipeRefreshLayout.setRefreshing(false);

        if (items == null)
            items = new ArrayList<>();

        //no results :'(
        if (items.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
            llQueryButtons.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
            return;
        }

        long seed = System.nanoTime();
        Collections.shuffle(items, new Random(seed));

        //there is hope!
        if (mAdapter == null) {
            mAdapter = new QueryResultListAdapter(items, OccurrenceList.this, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }

        mEmptyView.setVisibility(View.GONE);
        llQueryButtons.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("CONN", "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {
        // Once connected with google api, get the location
        //Toast.makeText(getActivity(), "Successfully connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(getActivity(), "Something went wrong :o ", Toast.LENGTH_SHORT).show();
        if (items.size() == 0) {
            llQueryButtons.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
    }
}
