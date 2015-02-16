package com.bioviz.ricardo.bioviz.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.activity.OccurrenceDetails;
import com.bioviz.ricardo.bioviz.adapters.OccurrenceListAdapter;
import com.bioviz.ricardo.bioviz.model.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.OccurrenceLookupResponse;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OccurrenceList extends Fragment implements OnItemClickListener, Response.Listener<JSONObject>, Response.ErrorListener, LocationListener {

    private RecyclerView mRecyclerView;
    private LinearLayout mEmptyView;
    private LinearLayout llQueryButtons;

    private SwipeRefreshLayout swipeRefreshLayout;

    private OccurrenceListAdapter mAdapter;
    private ArrayList<GBIFOccurrence> items;

    private Dialog dialog;
    private LocationManager locationManager;
    private String latitude;
    private String longitude;
    private String lastQuery;

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
        mAdapter = new OccurrenceListAdapter(items, this, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        swipeRefreshLayout.setVisibility(View.GONE);
        llQueryButtons.setVisibility(View.VISIBLE);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                executeQuery("");
            }
        });

        btRandomQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeQuery("");
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
        Intent myIntent = new Intent(getActivity(), OccurrenceDetails.class);
        myIntent.putExtra("item", new Gson().toJson(items.get(position)));
        getActivity().startActivity(myIntent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
    }

    @Override
    public void onResponse(JSONObject response) {

        OccurrenceLookupResponse responseObj =
                new Gson().fromJson(response.toString(), OccurrenceLookupResponse.class);

        llQueryButtons.setVisibility(View.GONE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setRefreshing(false);

        items = responseObj.getResults();
        if (items.size() == 0) {
            //load empty view
            mEmptyView.setVisibility(View.VISIBLE);
            llQueryButtons.setVisibility(View.GONE);
            swipeRefreshLayout.setVisibility(View.GONE);
        } else {
            mEmptyView.setVisibility(View.GONE);
            llQueryButtons.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            mAdapter = new OccurrenceListAdapter(items, this, getActivity());
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(getActivity(), "Something went wrong :o ", Toast.LENGTH_SHORT).show();
        llQueryButtons.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
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
                promptSearchParams();
                return true;
            case R.id.action_near_me:
                fetchLocalOccurrences();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void fetchLocalOccurrences() {
        boolean[] states = AppController.getStates();
        locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        //fine locaiton is required by the user
        if (states[3]) {

            //start location listener
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, OccurrenceList.this);

            swipeRefreshLayout.setRefreshing(true);

        } else { //it can be an aproximate location

            Location loc = getLastBestLocation();
            latitude = "" + loc.getLatitude();
            longitude = "" + loc.getLongitude();

            Toast.makeText(getActivity(),
                    "LAT: " + loc.getLatitude() + "\nLNG: " + loc.getLongitude(),
                    Toast.LENGTH_SHORT).show();

            executeQuery(lastQuery == null ? "" : lastQuery,
                    "&decimalLatitude=" + latitude,
                    "&decimalLongitude=" + longitude);
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
                    lastQuery = "&scientificName=" + sName.getText().toString();
                }
                executeQuery(lastQuery);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void executeQuery(String... params) {
        String request = Values.GBIFBaseAddr + Values.GBIFOccurrence + "/search?";

        boolean[] settings = AppController.getStates();

        if (settings[1]) {
            request += "&mediaType=StillImage";
        }

        //FIXME apparently the api simply ignores this -.-''
        if (settings[2]) {
            request+="&language=en";
        }

        for (String s : params) {
            request += s;
        }

        offset += 10;
        request += "&offset=" + offset;
        Log.e("REQUEST", request);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                request, null,
                this, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "occurrence_list_request");
    }

    @Override
    public void onLocationChanged(Location loc) {
        Toast.makeText(
                getActivity(),
                "Location changed: Lat: " + loc.getLatitude() + " Lng: "
                        + loc.getLongitude(), Toast.LENGTH_SHORT).show();

        longitude = "" + loc.getLongitude();
        latitude = "" + loc.getLatitude();

        if (lastQuery != null ||
                !lastQuery.equals("")) {

            executeQuery(lastQuery
                    + "&decimalLatitude=" + latitude
                    + "&decimalLongitude=" + longitude
                    );
        } else {
            executeQuery("&decimalLatitude=" + latitude
                    + "&decimalLongitude=" + longitude
                    );
        }

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0)
                System.out.println(addresses.get(0).getLocality());
            cityName = addresses.get(0).getLocality();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * @return the last know best location
     */
    private Location getLastBestLocation() {
        if (locationManager == null) {
            Toast.makeText(getActivity(), "Unable to fetch location", Toast.LENGTH_SHORT).show();
            return new Location("Porto");
        }
        Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
