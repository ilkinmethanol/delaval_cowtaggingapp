package com.pozyx.nfctool;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import com.pozyx.nfctool.Util.FarmModel;
import com.pozyx.nfctool.Util.FarmsHelper;
import com.pozyx.nfctool.Util.PostTask;
import com.pozyx.nfctool.Util.TagSetting;
import com.pozyx.nfctool.Util.TagSettings;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class InsertCowIdPage extends AppCompatActivity  {

    private Button button;
    private Button button2;
    private EditText cowid;
    private String endpointValue;
    private String farmidValue;
    private String farmNameValue;
    private String tagidValue;
    private String cowidValue;
    private Double latValue = 0.0;
    private Double lngValue = 0.0;
    private Integer responseCode;
    private Boolean isConnected;
    final int LOCATION_PERMISSION_REQUEST_CODE = 1252; //Note that the value 1252 is arbitrary.
    private LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_cow_id_page);


        findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
        findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);

        cowid = findViewById(R.id.cowIdInput);
        cowid.setRawInputType(Configuration.KEYBOARD_QWERTY);


        Intent intent = getIntent();
        String currentTextScan = intent.getExtras().getString("currentTextScan");
        //tagidValue = intent.getExtras().getString("Id");
        tagidValue = intent.getExtras().getString("Id");
        cowid.setText(currentTextScan);
        findViewById(R.id.cowIdInput).requestFocus();
        new Handler().postDelayed(new Runnable() {

            public void run() {
//        ((EditText) findViewById(R.id.et_find)).requestFocus();
//
                EditText yourEditText = (EditText) findViewById(R.id.cowIdInput);
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(yourEditText, InputMethodManager.SHOW_IMPLICIT);

                yourEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                yourEditText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 200);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 23) { // Marshmallow

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // TODO Auto-generated method stub
                if (FarmsHelper.FarmHelper.latValue == 0.0 && FarmsHelper.FarmHelper.lngValue == 0.0)
                {
                    setLocation(location.getLatitude(), location.getLongitude());
                }
                else
                {
                    double latitude=location.getLatitude();
                    double longitude=location.getLongitude();
                    FarmsHelper.FarmHelper.latValue = latitude;
                    FarmsHelper.FarmHelper.lngValue = longitude;
                    Log.i("Geo_location","Latitude: "+latitude+" ,Longitude: "+longitude);

                }
            }
            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
                FarmsHelper.FarmHelper.latValue = 0.0;
                FarmsHelper.FarmHelper.lngValue = 0.0;
            }
            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
                FarmsHelper.FarmHelper.latValue = 0.0;
                FarmsHelper.FarmHelper.lngValue = 0.0;
            }
            @Override
            public void onStatusChanged(String provider, int status,
                                        Bundle extras) {
                // TODO Auto-generated method stub
            }
        });


