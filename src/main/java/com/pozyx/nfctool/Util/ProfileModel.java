package com.pozyx.nfctool.Util;

import androidx.annotation.NonNull;

import java.util.List;

public class ProfileModel {
    public String profileid;
    public String profilename;
    public List<Config> profileconfig;

    public String getProfileid() {
        return profileid;
    }

    public void setProfileid(String profileid) {
        this.profileid = profileid;
    }

    public String getProfilename() {
        return profilename;
    }

    public void setProfilename(String profilename) {
        this.profilename = profilename;
    }

    public List<Config> getProfileconfig() {
        return profileconfig;
    }

    public void setProfileconfig(List<Config> profileconfig) {
        this.profileconfig = profileconfig;
    }

    public static class Config {
        public String samples_interval;
        public String agg_alg;
        public String minimum_activeblinks;
        public String minimumlevel_activeblinks;
        public String threshold;
        public String minimum_trigger_count;

        public String getThreshold() {
            return threshold;
        }

        public void setThreshold(String threshold) {
            this.threshold = threshold;
        }

        public String getMinimum_trigger_count() {
            return minimum_trigger_count;
        }

        public void setMinimum_trigger_count(String minimum_trigger_count) {
            this.minimum_trigger_count = minimum_trigger_count;
        }

        public String getSamples_interval() {
            return samples_interval;
        }

        public void setSamples_interval(String samples_interval) {
            this.samples_interval = samples_interval;
        }

        public String getAgg_alg() {
            return agg_alg;
        }

        public void setAgg_alg(String agg_alg) {
            this.agg_alg = agg_alg;
        }

        public String getMinimum_activeblinks() {
            return minimum_activeblinks;
        }

        public void setMinimum_activeblinks(String minimum_activeblinks) {
            this.minimum_activeblinks = minimum_activeblinks;
        }

        public String getMinimumlevel_activeblinks() {
            return minimumlevel_activeblinks;
        }

        public void setMinimumlevel_activeblinks(String minimumlevel_activeblinks) {
            this.minimumlevel_activeblinks = minimumlevel_activeblinks;
        }

    }

    @Override
    public String toString() {
        return profilename;
    }
}
