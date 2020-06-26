package com.pozyx.nfctool.Util;

import java.util.List;

public class FarmModel {
    public String vcId;
    public String shortName;
    public fullName name;

    public String getVcId() {
        return vcId;
    }

    public void setVcId(String vcId) {
        this.vcId = vcId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public fullName getName() {
        return name;
    }

    public void setName(fullName name) {
        this.name = name;
    }
}

class fullName {
    public List<String> strings;
}
