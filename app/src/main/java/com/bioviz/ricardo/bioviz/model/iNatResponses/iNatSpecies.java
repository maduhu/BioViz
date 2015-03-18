package com.bioviz.ricardo.bioviz.model.iNatResponses;

import java.util.List;


public class iNatSpecies {
    public String name;
    public String unique_name;
    public String wikipedia_title;
    public String photo_url;
    public List<TaxonPhotos> taxon_photos;
    public List<TaxonNames> taxon_names;

    private class TaxonNames {
        public String name;
        public String lexicon;
    }

    private class TaxonPhotos {
        public List<Photo> photo;
    }

    private class Photo {
        public String square_url;
        public String small_url;
        public String large_url;
        public String native_username;
        public String attribution;
    }
}
