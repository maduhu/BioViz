package com.bioviz.ricardo.bioviz.model.iNatResponses;

/**
 * Created by ricardo on 18-02-2015.
 */
public class iNatMediaElement {
    private String large_url;
    private String small_url;
    private String native_realname;
    private String medium_url;
    private String thumb_url;

    public String getLarge_url() {
        return large_url;
    }

    public void setLarge_url(String large_url) {
        this.large_url = large_url;
    }

    public String getNative_realname() {
        return native_realname;
    }

    public void setNative_realname(String native_realname) {
        this.native_realname = native_realname;
    }

    public String getThumb_url() {
        return thumb_url;
    }

    public void setThumb_url(String thumb_url) {
        this.thumb_url = thumb_url;
    }

    public String getSmall_url() {
        return small_url;
    }

    public void setSmall_url(String small_url) {
        this.small_url = small_url;
    }

    public String getMedium_url() {
        return medium_url;
    }

    public void setMedium_url(String medium_url) {
        this.medium_url = medium_url;
    }
}
