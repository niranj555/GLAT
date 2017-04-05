package com.example.niranj555.myapplication;

/**
 * Created by niranj555 on 12-03-2017.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by antar on 05-Feb-17.
 */


public class nodeHttpRequest extends AsyncTask<URLDataHash, Void, JSONObject> {

    private Context context;
    public String requestData;
    public nodeHttpRequest(Context context) {
        this.context = context;
    }

    protected void onPreExecute() {

    }

    @Override
    protected JSONObject doInBackground(URLDataHash... mydata) {
        //"192.168.43.231"
        String link="http://"+mydata[0].url+":8080"+"/"+mydata[0].apicall;
        JSONObject jsonobj = new JSONObject();
        // NOTE: Nested JSON is possible
        jsonobj=mydata[0].hashMap;
        BufferedReader bufferedReader;
        String result;
        Log.d("Reached 1","");
        try {
            String message = jsonobj.toString();
            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setReadTimeout(10000);
            con.setConnectTimeout(15000);
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setChunkedStreamingMode(0);// Size of data unknown
            // making http header
            con.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            con.setRequestProperty("X-Requested-With", "XMLHttpRequest");
            //Connect
            con.connect();
            //setup send
            OutputStream os = new BufferedOutputStream(con.getOutputStream());
            os.write(message.getBytes());
            os.flush();
            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            result = bufferedReader.readLine();
            //con.disconnect();
            // Parsing String to JSON
            Log.d("MYAPP",result);
            JSONObject myobj = new JSONObject(result);
            return myobj;

        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }

    }

    @Override
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);
        //this.requestData = result;
        //Log.d("Data",result);
    }
}
