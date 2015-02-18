package com.bioviz.ricardo.bioviz.Interface;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;

public interface OnObservationResponseListener extends Response.Listener<JSONArray> {

    @Override
    void onResponse(JSONArray jsonArray);
}
