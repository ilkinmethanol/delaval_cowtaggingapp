package com.pozyx.nfctool;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pozyx.nfctool.Util.NfcWrapper;
import com.pozyx.nfctool.Util.TagSetting;
import com.pozyx.nfctool.Util.TagSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ScanningPage extends AppCompatActivity {
    private NfcAdapter mNfcAdapter; //phone nfc adapter
    private PendingIntent mPendingIntent ;
    private static final String AWAITING = "Please hold device" + '\n' + "up to tag";
    private Button activateButton;
    private NfcV nfcvTag; //NfcV tag object made from regular tag object
    private Tag tag; //represents physical NFC tag
    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    final int LOCATION_PERMISSION_REQUEST_CODE = 1516; //Note that the value 1252 is arbitrary.
    TagSettings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanning_page);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String prfconf_json = prefs.getString("configjson","");

        JSONObject jObj = null;
        JSONObject configobj = null;
        try {
            jObj = new JSONObject(prfconf_json);
            JSONArray jsonArry = jObj.getJSONArray("profileconfig");

            configobj = jsonArry.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//                mem_threshold = Integer.parseInt(configobj.optString("threshold"));
//                mem_minimum_trigger_count = Integer.parseInt(configobj.optString("minimum_trigger_count"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            return;
        }

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity
                (this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            } else { // if permission is not granted

                this.finishAffinity();
/*                System.exit(0);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                }*/
                // decide what you want to do if you don't get permissions
                Toast.makeText(this, "GPS permission is required! Grant it, please.", Toast.LENGTH_LONG).show();
            }
        }
    }

        @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.settingsButton:

                Intent intent = new Intent(this, OptionsPage.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent) { //this is called when a tag is discovered
        try {

            super.onNewIntent(intent);
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG); //get the tag object from new intent
            //make nfcV object
            nfcvTag = NfcV.get(tag);
            //try to get all tag data
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            String topActivity = am.getRunningTasks(1).get(0).topActivity.getClassName();
            if (topActivity.equals("com.pozyx.nfctool.ScanningPage") || topActivity.equals("com.pozyx.nfctool.SettingsPage"))
            {
                NfcWrapper nfc = new NfcWrapper();
                byte[] settings_bytes = nfc.readSettingsBlocks(nfcvTag);
                settings = new TagSettings();
                if (settings_bytes.length != 0) {
                    settings.deserialize(settings_bytes);
                }
                Long idValue = settings.settingsmap.get("Id").getValue();
                Intent settings_intent = new Intent(ScanningPage.this, MenuPage.class);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString("TagSettingsBytes_pref", Base64.encodeToString(settings_bytes,Base64.DEFAULT));
                editor.putString("Id",String.valueOf(idValue));

                settings_intent.putExtra("TagSettingsBytes_int", settings_bytes);
                settings_intent.putExtra(NfcAdapter.EXTRA_TAG, tag);


                editor.commit();

                startActivity(settings_intent);
            }
        } catch (Exception e) {
            //display warning if process is aborted
            Context context = getApplicationContext();
            CharSequence text = "Read Failed, Try Again";
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.setGravity(Gravity.TOP, 0, 100);
            toast.show();
            return;
        }

    }

    private void formatTag(Tag tag,NdefMessage ndefMessage){
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable==null){
                Toast.makeText(this,"Tag is not formattable",Toast.LENGTH_LONG).show();
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
        }
        catch (Exception e){
            Log.e("formattag", e.getMessage());
        }
    }

    private  void  writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try {
            if (tag == null){
                Toast.makeText(this,"Tag is null",Toast.LENGTH_LONG);
                return;
            }
            Ndef ndef = Ndef.get(tag);

            if (ndef == null){
                formatTag(tag,ndefMessage);
            }
            else{
                ndef.connect();
                if (!ndef.isWritable()){
                    Toast.makeText(this,"Tag is not writable",Toast.LENGTH_LONG);
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();

                Toast.makeText(this,"Tag written",Toast.LENGTH_LONG);

            }
        }
        catch (Exception e){
            Log.e("writeerror",e.getMessage());
        }
    }



    private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord;
        ndefRecord = NdefRecord.createTextRecord("EN",content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord});
        return ndefMessage;
    }
    @Override
    public void onResume(){
        super.onResume();

        if(mNfcAdapter != null){
            mNfcAdapter.enableForegroundDispatch(this,mPendingIntent,null,null);
        }
        final TextView messageChange = findViewById(R.id.messageView3);
        messageChange.setText(AWAITING);

        //hide 2 top bars
        //View decorView = getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        //android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.hide();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mNfcAdapter != null){
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    public void onBackPressed() {
        //disable back button so user cant go back to error screen
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
