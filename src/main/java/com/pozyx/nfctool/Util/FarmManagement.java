package com.pozyx.nfctool.Util;

public class FarmManagement {
    private String farmname;
    private String farmid;

    public FarmManagement(String farmname, String farmid) {
        this.farmname = farmname;
        this.farmid = farmid;
    }

    public String getFarmname() {
        return farmname;
    }

    public void setFarmname(String farmname) {
        this.farmname = farmname;
    }

    public String getFarmid() {
        return farmid;
    }

    public void setFarmid(String farmid) {
        this.farmid = farmid;
    }


    @Override
    public String toString() {
        return farmname;
    }
}
