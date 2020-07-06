package com.pozyx.nfctool;

import android.app.PendingIntent;
import android.content.Context;
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
import com.google.gson.reflect.TypeToken;
import com.pozyx.nfctool.Util.FarmManagement;
import com.pozyx.nfctool.Util.ProfileConfig;
import com.pozyx.nfctool.Util.ProfileModel;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class OptionsPage extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, Serializable {
    private Boolean activateSwitchValue;
    private Boolean activateSwitchValueTemp;

    private Button button;
    private Button button2;

    private Switch activateSwitchButton;
    SearchableSpinner spinner_profiles;
    SearchableSpinner spinner_farms;
    List<ProfileModel> pr_list = new ArrayList<>();
    List<FarmManagement> fr_list = new ArrayList<>();
    Integer farmindex;
    Integer profileindex;
//    List<FarmModel> farms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options_page);
        getSupportActionBar().setTitle("Settings");

        View v2 = findViewById(R.id.activity_options_page_id);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson1 = new Gson();
        Type type = new TypeToken<ArrayList<FarmManagement>>() {}.getType();
        Type typo = new TypeToken<ArrayList<ProfileModel>>() {}.getType();
        String profiles_apiresp_str = pref.getString("prlist_p","0");
        String farmsList_apiresp_str = pref.getString("frlist_p","0");

        spinner_farms = (SearchableSpinner)findViewById(R.id.dropdown);
        spinner_profiles = (SearchableSpinner)findViewById(R.id.dropdown_profiles);

        fr_list = gson1.fromJson(farmsList_apiresp_str,type);
//        fr_list = get_farmsHttpResponse();
        pr_list = gson1.fromJson(profiles_apiresp_str,typo);

        farmindex = 0;
        profileindex = 0;

        ArrayAdapter adapter_profiles = new ArrayAdapter(OptionsPage.this, android.R.layout.simple_list_item_1 , pr_list);
        ArrayAdapter adapter_farms = new ArrayAdapter(OptionsPage.this,android.R.layout.simple_list_item_1,fr_list);

        boolean isFilePresent = isFilePresent("profile_farm_setting.json");
        if(isFilePresent) {
            String jsonString = read("profile_farm_setting.json");
            String stringToSplit = jsonString;
            String[] tempArray;
            String delimiter = ",";
            tempArray = stringToSplit.split(delimiter);
            farmindex = Integer.parseInt(tempArray[0]);
            profileindex = Integer.parseInt(tempArray[1]);
        }
        else{
            farmindex = 0;
            profileindex = 0;
        }

        spinner_farms.setTitle("Select Farm");
        spinner_farms.setAdapter(adapter_farms);
        spinner_farms.setPositiveButton("OK");
        spinner_farms.setSelection(farmindex);


//        spinner_farms.setSelection(getIndex(spinner_farms, farm));
        spinner_profiles.setAdapter(adapter_profiles);
        spinner_profiles.setTitle("Select Profile");
        spinner_profiles.setPositiveButton("OK");
        spinner_profiles.setSelection(profileindex);

//        if (farm_profile_indexes.size()>0){
//        }

//        if (!spinner_farms.isSelected()){
//            spinner_farms.setSelection(0);
//        }
//        else{
//            spinner_profiles.setSelection(getIndex(spinner_farms,spinner_farms.getSelectedItem().toString()));
//        }

//        if (!spinner_profiles.isSelected()){
//            spinner_profiles.setSelection(0);
//        }
//        else{
//            spinner_profiles.setSelection(getIndex(spinner_profiles,spinner_profiles.getSelectedItem().toString()));
//        }

//        String profileNamee = pref.getString("profilename","0");
//        spinner_profiles.setSelection(getIndex(spinner_profiles, profileNamee));

        spinner_profiles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinner_farms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getApplicationContext(),"Selected",Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

//    public List get_farmsHttpResponse() {
//
//        String url = "https://iam.dev.farmregistry.delaval.cloud/customers/9999999999/farms";
//
//        OkHttpClient client = new OkHttpClient();
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .header("Accept", "application/json")
//                .header("Content-Type", "application/json")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Toast.makeText(getApplicationContext(),"problem calling api",Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                final String profilesresp = response.body().string();
//                OptionsPage.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("Response", profilesresp);
//                        try {
//                            JSONArray farmsArr = new JSONArray(profilesresp);
//
//                            for(int i =0; i < farmsArr.length(); i++){
//                                JSONObject farm = farmsArr.getJSONObject(i);
//
//                                FarmManagement farmModel = new FarmManagement(farm.getString("name"),farm.getString("farm_id"));
//                                fr_list.add(farmModel);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"Error on fetching",Toast.LENGTH_LONG);
//                        }
//                    }
//                });
//            }
//        });
//        return fr_list;
//    }

//    public List getHttpResponse() {
//
//        String url = "https://4fm1sus9w2.execute-api.eu-west-1.amazonaws.com/dev/profiles";
//
//        OkHttpClient client = new OkHttpClient();
//
//        final Request request = new Request.Builder()
//                .url(url)
//                .header("Accept", "application/json")
//                .header("Content-Type", "application/json")
//                .build();
//
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                Toast.makeText(getApplicationContext(),"problem calling api",Toast.LENGTH_LONG);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                final String profilesresp = response.body().string();
//                OptionsPage.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.e("Response", profilesresp);
//                        try {
//
//                            JSONObject users = new JSONObject(profilesresp);
//                            JSONArray usersArr = users.getJSONArray("profiles");
//
//                            for(int i =0; i < usersArr.length(); i++){
//
//                                JSONObject user = usersArr.getJSONObject(i);
//
//                                ProfileModel profileModel = new ProfileModel();
//                                profileModel.setProfilename(user.getString("profilename"));
//                                profileModel.setProfileid(user.getString("profileid"));
//
//                                List<ProfileModel.Config> profconf = new ArrayList<>();
//                                JSONObject confobject = user.getJSONObject("profileconfig");
//
//                                ProfileModel.Config Profileconf = new ProfileModel.Config();
//
//                                Profileconf.setSamples_interval(confobject.optString("samples_interval","0"));
//                                Profileconf.setAgg_alg(confobject.optString("agg_alg","0"));
//                                Profileconf.setMinimum_activeblinks(confobject.optString("minimum_activeblinks","0"));
//                                Profileconf.setMinimumlevel_activeblinks(confobject.optString("minimumlevel_activeblinks","0"));
////                                Profileconf.setMinimum_trigger_count(confobject.optString("minimum_trigger_count","0"));
////                                Profileconf.setThreshold(confobject.optString("threshold","0"));
//                                profconf.add(Profileconf);
//
//                                profileModel.setProfileconfig(profconf);
//
//                                pr_list.add(profileModel);
//                            }
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"Error on fetching",Toast.LENGTH_LONG);
//                        }
//                    }
//                });
//            }
//        });
//        return pr_list;
//    }



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

    private String read(String fileName) {
        try {
            FileInputStream fis = getApplicationContext().openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (FileNotFoundException fileNotFound) {
            return null;
        } catch (IOException ioException) {
            return null;
        }
    }

    public boolean saveFarmProfileindex(String jsonString){
        String FILENAME = "profile_farm_setting.json";
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(FILENAME,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public boolean saveProfileConfig(String jsonString,String FILENAME){
        try {
            FileOutputStream fos = getApplicationContext().openFileOutput(FILENAME,Context.MODE_PRIVATE);
            if (jsonString != null) {
                fos.write(jsonString.getBytes());
            }
            fos.close();
            return true;
        } catch (FileNotFoundException fileNotFound) {
            return false;
        } catch (IOException ioException) {
            return false;
        }
    }

    public boolean isFilePresent(String fileName) {
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public void finishAfterSaveButton(View view)
    {
        View optionsView = findViewById(R.id.activity_options_page_id);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = pref.edit();

        String selectedItem = spinner_farms.getSelectedItem().toString();
        String selectedItem_profiles = spinner_profiles.getSelectedItem().toString();

        String profileid = get_profile_id(selectedItem_profiles);
        String farmid = get_farm_id(selectedItem);

        String prf_config = get_profile_congif(selectedItem_profiles);
        editor.putString("farmid",farmid);
        editor.putString("profilename", selectedItem_profiles);
        editor.putString("profileid",profileid);
        editor.putString("configjson",prf_config);
        editor.commit();

        String config_text = spinner_farms.getSelectedItemPosition()+","+spinner_profiles.getSelectedItemPosition()+","+"1";

        boolean isFilePresent = isFilePresent("profile_farm_setting.json");
        if(isFilePresent) {
            String jsonString = read("profile_farm_setting.json");
            saveFarmProfileindex(config_text);
            String filename = "profileconfig.json";
            saveProfileConfig(prf_config+" @ "+profileid+" @ "+farmid+" @ "+selectedItem_profiles,filename);
//            Toast.makeText(this, "Read"+jsonString+":"+config_text+"@ "+prf_config, Toast.LENGTH_SHORT).show();
        } else {
            boolean isFileCreated = saveFarmProfileindex(config_text);
            if(isFileCreated) {
                String filename = "profileconfig.json";
                saveProfileConfig(prf_config+" @ "+profileid+" @ "+farmid+" @ "+selectedItem_profiles,filename);

            } else {
//                Toast.makeText(this, "Settings have not been saved!", Toast.LENGTH_SHORT).show();
            }
        }

        Toast.makeText(this, "Settings have been saved!"+prf_config, Toast.LENGTH_SHORT).show();

        Intent scan_tagpage = new Intent(OptionsPage.this,ScanningPage.class);
        startActivity(scan_tagpage);
        finish();
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

    public String get_farm_id(String name){
        String farmid= "none";
        for (int i = 0; i<fr_list.size(); i++){
            if (fr_list.get(i).getFarmname() == name){
                farmid = fr_list.get(i).getFarmid();
            }
        }
        return farmid;
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
