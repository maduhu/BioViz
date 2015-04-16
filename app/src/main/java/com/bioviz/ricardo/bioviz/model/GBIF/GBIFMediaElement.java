package com.bioviz.ricardo.bioviz.model.GBIF;

/**
 * Media element holding the associated resource (image, sound, or moving image)
 * format -> mimeType
 * identifyier -> direct url for the image
 */
public class GBIFMediaElement {

    private String format;
    private String identifier;
    private String references;
    private String title;
    private String creator;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReferences() {return references; }

    public String getFormat() {
        return format;
    }

    public String getIdentifier() {
        return identifier;
    }

}
