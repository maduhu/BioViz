package com.bioviz.ricardo.bioviz.utils;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Values {
    public static final String GBIFBaseAddr = "http://api.gbif.org/v1/";
    public static final String iNATBaseAddr = "https://www.inaturalist.org/";

    public static final String iNatObservation = "observations.json";

    public static final String GBIFOccurrence = "occurrence";
    public static final String GBIFSpecies = "species";

    public static int section_about_id = 3;
    public static int section_home_id = 1;

    public static int view_occurrence = 0;
    public static int view_observation = 1;

    public static final Type ARRAY_INAT_OBSERVATIONS = new TypeToken<ArrayList<com.bioviz.ricardo.bioviz.model.iNatResponses.iNatObservation>>() {
    }.getType();
}
