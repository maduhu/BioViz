package com.bioviz.ricardo.bioviz.model.iNatResponses;


import java.util.ArrayList;


public class iNatObservation {

    private String uri;
    private String species_guess;
    private String place_guess;
    private ArrayList<iNatMediaElement> photos;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getSpecies_guess() {
        return species_guess;
    }

    public void setSpecies_guess(String species_guess) {
        this.species_guess = species_guess;
    }

    public String getPlace_guess() {
        return place_guess;
    }

    public void setPlace_guess(String place_guess) {
        this.place_guess = place_guess;
    }

    public ArrayList<iNatMediaElement> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<iNatMediaElement> photos) {
        this.photos = photos;
    }
}
