package com.bioviz.ricardo.bioviz.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bioviz.ricardo.bioviz.AppController;
import com.bioviz.ricardo.bioviz.R;
import com.bioviz.ricardo.bioviz.model.GBIFOccurrence;
import com.bioviz.ricardo.bioviz.model.GBIFResponses.GBIFSpeciesLookupResponse;
import com.bioviz.ricardo.bioviz.utils.Values;
import com.google.gson.Gson;

import org.json.JSONObject;

public class OccurrenceDetails extends Activity implements Response.Listener<JSONObject>, Response.ErrorListener {

    GBIFOccurrence occurrenceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occurrence_details);

        if (getActionBar() != null) {
            getActionBar().setSubtitle("Occurrence details");
        }

        String itemString;
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            itemString = intent.getStringExtra("item");
        } else {
            itemString = savedInstanceState.getString("item");
        }

        occurrenceItem = new Gson().fromJson(itemString, GBIFOccurrence.class);

        //lookup species with speciesKey
        //http://api.gbif.org/v1/species/2482473/descriptions
        String request = Values.GBIFBaseAddr +  "species/" + occurrenceItem.getSpeciesKey() + "/descriptions";
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        GBIFSpeciesLookupResponse response = new Gson().fromJson(jsonObject.toString(), GBIFSpeciesLookupResponse.class);
        Toast.makeText(this, jsonObject.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("VOLLEY", volleyError.toString());
        Toast.makeText(this, "Something went wrong :| ", Toast.LENGTH_SHORT).show();
    }
}
