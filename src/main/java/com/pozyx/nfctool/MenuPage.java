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

import com.google.gson.JsonObject;
import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.NfcWrapper;
import com.pozyx.nfctool.Util.PostTask;
import com.pozyx.nfctool.Util.TagSettings;
import com.pozyx.nfctool.Util.ProfileModel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

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
                Intent settingsPage = new Intent(MenuPage.this,ScanningPage.class);
                startActivity(settingsPage);
            }
        });

        associateCowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//                    Long idValue = settings.get_setting("Id");
                    Long idValue = 20L;
                    Intent intent = new Intent(MenuPage.this, InsertCowIdPage.class);
                    intent.putExtra("Id", Long.toString(idValue));
                    startActivity(intent);
                } catch (Exception e){
                    showToast("Unexpected error occurred!");
                }
            }
        });

        unassociateCowBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                endpointValue = "https://4fm1sus9w2.execute-api.eu-west-1.amazonaws.com/dev/pozyxtag";

                

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("farmid","123");
                    jsonObject.put("profiledata","123312");
                    jsonObject.put("animalid","123");
                    jsonObject.put("hardwareid","123ssdfs");
                }


                catch (Exception e){

                }
                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                RequestBody rb = RequestBody.create(JSON,jsonObject.toString());
                Toast.makeText(getApplicationContext(),"Cow unassociated from tag.", Toast.LENGTH_LONG);
                try {
                    OkHttpClient client = new OkHttpClient();

                    Request request = new Request.Builder()
                            .url(endpointValue)
                            .post(rb)
                            .addHeader("Content-Type", "application/json") //Notice this request has header if you don't need to send a header just erase this part
                            .build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Request request, IOException e) {
                            Toast.makeText(getApplicationContext(),"Error on posting "+e.toString(),Toast.LENGTH_LONG);
                        }

                        @Override
                        public void onResponse(Response response) throws IOException {
                            Toast.makeText(getApplicationContext(),"Posted",Toast.LENGTH_LONG);
                        }
                    });
                }
                catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Error on posting "+e.toString(),Toast.LENGTH_LONG);
                }
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