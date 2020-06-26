package com.pozyx.nfctool;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

    private HashMap<String, TextView> settings_views;
    TagSettings settings;

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
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


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
                byte[] settingsBytes = (byte[])getIntent().getSerializableExtra("TagSettingsBytes");

                Intent settingsPage = new Intent(MenuPage.this,SettingsPage.class);

                settingsPage.putExtra("TagSettingsBytes", settingsBytes);
                settingsPage.putExtra(NfcAdapter.EXTRA_TAG, tag);
                startActivity(settingsPage);
            }
        });

        associateCowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Intent intent = new Intent(MenuPage.this, InsertCowIdPage.class);
                    startActivity(intent);
                } catch (Exception e){

                    showToast("Unexpected error occurred!");
                }
            }
        });

        unassociateCowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                tagidValue = prefs.getString("Id","Nulldata");
                farmidValue = prefs.getString("farmid","Null");
                Toast.makeText(getApplicationContext(),"Unassociated from tag"+farmidValue,Toast.LENGTH_LONG).show();
                AnimalAssociate animalAssociate = new AnimalAssociate(tagidValue,"123465");
                Gson gs = new Gson();
                String jsonObject = gs.toJson(animalAssociate);

                okhttp3.OkHttpClient client = new OkHttpClient();
                okhttp3.MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                // put your json here
                okhttp3.RequestBody body = RequestBody.create(JSON, jsonObject);
                okhttp3.Request request = new Request.Builder()
                        .url("https://4fm1sus9w2.execute-api.eu-west-1.amazonaws.com/dev/pozyxtag")
                        .patch(body)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(okhttp3.Call call, IOException e) {
                        showToast("unassociate failure ");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            showToast("Problem on call unassociate"+response.code());
                        }
                    }
                });
                return;
            }
        });
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