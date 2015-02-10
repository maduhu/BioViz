package com.bioviz.ricardo.bioviz.model.GBIFResponses;

import com.bioviz.ricardo.bioviz.model.GBIFSpeciesDescription;

import java.util.ArrayList;

/**
 * Created by ricardo on 10-02-2015.
 */
public class GBIFSpeciesLookupResponse {
    private int offset;
    private ArrayList<GBIFSpeciesDescription> results;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public ArrayList<GBIFSpeciesDescription> getResults() {
        return results;
    }

    public void setResults(ArrayList<GBIFSpeciesDescription> results) {
        this.results = results;
    }
}
