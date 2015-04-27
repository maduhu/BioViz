package com.bioviz.ricardo.bioviz.model.iNatResponses;

import java.util.List;

/**
 * Created by ricardo on 17-03-2015.
 */
public class iNatSpeciesLookupResponse {
    private  List<iNatSpecies> results;

    public List<iNatSpecies> getResults() {
        return results;
    }

    public void setResults(List<iNatSpecies> results) {
        this.results = results;
    }
}