/*        findViewById(R.id.cancelButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findViewById(R.id.submitButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    public void cancelButtonCowId(View view)
    {
        try {
            Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(InsertCowIdPage.this, "Unexpected error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveButtonCowId(View view)
    {
        try {
            hideKeyboard(InsertCowIdPage.this);
            isConnected = isNetworkConnected();
            if (isConnected) {


                final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                endpointValue = pref.getString("endpoint", "https://z4554h0e4m.execute-api.eu-west-1.amazonaws.com/prod/item");
                Log.i("TESTING", "endpoint: " + endpointValue);

                farmNameValue = pref.getString("farmName", "default").replace(" - ", ".");
                Log.i("TESTING", "farmName: " + farmNameValue);

                farmidValue = findFarm(farmNameValue).vcId;
                Log.i("TESTING", "farmid: " + farmidValue);

                cowidValue = cowid.getText().toString();
                if (cowidValue.length() == 0 || cowidValue.length() > 6)
                {
                    Toast.makeText(this, "Given CowID is incorrect!", Toast.LENGTH_LONG).show();
                    return;
                }
                Log.i("TESTING", "cowid: " + cowidValue);

                Log.i("TESTING", "tagid: " + tagidValue);
                Log.i("TESTING", "Latitude: " + FarmsHelper.FarmHelper.latValue);
                Log.i("TESTING", "Longitude: " + FarmsHelper.FarmHelper.lngValue);


                LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                boolean gps_enabled = false;
                boolean network_enabled = false;
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if(!gps_enabled) {
                    // notify user
                    FarmsHelper.FarmHelper.latValue = 0.0;
                    FarmsHelper.FarmHelper.lngValue = 0.0;
                    new AlertDialog.Builder(InsertCowIdPage.this)
                            .setMessage("GPS needs to be on. Enable it, please.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            })
                            .setNegativeButton("No",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                    return;
                                }
                            })
                            .show();
                    return;
                }

                if (FarmsHelper.FarmHelper.latValue == 0.0 || FarmsHelper.FarmHelper.lngValue == 0.0)
                {
                    findViewById(R.id.loadingText).setVisibility(View.VISIBLE);
                    findViewById(R.id.loadingCircle).setVisibility(View.VISIBLE);
                    return;
                }

                if (farmidValue.isEmpty() || farmidValue == null || farmidValue == "default") {
                    Toast.makeText(InsertCowIdPage.this, "Missing Farm ID, please check Settings!", Toast.LENGTH_LONG).show();
                } else if (endpointValue.isEmpty() || endpointValue == null || endpointValue == "default") {
                    Toast.makeText(InsertCowIdPage.this, "Missing endpoint URL, please check Settings!", Toast.LENGTH_LONG).show();
                } else if (cowidValue.isEmpty() || cowidValue == null) {
                    Toast.makeText(InsertCowIdPage.this, "Missing Cow ID!", Toast.LENGTH_SHORT).show();
                } else if (!URLUtil.isValidUrl(endpointValue)) {
                    Toast.makeText(InsertCowIdPage.this, "Given endpoint URL is not valid!", Toast.LENGTH_SHORT).show();
                } else if (!URLUtil.isValidUrl(endpointValue)) {
                    Toast.makeText(InsertCowIdPage.this, "Given endpoint URL is not valid!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        responseCode = new PostTask().execute(endpointValue, cowidValue, tagidValue, farmidValue, farmNameValue, String.valueOf(FarmsHelper.FarmHelper.latValue), String.valueOf(FarmsHelper.FarmHelper.lngValue)).get();
                    } catch (ExecutionException e) {
                        responseCode = null;
                    } catch (InterruptedException e) {
                        responseCode = null;
                        e.printStackTrace();
                    }

                    if (responseCode == null) {
                        Toast.makeText(InsertCowIdPage.this, "Error! Make sure your endpoint URL is correct!", Toast.LENGTH_LONG).show();
                    } else if (responseCode == 200 || responseCode == 201 || responseCode == 204) {
                        Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        Toast.makeText(InsertCowIdPage.this, "Added successfully!", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 404) {
                        Toast.makeText(InsertCowIdPage.this, "Endpoint not found!", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 401 || responseCode == 403) {
                        Toast.makeText(InsertCowIdPage.this, "You are not authorized to access this endpoint!", Toast.LENGTH_LONG).show();
                    } else if (responseCode == 500) {
                        Toast.makeText(InsertCowIdPage.this, "Server error!", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 400) {
                        Toast.makeText(InsertCowIdPage.this, "Server error - 400", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 415) {
                        Toast.makeText(InsertCowIdPage.this, "Server error - 415", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 409) {
                        Toast.makeText(InsertCowIdPage.this, "This resource already exist!", Toast.LENGTH_SHORT).show();
                    } else if (responseCode == 408 || responseCode == 504) {
                        Toast.makeText(InsertCowIdPage.this, "Server timeout!", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ScanningPage.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(InsertCowIdPage.this, "Error occurred - " + responseCode.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(InsertCowIdPage.this, "No internet connection.", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(InsertCowIdPage.this, "Unexpected error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLocation(double latitude, double langitude)
    {
        FarmsHelper.FarmHelper.latValue = latitude;
        FarmsHelper.FarmHelper.lngValue = langitude;

        if (findViewById(R.id.loadingCircle).getVisibility() == View.VISIBLE)
        {
            findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
            findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);
            findViewById(R.id.submitButton2).performClick();
        }
        else
        {
            findViewById(R.id.loadingText).setVisibility(View.INVISIBLE);
            findViewById(R.id.loadingCircle).setVisibility(View.INVISIBLE);
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    FarmModel findFarm(String codeIsIn) {
        for(FarmModel farm : FarmsHelper.FarmHelper.farms) {
            if(farm.shortName.equals(codeIsIn)) {
                return farm;
            }
        }
        return null;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else { // if permission is not granted

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                    return;
                }
                // decide what you want to do if you don't get permissions
                Toast.makeText(this, "GPS permission is required!", Toast.LENGTH_LONG).show();
            }
        }
    }

/*    public LatLng getLocation() {
        // Get the location manager
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }
        if (!locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return null;
        }
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        Double lat,lon;
        try {
            lat = location.getLatitude();
            lon = location.getLongitude();
            return new LatLng(lat, lon);
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return null;
        }
    }*/

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
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

class LatLng {
    Double lat;
    Double lng;
    LatLng(Double lat, Double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }
}
