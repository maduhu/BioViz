package com.bioviz.ricardo.bioviz.model.GBIFResponses;

import java.util.ArrayList;

public class OccurrenceLookupResponse {

    private Number count;
    private boolean endOfRecords;
    private Number limit;
    private Number offset;
    private ArrayList<GBIFOccurrence> results;

    public Number getCount(){
        return this.count;
    }
    public void setCount(Number count){
        this.count = count;
    }
    public boolean getEndOfRecords(){
        return this.endOfRecords;
    }
    public void setEndOfRecords(boolean endOfRecords){
        this.endOfRecords = endOfRecords;
    }
    public Number getLimit(){
        return this.limit;
    }
    public void setLimit(Number limit){
        this.limit = limit;
    }
    public Number getOffset(){
        return this.offset;
    }
    public void setOffset(Number offset){
        this.offset = offset;
    }
    public ArrayList<GBIFOccurrence> getResults(){
        return this.results;
    }
    public void setResults(ArrayList results){
        this.results = results;
    }
}
