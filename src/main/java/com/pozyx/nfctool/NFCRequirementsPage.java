package com.pozyx.nfctool;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pozyx.nfctool.Util.GetFarmsTask;

import java.io.File;

public class NFCRequirementsPage extends AppCompatActivity {
    private Button buttonChange;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc_requirements_page);

        boolean supported = false;
        boolean enabled = false;
        buttonChange = findViewById(R.id.settingsButton);

        //attach listener to settings button
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
            }
        });

        //check supported and enabled
        NfcManager manager = (NfcManager) getBaseContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if(adapter != null && adapter.isEnabled()){
            supported = true;
            enabled = true;
        }else if(adapter == null){
            supported = false;
        }else if(adapter != null && !adapter.isEnabled()) {
            supported = true;
            enabled = false;

            //supported but not enabled
            //enable settings button
            buttonChange.setVisibility(View.VISIBLE);
        }

        //if both supported and enabled, go to page 2
        if(supported == true && enabled == true){
            Intent intent = new Intent(NFCRequirementsPage.this, ScanningPage.class);
            startActivity(intent);
        }else if(supported == true && enabled == false){ //nfc supported but not enabled
            final TextView enabledChange = findViewById(R.id.enabledText);
            enabledChange.setVisibility(View.VISIBLE);
        } else if(supported == false){ //nfc not supported by device, enabled doesnt matter
            final TextView supportedChange = findViewById(R.id.supportText2);
            supportedChange.setVisibility(View.VISIBLE);
        }else{
            //should never be reached
        }
    }

    @Override
    public void onResume(){
        super.onResume();

        //hide 2 top bars
        //View decorView = getWindow().getDecorView();
        //int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        //decorView.setSystemUiVisibility(uiOptions);
        //android.support.v7.app.ActionBar ab = getSupportActionBar();
        //ab.hide();


        NfcManager manager = (NfcManager) getBaseContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        //if user came back from enabling nfc, redirect
        if(adapter != null && adapter.isEnabled()){
            Intent intent = new Intent(NFCRequirementsPage.this, ScanningPage.class);
            startActivity(intent);
        }
    }
}
