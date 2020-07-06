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

    @SerializedName("activated")
    @Expose
    private String memactivated;

    @SerializedName("eta")
    @Expose
    private String memeta;

    @SerializedName("pgdly")
    @Expose
    private String mempgdly;

    @SerializedName("power")
    @Expose
    private String mempower;

    @SerializedName("blinkindex")
    @Expose
    private String memblinkindex;

    @SerializedName("changed")
    @Expose
    private String memchanged;

    @SerializedName("firmware")
    @Expose
    private String memfirmware;

    @SerializedName("hardware")
    @Expose
    private String memhardware;

    @SerializedName("minimum_trigger_count")
    @Expose
    private String memminimumtriggercount;

    @SerializedName("threshold")
    @Expose
    private String memthreshold;

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

    public ProfileConfig(String samplesInterval, String aggAlg, String minimumActiveblinks, String minimumlevelActiveblinks,
                         String  memthreshold, String memminimumtriggercount) {
        this.samplesInterval = samplesInterval;
        this.aggAlg = aggAlg;
        this.minimumActiveblinks = minimumActiveblinks;
        this.minimumlevelActiveblinks = minimumlevelActiveblinks;
        this.memthreshold = memthreshold;
        this.memminimumtriggercount = memminimumtriggercount;
    }



    public String getMinimumlevelActiveblinks() {
        return minimumlevelActiveblinks;
    }

    public void setMinimumlevelActiveblinks(String minimumlevelActiveblinks) {
        this.minimumlevelActiveblinks = minimumlevelActiveblinks;
    }

}
