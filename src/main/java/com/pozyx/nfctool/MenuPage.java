package com.pozyx.nfctool;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.pozyx.nfctool.Util.AnimalAssociate;
import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.NfcWrapper;
import com.pozyx.nfctool.Util.PostTask;
import com.pozyx.nfctool.Util.ProfileConfig;
import com.pozyx.nfctool.Util.TagSettings;
import com.pozyx.nfctool.Util.ProfileModel;
import com.squareup.okhttp.FormEncodingBuilder;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MenuPage extends AppCompatActivity {
    private Integer responseCode;
    private String endpointValue;
    private String cowidValue;
    private String tagidValue;
    private String farmidValue;
    private String farmNameValue;
    Button viewInfo;
    Button associateCowBtn;
    Button unassociateCowBtn;
    Toast current_toast;
    private String UID;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent ;
    private NfcV nfcvTag;
    byte[] settingsBytes;
    private HashMap<String, TextView> settings_views;
    TagSettings settings;
    TextView tv;
    NfcWrapper nfc = new NfcWrapper();

    void showToast(String msg){
        if (current_toast != null){
            current_toast.cancel();
        }
        current_toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        current_toast.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_page);

        viewInfo = (Button) findViewById(R.id.view_info_btn);
        associateCowBtn = (Button) findViewById(R.id.associate_cow_btn);
        unassociateCowBtn = (Button) findViewById(R.id.unassociate_cow_btn);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

//        settingsBytes = (byte[])getIntent().getSerializableExtra("TagSettingsBytes_int");
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        settingsBytes = Base64.decode(prefs.getString("TagSettingsBytes_pref","0"), Base64.DEFAULT);

        tv = (TextView)findViewById(R.id.unassociate_text);

        tv.setVisibility(View.INVISIBLE);

//        //make nfcV object and read some data
//        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
//        nfcvTag = NfcV.get(tag);
//        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
//        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
//
//        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        //get data passed from NFCRequirementsPage
//
//        byte[] settingsBytes = (byte[])getIntent().getSerializableExtra("TagSettingsBytes");
//        if (settingsBytes == null){
//            settingsBytes = nfc.readSettingsBlocks(nfcvTag);
//        }
//
//        settings = new TagSettings();
//        if (settingsBytes.length != 0) {
//            settings.deserialize(settingsBytes);
//        }
//
        viewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);

//                settingsBytes = Base64.decode(prefs.getString("TagSettingsBytes_pref","0"), Base64.DEFAULT);
                Intent settingsPage = new Intent(MenuPage.this,SettingsPage.class);
                settingsPage.putExtra("TagSettingsBytes", settingsBytes);
                settingsPage.putExtra(NfcAdapter.EXTRA_TAG, tag);
                startActivity(settingsPage);
                finish();
            }
        });

        associateCowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    settingsBytes = Base64.decode(prefs.getString("TagSettingsBytes","0"), Base64.DEFAULT);

                    Intent intent = new Intent(MenuPage.this, InsertCowIdPage.class);
                    intent.putExtra("TagSettingsBytes_pref",settingsBytes);
                    startActivity(intent);
                    finish();
                } catch (Exception e){

                    showToast("Unexpected error occurred!");
                }
            }
        });

        unassociateCowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                final boolean isConnected = isNetworkConnected();


                final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                settingsBytes = Base64.decode(prefs.getString("TagSettingsBytes","0"), Base64.DEFAULT);

                tagidValue = prefs.getString("Id","Nulldata");
                farmidValue = prefs.getString("farmid","Null");
//                Toast.makeText(getApplicationContext(),"Unassociated from tag"+ Arrays.toString(settingsBytes),Toast.LENGTH_LONG).show();
                AnimalAssociate animalAssociate = new AnimalAssociate(tagidValue,farmidValue);
                Gson gs = new Gson();
                String jsonObject = gs.toJson(animalAssociate);

                okhttp3.OkHttpClient client = new OkHttpClient();
                okhttp3.MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // put your json here
                okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject);
                final okhttp3.Request request = new Request.Builder()
                        .url("https://zbn8x5og64.execute-api.eu-west-1.amazonaws.com/stage/pozyxtag")
                        .patch(body)
                        .build();

                if (isConnected){

                    client.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(okhttp3.Call call, IOException e) {
                            showToast("unassociate failure ");
                        }

                        @Override
                        public void onResponse(Call call, final Response response) throws IOException {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isConnected){
                                        if (response.code() == 200 || response.code() == 201 || response.code() == 204){
                                            tv.setVisibility(View.VISIBLE);
                                            tv.setText("unassociating");
                                            Handler h = new Handler();
                                            h.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tv.setText("Unassociated");
                                                }
                                            },2000);
                                        }
                                        else {
                                            tv.setVisibility(View.VISIBLE);
                                            tv.setText("not unassociating");
                                        }
                                    }

                                }
                            });
                        }
                    });
                }
                else{
                    tv.setVisibility(View.VISIBLE);
                    tv.setText("Please check internet connection");
                }

            }
        });

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    FarmModel findFarm(String codeIsIn) {
        for(FarmModel farm : FarmsHelper.FarmHelper.farms) {
            if(farm.shortName.equals(codeIsIn)) {
                return farm;
            }
        }
        return null;
    }

}