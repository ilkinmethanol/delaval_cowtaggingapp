package com.pozyx.nfctool.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class PostTask extends AsyncTask<String, Void, Integer> {
    private String endpoint;
    private String cowid;
    private String tagid;
    private String farmid;
    private String farmName;
    private Integer status;
    private Double latValue;
    private Double lngValue;

    @Override
    protected Integer doInBackground(String... params) {
        byte[] result = null;
        String str = "";
        // Create a new HttpClient and Post Header
        endpoint = params[0];
        cowid = params[1];
        tagid = params[2];
        farmid = params[3];
        farmName = params[4];
        latValue = Double.parseDouble(params[5]);
        lngValue = Double.parseDouble(params[6]);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(endpoint);


        try {
            postData data = new postData();

            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            df.setTimeZone(TimeZone.getTimeZone("UTC"));
            String utcTime = df.format(new Date());

            data.cowId = cowid;
            data.tagId = tagid;
            data.farmId = farmid;
            data.latitude = latValue.toString();
            data.longitude = lngValue.toString();
            data.delProVersion = "2";

            Gson gson= new Gson();
            StringEntity postingString = new StringEntity(gson.toJson(data));

            //String json = gson.toJson(data);

            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(postingString);

            // Execute HTTP Post Request
            httpclient.getConnectionManager().getSchemeRegistry().register( new Scheme("https", SSLSocketFactory.getSocketFactory(), 443) );
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            status = statusLine.getStatusCode();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        return status;
    }
}

class postModel
{
    postData Item;
    String TableName;
}

class postData
{
    String cowId;
    String tagId;
    String farmId;
    String latitude;
    String delProVersion;
    String longitude;
}

class cowID
{
    String S;
}
class tagID
{
    String S;
}
class farmID
{
    String S;
    String Name;
}
class latitude
{
    String lat;
}
class langitude
{
    String lng;
}
class timestamp
{
    String S;
}
class farmRegVar
{
    String S;
}


