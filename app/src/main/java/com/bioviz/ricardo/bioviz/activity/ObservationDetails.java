package com.bioviz.ricardo.bioviz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.SpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.adapters.iNatSpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFSpeciesLookupResponse;
import com.bioviz.ricardo.bioviz.model.GBIFSpeciesDescription;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation;
import com.bioviz.ricardo.bioviz.model.iNatResponses.iNatSpeciesLookupResponse;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;


public class ObservationDetails  extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener, OnItemClickListener {

    private iNatObservation observationItem;
    private ArrayList<Object> items;
    private iNatSpeciesDescriptionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_details);

        String itemString;

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            itemString = intent.getStringExtra("item");
        } else {
            itemString = savedInstanceState.getString("item");
        }

        observationItem = new Gson().fromJson(itemString, iNatObservation.class);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //http://www.inaturalist.org/taxa/search.json?q=merriam kangaroo rat

        final RecyclerView descriptionList = (RecyclerView) findViewById(R.id.list_descriptions);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);


        items = new ArrayList<>();
        mAdapter = new iNatSpeciesDescriptionAdapter(items, observationItem, this, this);
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        if (getActionBar() != null) {
            getActionBar().setTitle(observationItem.getSpecies_guess());
            getActionBar().setSubtitle("Species overview");
        }

        String request = Values.iNATBaseAddr + "taxa/search.json?q=" + observationItem.getSpecies_guess();

        //lookup species with speciesKey
        Log.e("REQUEST", request);
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                request, null,
                this, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "inat_species_description_lookup");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_occurrence_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       /* int id = item.getItemId();

        if (id ==  android.R.id.home) {
            AppController.getInstance().cancelPendingRequests("species_description_lookup");
            finish();
            return true;
        } else if (id == R.id.action_share_occurrence) {

            String body = occurrenceItem.getScientificName() + "\n\n";

            for (GBIFSpeciesDescription description : items) {
                body += "\n"
                        + description.getType() + "\n"
                        + description.getDescription();
            }

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, body);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }*/
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {

        iNatSpeciesLookupResponse response = new Gson().fromJson(jsonObject.toString(), iNatSpeciesLookupResponse.class);
       /*
        ArrayList<GBIFSpeciesDescription> fetchedDescriptions = response.getResults();
        if (response.getResults() == null) {
            Toast.makeText(this, "No descriptions available for this species", Toast.LENGTH_SHORT).show();
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
        */
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(this, "Something went wrong :| ", Toast.LENGTH_SHORT).show();
        loadErrorView();
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    /**
     * Loads the view showing that there are no descriptions available
     */
    private void loadErrorView() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.view_woops, null);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        (v.findViewById(R.id.tvKittenMode)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.google.pt/search?q=kittens&source=lnms&tbm=isch&sa=X");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });


        LinearLayout llDetails = (LinearLayout) findViewById(R.id.ll_occurrence_details);
        llDetails.removeAllViews();
        llDetails.addView(v, params);
    }
/*

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("item", new Gson().toJson(occurrenceItem));
    }*/
}