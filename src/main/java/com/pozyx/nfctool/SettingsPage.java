package com.pozyx.nfctool;

import android.Manifest;
import android.app.ActionBar;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.AppOpsManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pozyx.nfctool.Util.DeleteTask;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.NfcWrapper;
import com.pozyx.nfctool.Util.PostTask;
import com.pozyx.nfctool.Util.TagSetting;
import com.pozyx.nfctool.Util.TagSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.HashMap;

public class SettingsPage extends AppCompatActivity {
    private String UID;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent ;
    private NfcV nfcvTag;
    private Boolean DEACTIVATE = true;
    private TextView tag_memory;
    private TextView UIDChange, enabledChange,idChange,percentChange, sleepChange, variationChange, settingsChange,accChange,channelChange,preambleChange,datarateChange,prfChange;
    private ProgressBar progressChange;
    private Button activateButton;

    private HashMap<String, TextView> settings_views;
    TagSettings settings;
    NfcWrapper nfc = new NfcWrapper();

    Toast current_toast;
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
        setContentView(R.layout.activity_settings_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

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

            final TableLayout settings_table = findViewById(R.id.SettingsTable);
            settings_table.setPadding(15,15,15,15);
            settings_views = new HashMap<String, TextView>();

            TableRow uid_row = new TableRow(this);
            TextView uid_name = new TextView(this);
            TextView uid_value = new TextView(this);
            //Typeface face = ResourcesCompat.getFont(this, R.font.azo_sans_regular);



            for (String setting_name : settings.settings_order) {
                TagSetting setting = settings.settingsmap.get(setting_name);
                TableRow row = new TableRow(this);
                TextView name = new TextView(this);
                //value.setTypeface(face); //we need to force the right font to be used, because android doesn't force the default one
                //name.setTypeface(face);

                name.setText(setting.getName());
                name.setTextSize(13);
                row.addView(name);
                row.addView(setting.getView(this));
                if (setting.getName() == "Threshold"){
                    TextView tv = new TextView(this);
                    tv.setText("Accelerometer aggregation settings");
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    tv.setPadding(0, 20, 0, 0);
                    settings_table.addView(tv);
                }
                if (setting.getName() == "Power"){
                    TextView tv = new TextView(this);
                    tv.setText("Technical settings");
                    tv.setTypeface(tv.getTypeface(), Typeface.BOLD);
                    tv.setPadding(0, 20, 0, 0);
                    settings_table.addView(tv);
                }
                settings_table.addView(row);
            }
            activateButton = findViewById(R.id.deactivateButton2);

            if (settings.get_setting("State") == 0){   //activateButton default state is Deactivate
                DEACTIVATE = false;
                activateButton.setText("ACTIVATE");
                activateButton.setBackgroundColor(Color.GREEN);
            }

            //Finally add the listeners
            activateButton.setOnClickListener(new View.OnClickListener() {
                //button swaps between activate and deactivate
                @Override
                public void onClick(View v) {
                    if (nfcvTag.isConnected()){ //check if tag is already busy
                        showToast("Commands following too fast");
                        try{nfcvTag.close();} catch(Exception e) {};
                        return;
                    }

                    if (DEACTIVATE == true) {
                        try{
                            HashMap<Integer, byte[]> deactivation = new HashMap<Integer, byte[]>(){{put(0, new byte[]{(byte)1,(byte)settings.get_setting("Hardware version"),(byte)settings.get_setting("Firmware version"),(byte)0});}};
                            nfcvTag.connect();
                            nfc.writeBlocks(nfcvTag, deactivation);
                            nfc.sendInterrupt(nfcvTag);
                            nfcvTag.close();
                        }catch(IOException | NullPointerException e) {
                            showToast("Deactivate failed");
                        }
                    } else {
                        try {
                            HashMap<Integer, byte[]> activation = new HashMap<Integer, byte[]>(){{put(0, new byte[]{(byte)1,(byte)settings.get_setting("Hardware version"),(byte)settings.get_setting("Firmware version"),(byte)1});}};
                            nfcvTag.connect();
                            nfc.writeBlocks(nfcvTag, activation);
                            nfc.sendInterrupt(nfcvTag);
                            nfcvTag.close();
                        } catch (IOException | NullPointerException e) {
                            showToast("Activate failed");
                        }
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //finally read the settings and updateyes the view
                                byte[] settingsBytes = nfc.readSettingsBlocks(nfcvTag);
                                settings.deserialize(settingsBytes);
                                updateView();
                            } catch (Exception e){

                            }
                        }
                    }, 100);

                }
            });

            findViewById(R.id.read_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nfcvTag.isConnected()){ //check if tag is already busy
                        showToast("Commands following too fast");
                        try{nfcvTag.close();} catch(Exception e) {};
                        return;
                    }
                    try {
                        byte[] settingsBytes = nfc.readSettingsBlocks(nfcvTag);
                        settings.deserialize(settingsBytes);
                        updateView();
                        showToast("Read successfully!");
                    } catch (Exception e){
                        showToast("Keep tag close to mobile!");
                    }

