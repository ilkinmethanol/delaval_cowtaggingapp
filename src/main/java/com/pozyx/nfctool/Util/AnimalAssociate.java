package com.pozyx.nfctool.Util;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class AnimalAssociate {

    @SerializedName("hardwareid")
    @Expose
    private String hardwareid;
    @SerializedName("animalid")
    @Expose
    private String animalid;
    @SerializedName("farmid")
    @Expose
    private String farmid;
    @SerializedName("profiledata")
    @Expose
    private ProfileConfig profiledata;



    public AnimalAssociate(String hardwareid, String animalid, String farmid, ProfileConfig profiledata) {
        this.hardwareid = hardwareid;
        this.animalid = animalid;
        this.farmid = farmid;
        this.profiledata = profiledata;
    }

    public String getHardwareid() {
        return hardwareid;
    }

    public void setHardwareid(String hardwareid) {
        this.hardwareid = hardwareid;
    }

    public String getAnimalid() {
        return animalid;
    }

    public void setAnimalid(String animalid) {
        this.animalid = animalid;
    }

    public String getFarmid() {
        return farmid;
    }

    public void setFarmid(String farmid) {
        this.farmid = farmid;
    }

    public ProfileConfig getProfiledata() {
        return profiledata;
    }

    public void setProfiledata(ProfileConfig profiledata) {
        this.profiledata = profiledata;
    }

}

