package com.pozyx.nfctool.Util;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class FarmsHelper {
    public static class FarmHelper {
        public static String farmsString;
        public static List<FarmModel> farms;
        public static Double latValue = 0.0;
        public static Double lngValue = 0.0;

        public static void GetFarms()
        {
            try {
                farmsString = new GetFarmsTask().execute("https://myfarm.delaval.com/GetAreaChanges.vcx?query=areas&usr=areafetcher@delaval.com&pwd=bAka627").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Gson gson = new Gson();
            FarmModel[] mcArray = gson.fromJson(farmsString, FarmModel[].class);
            farms = Arrays.asList(mcArray);
        }

    }
}
