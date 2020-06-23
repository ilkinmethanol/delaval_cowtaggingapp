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

import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.NfcWrapper;
import com.pozyx.nfctool.Util.PostTask;
import com.pozyx.nfctool.Util.TagSettings;
import com.pozyx.nfctool.Util.ProfileModel;
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

        //make nfcV object and read some data
        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        nfcvTag = NfcV.get(tag);
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //get data passed from NFCRequirementsPage

        byte[] settingsBytes = (byte[])getIntent().getSerializableExtra("TagSettingsBytes");
        if (settingsBytes == null){
            settingsBytes = nfc.readSettingsBlocks(nfcvTag);
        }

        settings = new TagSettings();
        if (settingsBytes.length != 0) {
            settings.deserialize(settingsBytes);
        }

        viewInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent settingsPage = new Intent(MenuPage.this,PinEnter.class);
                startActivity(settingsPage);
            }
        });

        associateCowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Long idValue = settings.get_setting("Id");
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
                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.i("TESTING", "endpoint: " + endpointValue);

                farmNameValue = pref.getString("farmName", "default").replace(" - ", ".");
                Log.i("TESTING", "farmName: " + farmNameValue);

                farmidValue = findFarm(farmNameValue).vcId;
                Log.i("TESTING", "farmid: " + farmidValue);

                cowidValue = "";


                try {
                    responseCode = new PostTask().execute(endpointValue, cowidValue="", tagidValue, farmidValue, farmNameValue="", "None","None").get();
                } catch (ExecutionException e) {
                    responseCode = null;
                } catch (InterruptedException e) {
                    responseCode = null;
                    e.printStackTrace();
                }

                if (responseCode == null) {
                    Toast.makeText(MenuPage.this, "Error! Make sure your endpoint URL is correct!", Toast.LENGTH_LONG).show();
                } else if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
                    Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getApplicationContext(),"Tag unassociated from cow", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 404) {
                    Toast.makeText(MenuPage.this, "Endpoint not found!", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 401 || responseCode == 403) {
                    Toast.makeText(MenuPage.this, "You are not authorized to access this endpoint!", Toast.LENGTH_LONG).show();
                } else if (responseCode == 500) {
                    Toast.makeText(MenuPage.this, "Server error!", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 400) {
                    Toast.makeText(MenuPage.this, "Server error - 400", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 415) {
                    Toast.makeText(MenuPage.this, "Server error - 415", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 409) {
                    Toast.makeText(MenuPage.this, "This resource already exist!", Toast.LENGTH_SHORT).show();
                } else if (responseCode == 408 || responseCode == 504) {
                    Toast.makeText(MenuPage.this, "Server timeout!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Toast.makeText(MenuPage.this, "Error occurred - " + responseCode.toString(), Toast.LENGTH_LONG).show();
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