//                    Toast.makeText(getApplicationContext(),)

                }
            });

            findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (nfcvTag.isConnected()) { //check if tag is already busy
                        showToast("Commands following too fast");
                        try {
                            nfcvTag.close();
                        } catch (Exception e) {
                        }
                        ;
                        return;
                    }
                    try {

                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        String prfconf_json = prefs.getString("configjson","");

                        JSONObject jObj = new JSONObject(prfconf_json);
                        JSONArray jsonArry = jObj.getJSONArray("profileconfig");

                        JSONObject configobj = jsonArry.getJSONObject(0);

                        Integer mem_samples_interval = Integer.parseInt(configobj.optString("samples_interval"));
                        Integer mem_agg_alg = Integer.parseInt(configobj.optString("agg_alg"));
                        Integer mem_min_active_blinks = Integer.parseInt(configobj.optString("minimum_activeblinks"));
                        Integer mem_minlevel_active_blinks = Integer.parseInt(configobj.optString("minimumlevel_activeblinks"));


                        settings.set_setting("Threshold",12124);
                        settings.set_setting("Samples per interval",mem_samples_interval);
                        settings.set_setting("Aggregation algorithm",mem_agg_alg);
                        settings.set_setting("Minimum active blinks",mem_min_active_blinks);
                        settings.set_setting("Minimum level active blinks",mem_minlevel_active_blinks);


//                        tag_memory.setText(settings.get_setting("Tg"));
                        HashMap<Integer, byte[]> changedBlocks = settings.getChangedBlocks();
                        nfcvTag.connect();
                        nfc.writeBlocks(nfcvTag, changedBlocks);
                        nfc.sendInterrupt(nfcvTag);
                        nfcvTag.close();
                        showToast("Written successfully"+prfconf_json);
                    } catch (IOException | NullPointerException e) {
                        showToast("Keep tag close to mobile!"+e.toString());
                    } catch (Exception e) {
                        showToast(e.getMessage());
                    }

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //finally read the settings and update the view
                                byte[] settingsBytes = nfc.readSettingsBlocks(nfcvTag);
                                settings.deserialize(settingsBytes);
                                updateView();
                            } catch (Exception e) {

                            }
                        }
                    }, 100);
                }
            });

            findViewById(R.id.nextButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        Long idValue = settings.get_setting("Id");

                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("Id",idValue.toString());
                        Intent intent = new Intent(SettingsPage.this, MenuPage.class);
                        intent.putExtra("Id", Long.toString(idValue));
                        startActivity(intent);
                    } catch (Exception e){
                        showToast("Unexpected error occurred!");
                    }
                }
            });

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Long idValue = settings.get_setting("Id");
                    String tagId = Long.toString(idValue);
                    Integer responseCode = new DeleteTask().execute("https://z4554h0e4m.execute-api.eu-west-1.amazonaws.com/prod/item/", tagId).get();
                    if (responseCode == 200 || responseCode == 202 || responseCode == 204)
                    {
                        Toast.makeText(SettingsPage.this, "Tag deleted!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(SettingsPage.this, "Tag not found on server to delete.", Toast.LENGTH_LONG).show();
                    }

                    Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);

                } catch (Exception e){
                    showToast("Keep tag close to mobile!");
                }
            }
        });

            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            //get value for activateswitch from sharedpreferences and execute below block if it is on
            if (settings.get_setting("State") == 0 && pref.getBoolean("isActivate", true))
            {
                findViewById(R.id.deactivateButton2).performClick();
            }


    }



    @Override
    public void onNewIntent(Intent intent){ //when a tag is introduced on page 3, this triggers
        super.onNewIntent(intent);
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (java.util.Arrays.equals(nfcvTag.getTag().getId(), tag.getId())) {
            nfcvTag = NfcV.get(tag);
            try{
                byte[] statusblock = nfc.readStatusBlocks(nfcvTag);
                settings.deserializeStatus(statusblock);
                updateView();
                return;
            }
            catch (Exception e){
                Toast.makeText(this, "Keep the tag closer!", Toast.LENGTH_SHORT).show();
            }
        }

        Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);

        if (parcelables != null && parcelables.length>0){
            readTextFromMessage((NdefMessage) parcelables[0]);
        }else {
            Toast.makeText(this, "No data found in tag!", Toast.LENGTH_SHORT).show();
        }
        nfcvTag = NfcV.get(tag);
        //read tag again
        try {
            byte[] settingsBytes = nfc.readSettingsBlocks(nfcvTag);
            settings.deserialize(settingsBytes);
            updateView();
        } catch (Exception e) {
            showToast("Read failed.");
        }

    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        tag_memory = (TextView)findViewById(R.id.tagMemory);
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefRecords != null && ndefRecords.length>0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            tag_memory.setText(tagContent);
        }else {
            Toast.makeText(this, "No data NDEF found in tag!", Toast.LENGTH_SHORT).show();
        }
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord)
    {
        String tagContent = null;
        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1,
                    payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }
        return tagContent;
    }

    private void updateView() //update all the views
    {
        if(settings.get_setting("State") == 1){
            DEACTIVATE = true;
            activateButton.setText("DEACTIVATE");
            activateButton.setBackgroundColor(Color.RED);
        }else{
            DEACTIVATE = false;
            activateButton.setText("ACTIVATE");
            activateButton.setBackgroundColor(Color.GREEN);
        }
    }

    public void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    public void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        //hide 2 top bars
        //View decorView = getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        //android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.hide();
    }
}
