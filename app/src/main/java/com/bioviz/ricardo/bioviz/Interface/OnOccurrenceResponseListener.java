package com.bioviz.ricardo.bioviz.Interface;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Created by ricardo on 18-02-2015.
 */
public interface OnOccurrenceResponseListener extends Response.Listener<JSONObject> {

    @Override
    void onResponse(JSONObject jsonObject);
}
