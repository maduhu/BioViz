package com.bioviz.ricardo.bioviz.biointerface;

import com.android.volley.Response;

import org.json.JSONArray;

public interface OnObservationResponseListener extends Response.Listener<JSONArray> {

    @Override
    void onResponse(JSONArray jsonArray);
}
