package com.bioviz.ricardo.bioviz.model;

import java.util.List;

/**
 * Class with the GBIF occurrence details
 * (not all of them are listed)
 */
public class GBIFOccurrence {

    private String basisOfRecord;
    private String scientificName;
    private String kingdom;
    private String phylum;
    private String order;
    private String family;
    private String genus;
    private String species;
    private String country;
    private String locality;
    private String decimalLongitude;
    private String decimalLatitude;
    private String datasetKey;
    private String speciesKey;

    private int year;
    private List<GBIFMediaElement> media;


    public String getDatasetKey() {
        return datasetKey;
    }

    public void setDatasetKey(String datasetKey) {
        this.datasetKey = datasetKey;
    }

    public String getBasisOfRecord() {
        return basisOfRecord;
    }

    public void setBasisOfRecord(String basisOfRecord) {
        this.basisOfRecord = basisOfRecord;
    }

    public String getScientificName() {
        return scientificName;
    }

    public void setScientificName(String scientificName) {
        this.scientificName = scientificName;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getDecimalLongitude() {
        return decimalLongitude;
    }

    public void setDecimalLongitude(String decimalLongitude) {
        this.decimalLongitude = decimalLongitude;
    }

    public String getDecimalLatitude() {
        return decimalLatitude;
    }

    public void setDecimalLatitude(String decimalLatitude) {
        this.decimalLatitude = decimalLatitude;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<GBIFMediaElement> getMedia() {
        return media;
    }

    public void setMedia(List<GBIFMediaElement> media) {
        this.media = media;
    }

    public String getSpeciesKey() {
        return speciesKey;
    }

    public void setSpeciesKey(String speciesKey) {
        this.speciesKey = speciesKey;
    }

}
