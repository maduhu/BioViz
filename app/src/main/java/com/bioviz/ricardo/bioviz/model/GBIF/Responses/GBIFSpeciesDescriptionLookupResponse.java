package com.bioviz.ricardo.bioviz.model.GBIF.Responses;

import com.bioviz.ricardo.bioviz.model.GBIF.GBIFSpeciesDescription;

import java.util.ArrayList;

/**
 * Created by ricardo on 10-02-2015.
 */
public class GBIFSpeciesDescriptionLookupResponse {
    private ArrayList<GBIFSpeciesDescription> results;
    public ArrayList<GBIFSpeciesDescription> getResults() {
        return results;
    }
}
