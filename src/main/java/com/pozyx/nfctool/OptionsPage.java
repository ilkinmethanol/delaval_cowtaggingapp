package com.pozyx.nfctool;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.ProfileModel;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OptionsPage extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, Serializable {
    private Boolean activateSwitchValue;
    private Boolean activateSwitchValueTemp;

    private Button button;
    private Button button2;

    private Switch activateSwitchButton;
    SearchableSpinner spinner_profiles;
    List<ProfileModel> pr_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_page);
        getSupportActionBar().setTitle("Settings");

        String farmsString = FarmsHelper.FarmHelper.farmsString;

        List<FarmModel> farms = FarmsHelper.FarmHelper.farms;

        View v2 = findViewById(R.id.activity_options_page_id);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        SearchableSpinner searchableSpinner = (SearchableSpinner)findViewById(R.id.dropdown);
        ArrayList<String> mStrings = new ArrayList<String>();

        for (FarmModel farm : farms)
        {
            mStrings.add(farm.shortName.replace(".", " - "));
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(OptionsPage.this,android.R.layout.simple_spinner_dropdown_item,mStrings);
        searchableSpinner.setAdapter(arrayAdapter);
        searchableSpinner.setTitle("Select Farm");
        searchableSpinner.setPositiveButton("OK");
        String farmName = pref.getString("farmName", "");
        searchableSpinner.setSelection(getIndex(searchableSpinner, farmName));

/*        farmid = (EditText)v2.findViewById(R.id.farmIdInput);
        String name = pref.getString("farmid", "");
        farmidText = name;*/


        spinner_profiles = (SearchableSpinner)findViewById(R.id.dropdown_profiles);
        getHttpResponse();

        ArrayAdapter adapter = new ArrayAdapter(OptionsPage.this, android.R.layout.simple_list_item_1 , pr_list);

        spinner_profiles.setAdapter(adapter);

        if (!spinner_profiles.isSelected()){
            spinner_profiles.setSelection(0);
        }
        else{
            spinner_profiles.setSelection(getIndex(spinner_profiles,spinner_profiles.getSelectedItem().toString()));
        }
        spinner_profiles.setTitle("Select Profile");
        spinner_profiles.setPositiveButton("OK");
        String profileNamee = pref.getString("profileName","");
        spinner_profiles.setSelection(getIndex(spinner_profiles, profileNamee));

        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getApplicationContext(),"Selectedf",Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        usersList = new ArrayList<String>();
//        catAdapter = new ArrayAdapter<String>(this,simple_list_item_1,usersList);
//        spinner.setAdapter(catAdapter);


        Switch activateSwitch = (Switch)v2.findViewById(R.id.activateSwitch);
        Boolean value2 = pref.getBoolean("isActivate", true);
        activateSwitchValue = value2;
        activateSwitchValueTemp = value2;


        activateSwitchButton = (Switch)v2.findViewById(R.id.activateSwitch);
        activateSwitchButton.setChecked(activateSwitchValue);

        button = (Button) v2.findViewById(R.id.saveButton);
        button2 = (Button) v2.findViewById(R.id.cancelButton);

        activateSwitchButton.setOnCheckedChangeListener(this);


    }

    public void getSelectedProfile(View v){
        ProfileModel pm = (ProfileModel) spinner_profiles.getSelectedItem();
        displayUserData(pm);
    }

    private void displayUserData(ProfileModel profile){
        String profilename = profile.getProfilename();
        String profileid = profile.getProfileid();
        List<ProfileModel.Config> pconflist = profile.getProfileconfig();

        String data = profilename+"  "+profileid;
        Toast.makeText(getApplicationContext(),data,Toast.LENGTH_LONG);
    }

    public List getHttpResponse() {

        String url = "https://4fm1sus9w2.execute-api.eu-west-1.amazonaws.com/dev/profiles";

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Toast.makeText(getApplicationContext(),"problem calling api",Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String profilesresp = response.body().string();
                OptionsPage.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Response", profilesresp);
                        try {

                            JSONObject users = new JSONObject(profilesresp);
                            JSONArray usersArr = users.getJSONArray("profiles");

                            for(int i =0; i < usersArr.length(); i++){

                                JSONObject user = usersArr.getJSONObject(i);

                                ProfileModel profileModel = new ProfileModel();
                                profileModel.setProfilename(user.getString("profilename"));
                                profileModel.setProfileid(user.getString("profileid"));

                                List<ProfileModel.Config> profconf = new ArrayList<>();
                                JSONObject confobject = user.getJSONObject("profileconfig");

                                ProfileModel.Config Profileconf = new ProfileModel.Config();

                                Profileconf.setSamples_interval(confobject.optString("samples_interval","0"));
                                Profileconf.setAgg_alg(confobject.optString("agg_alg","0"));
                                Profileconf.setMinimum_activeblinks(confobject.optString("minimum_activeblinks","0"));
                                Profileconf.setMinimumlevel_activeblinks(confobject.optString("minimumlevel_activeblinks","0"));
                                profconf.add(Profileconf);

                                profileModel.setProfileconfig(profconf);

                                pr_list.add(profileModel);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"Error on fetching",Toast.LENGTH_LONG);
                        }
                    }
                });
            }
        });
        return pr_list;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
                return i;
            }
        }
        return 0;
    }

    public void finishAfterCancelButton(View view)
    {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (activateSwitchValueTemp != pref.getBoolean("isActivate", true))
        {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("isActivate", activateSwitchValueTemp);
            editor.commit();
        }
        finish();
    }

    public void finishAfterSaveButton(View view)
    {
        View optionsView = findViewById(R.id.activity_options_page_id);
        /*EditText farmid = (EditText)optionsView.findViewById(R.id.farmIdInput);*/
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();


        SearchableSpinner spinner = (SearchableSpinner)optionsView.findViewById(R.id.dropdown);
        String selectedItem = spinner.getSelectedItem().toString();
        String selectedItem_profiles = spinner_profiles.getSelectedItem().toString();
        String profileid = get_profile_id(selectedItem_profiles);
        String prf_config = get_profile_congif(selectedItem_profiles);
        editor.putString("farmName", selectedItem);
        editor.putString("profilename", selectedItem_profiles);
        editor.putString("profileid",profileid);
        editor.putString("configjson",prf_config);
        editor.commit();

        Toast.makeText(this, "Settings have been saved!"+prf_config, Toast.LENGTH_SHORT).show();
        Intent scan_tagpage = new Intent(OptionsPage.this,ScanningPage.class);
        finish();
        startActivity(scan_tagpage);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        switch (buttonView.getId()){
            case R.id.switch2:
                editor.putBoolean("isCamera", isChecked);
                editor.commit();
                break;

            case R.id.activateSwitch:
                editor.putBoolean("isActivate", isChecked);
                editor.commit();
                break;
        }
    }

    public String  get_profile_id(String name){
        String profileid = "";
        for (int i = 0; i<pr_list.size(); i++){
            if (pr_list.get(i).profilename == name){
                profileid = pr_list.get(i).profileid;
            }
        }
        return profileid;
    }

    public String get_profile_congif(String profilename){
        ProfileModel pm = new ProfileModel();
        for (int i = 0; i<pr_list.size(); i++){
            if (pr_list.get(i).profilename == profilename){
                pm = pr_list.get(i);
            }
        }
        Gson gson = new Gson();
        gson.toJson(pm);

        return gson.toJson(pm);
    }

    public void onResume() {
        super.onResume();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
    }

    public void onPause() {
        super.onPause();
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        nfcAdapter.disableForegroundDispatch(this);
    }

    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // drop NFC events
        }
    }
}
