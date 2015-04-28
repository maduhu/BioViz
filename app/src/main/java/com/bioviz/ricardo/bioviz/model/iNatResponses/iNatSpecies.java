package com.bioviz.ricardo.bioviz.model.iNatResponses;

import java.util.List;


public class iNatSpecies {
    private String name;
    private String unique_name;
    private String wikipedia_title;
    private String wikipedia_summary;
    private String photo_url;
    private List<TaxonPhotos> taxon_photos;
    private List<TaxonNames> taxon_names;

    public String getWikipedia_summary() {
        return wikipedia_summary;
    }

    public void setWikipedia_summary(String wikipedia_summary) {
        this.wikipedia_summary = wikipedia_summary;
    }

    public String getWikipedia_title() {
        return wikipedia_title;
    }

    public void setWikipedia_title(String wikipedia_title) {
        this.wikipedia_title = wikipedia_title;
    }

    public String getUnique_name() {
        return unique_name;
    }

    public void setUnique_name(String unique_name) {
        this.unique_name = unique_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public List<TaxonPhotos> getTaxon_photos() {
        return taxon_photos;
    }

    public void setTaxon_photos(List<TaxonPhotos> taxon_photos) {
        this.taxon_photos = taxon_photos;
    }

    public List<TaxonNames> getTaxon_names() {
        return taxon_names;
    }

    public void setTaxon_names(List<TaxonNames> taxon_names) {
        this.taxon_names = taxon_names;
    }

    public class TaxonNames {
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
