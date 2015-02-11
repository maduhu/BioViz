package com.bioviz.ricardo.bioviz.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.Interface.OnItemClickListener;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.adapters.SpeciesDescriptionAdapter;
import com.bioviz.ricardo.bioviz.model.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFSpeciesLookupResponse;
import com.bioviz.ricardo.bioviz.model.GBIFSpeciesDescription;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class OccurrenceDetails extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener, OnItemClickListener {

    GBIFOccurrence occurrenceItem;

    private ArrayList<GBIFSpeciesDescription> items;
    private SpeciesDescriptionAdapter mAdapter;

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
        occurrenceItem = new Gson().fromJson(itemString, GBIFOccurrence.class);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        final RecyclerView descriptionList = (RecyclerView) findViewById(R.id.list_descriptions);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        items = new ArrayList<>();
        mAdapter = new SpeciesDescriptionAdapter(items, occurrenceItem, this, this);
        descriptionList.setAdapter(mAdapter);
        descriptionList.setLayoutManager(layoutManager);

        if (getActionBar() != null) {
            getActionBar().setTitle(occurrenceItem.getSpecies());
            getActionBar().setSubtitle("Species overview");
        }

        //lookup species with speciesKey
        String request = Values.GBIFBaseAddr + "species/" + occurrenceItem.getSpeciesKey() + "/descriptions";
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                request, null,
                this, this);

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, "species_description_lookup");
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
        int id = item.getItemId();

        if (id ==  android.R.id.home) {
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        GBIFSpeciesLookupResponse response = new Gson().fromJson(jsonObject.toString(), GBIFSpeciesLookupResponse.class);
        ArrayList<GBIFSpeciesDescription> fetchedDescriptions = response.getResults();

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

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(this, "Something went wrong :| ", Toast.LENGTH_SHORT).show();
        loadEmptyView();
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
    private void loadEmptyView() {
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.view_woops, null);
        LinearLayout llDetails = (LinearLayout) findViewById(R.id.ll_occurrence_details);
        llDetails.removeAllViews();
        llDetails.addView(v);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("item", new Gson().toJson(occurrenceItem));
    }
}