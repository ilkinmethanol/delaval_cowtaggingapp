package com.pozyx.nfctool;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pozyx.nfctool.Util.FarmManagement;
import com.pozyx.nfctool.Util.ProfileModel;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PinEnter extends AppCompatActivity {
    EditText pin;
    Button submit_pin;
    String correct_pin = "1803";
    TextView invalid_password_text;
    List<ProfileModel> pr_list = new ArrayList<>();
    List<FarmManagement> fr_list = new ArrayList<>();
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_enter);

        getHttpResponse();
        get_farmsHttpResponse();


        final Gson gson1 = new Gson();


        pin = (EditText) findViewById(R.id.pinenter);
        submit_pin = (Button) findViewById(R.id.submit_pin_button);
        invalid_password_text = (TextView) findViewById(R.id.invalidtext);
        submit_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pintext = pin.getText().toString();
                boolean isFilePresent = isFilePresent("profile_farm_setting.json");
                if(pintext.equals(correct_pin)){
                    if (isFilePresent){

                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = pref.edit();

//                        "prf_config+" @ "+profileid+" @ "+farmid+" @ "+selectedItem_profiles";

                        String jsonString = read("profileconfig.json");
                        String stringToSplit = jsonString;
                        String[] tempArray;
                        String delimiter = "@";
                        tempArray = stringToSplit.split(delimiter);
                        String prf_config = tempArray[0];
                        String profileid = tempArray[1];
                        String farmid = tempArray[2];
                        String profilename =tempArray[3];


                        editor.putString("farmid",farmid);
                        editor.putString("profilename", profilename);
                        editor.putString("profileid",profileid);
                        editor.putString("configjson",prf_config);
                        editor.commit();
                        Intent settingsPage = new Intent(PinEnter.this,ScanningPage.class);
                        startActivity(settingsPage);
                    }
                    else {
                        Intent settingsPage = new Intent(PinEnter.this,OptionsPage.class);
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor p = pref.edit();

                        p.putString("frlist_p",gson1.toJson(fr_list));
                        p.putString("prlist_p",gson1.toJson(pr_list));


                        p.commit();

//                    settingsPage.putExtra("profilesList_apiresp",gson1.toJson(pr_list));

                        startActivity(settingsPage);
//                    Toast.makeText(getApplicationContext(), gson1.toJson(fr_list.get(0)),Toast.LENGTH_LONG);
                    }
                }
                else {
                    invalid_password_text.setVisibility(View.VISIBLE);
                }
            }
        });
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

    public boolean isFilePresent(String fileName) {
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileName;
        File file = new File(path);
        return file.exists();
    }

    public List getHttpResponse() {

        String url = "https://zbn8x5og64.execute-api.eu-west-1.amazonaws.com/stage/profiles";

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                Toast.makeText(getApplicationContext(),"problem calling api",Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String profilesresp = response.body().string();
                PinEnter.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("Response", profilesresp);
                        if (response.code() == 200 || response.code() == 202 || response.code() == 201) {
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

                                    ProfileModel.Config Profileconf = new ProfileModel.Config(
                                            confobject.optString("samples_interval","0"),
                                            confobject.optString("agg_alg","0"),
                                            confobject.optString("minimum_activeblinks","0"),
                                            confobject.optString("minimumlevel_activeblinks","0"),
                                            confobject.optString("threshold","0"),
                                            confobject.optString("minimum_trigger_count","0")
                                    );
                                    profconf.add(Profileconf);

                                    profileModel.setProfileconfig(profconf);

                                    pr_list.add(profileModel);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(getApplicationContext(),"Error on fetching",Toast.LENGTH_LONG);
                            }
                        }
                    }
                });
            }
        });
        return pr_list;
    }

    public List get_farmsHttpResponse() {

        String url = "https://iam.stage.farmregistry.delaval.cloud/customers/9999999999/farms";

        OkHttpClient client = new OkHttpClient();

        final Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                Toast.makeText(getApplicationContext(),"problem calling api",Toast.LENGTH_LONG);
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String profilesresp = response.body().string();
                PinEnter.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (response.code() == 200) {
                            Log.e("Response", profilesresp);
                            try {
                                JSONArray farmsArr = new JSONArray(profilesresp);

                                for (int i = 0; i < farmsArr.length(); i++) {
                                    JSONObject farm = farmsArr.getJSONObject(i);

                                    FarmManagement farmModel = new FarmManagement(farm.getString("name"), farm.getString("farm_id"));
                                    fr_list.add(farmModel);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
//                                Toast.makeText(getApplicationContext(), "Error on fetching", Toast.LENGTH_LONG);
                            }
                        }
                    }
                });
            }
        });
        return fr_list;
    }


}