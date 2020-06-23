package com.pozyx.nfctool;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;

public class ThisApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FarmsHelper.FarmHelper.GetFarms();
        FarmModel farm = FarmsHelper.FarmHelper.farms.get(0);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();
        String value = farm.shortName;
        editor.putString("farmName", farm.shortName.replace(".", " - "));
        editor.commit();
    }
}
