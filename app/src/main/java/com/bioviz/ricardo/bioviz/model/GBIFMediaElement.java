package com.bioviz.ricardo.bioviz.model;

/**
 * Media element holding the associated resource (image, sound, or moving image)
 * format -> mimeType
 * identifyier -> direct url for the image
 */
public class GBIFMediaElement {

    private String format;
    private String identifier;
    private String refecrences;

    public String getReferences() {return refecrences; }

    public String getFormat() {
        return format;
    }

    public String getIdentifier() {
        return identifier;
    }
}
