package com.pozyx.nfctool.Util;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class ProfileConfig {

    @SerializedName("samples_interval")
    @Expose
    private String samplesInterval;

    @SerializedName("agg_alg")
    @Expose
    private String aggAlg;

    @SerializedName("minimum_activeblinks")
    @Expose
    private String minimumActiveblinks;

    @SerializedName("minimumlevel_activeblinks")
    @Expose
    private String minimumlevelActiveblinks;

//
//    @SerializedName("minimumlevel_activeblinks")
//    @Expose
//    private String threshold;
//
//
//    @SerializedName("minimumlevel_activeblinks")
//    @Expose
//    private String minimumtriggercount;

//    public String getThreshold() {
//        return threshold;
//    }
//
//    public void setThreshold(String threshold) {
//        this.threshold = threshold;
//    }
//
//    public String getMinimumtriggercount() {
//        return minimumtriggercount;
//    }
//
//    public void setMinimumtriggercount(String minimumtriggercount) {
//        this.minimumtriggercount = minimumtriggercount;
//    }

    public ProfileConfig() {

    }

    public String getSamplesInterval() {
        return samplesInterval;
    }

    public void setSamplesInterval(String samplesInterval) {
        this.samplesInterval = samplesInterval;
    }

    public String getAggAlg() {
        return aggAlg;
    }

    public void setAggAlg(String aggAlg) {
        this.aggAlg = aggAlg;
    }

    public String getMinimumActiveblinks() {
        return minimumActiveblinks;
    }

    public void setMinimumActiveblinks(String minimumActiveblinks) {
        this.minimumActiveblinks = minimumActiveblinks;
    }

    public ProfileConfig(String samplesInterval, String aggAlg, String minimumActiveblinks, String minimumlevelActiveblinks) {
//                         String minimumtriggercount, String threshold
        this.samplesInterval = samplesInterval;
        this.aggAlg = aggAlg;
        this.minimumActiveblinks = minimumActiveblinks;
        this.minimumlevelActiveblinks = minimumlevelActiveblinks;
//        this.threshold = threshold;
//        this.minimumtriggercount = minimumtriggercount;
    }


    public String getMinimumlevelActiveblinks() {
        return minimumlevelActiveblinks;
    }

    public void setMinimumlevelActiveblinks(String minimumlevelActiveblinks) {
        this.minimumlevelActiveblinks = minimumlevelActiveblinks;
    }

}
