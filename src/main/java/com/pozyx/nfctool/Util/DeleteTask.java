package com.pozyx.nfctool.Util;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
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

public class DeleteTask extends AsyncTask<String, Void, Integer> {
    private String tagId;
    private String endpoint;
    private Integer status;

    @Override
    protected Integer doInBackground(String... params) {
        byte[] result = null;
        String str = "";
        // Create a new HttpClient and Post Header
        endpoint = params[0];
        tagId = params[1];
        endpoint = endpoint + tagId;
        HttpClient httpclient = new DefaultHttpClient();
        HttpDelete httpDelete = new HttpDelete(endpoint);


        try {
            httpDelete.setHeader("Content-type", "application/json");

            // Execute HTTP Post Request
            httpclient.getConnectionManager().getSchemeRegistry().register( new Scheme("https", SSLSocketFactory.getSocketFactory(), 443) );
            HttpResponse response = httpclient.execute(httpDelete);
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