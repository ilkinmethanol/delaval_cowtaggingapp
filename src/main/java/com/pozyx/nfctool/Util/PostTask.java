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

    private String body;
    private Integer status;
    @Override
    protected Integer doInBackground(String... params) {
        byte[] result = null;
        String str = "";
        // Create a new HttpClient and Post Header
        endpoint = params[0];
        body = params[1];
//        latValue = Double.parseDouble(params[5]);
//        lngValue = Double.parseDouble(params[6]);
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(endpoint);


        try {

            StringEntity postingString = new StringEntity(body);

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
