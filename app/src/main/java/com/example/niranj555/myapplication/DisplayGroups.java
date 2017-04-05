package com.example.niranj555.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.json.JSONObject.NULL;

public class DisplayGroups extends AppCompatActivity  {

    public static final String MyPREFERENCES = "MyPrefs" ;
    LocationManager locationManager;
    double longitudeGPS, latitudeGPS;
    TextView testDisplay;
    ListView listView;

    private boolean checkLocation() {
        if(!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }


    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        testDisplay= (TextView) findViewById(R.id.textView);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 2 * 60 * 1000, 10, locationListenerNetwork);


        Location location =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        longitudeGPS = location.getLongitude();
        latitudeGPS = location.getLatitude();

        testDisplay.setText(longitudeGPS + "");
      //  testDisplay.setText(latitudeGPS + "");

        SharedPreferences phoneDetails = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);

        String PhoneValue = phoneDetails.getString("P    public static final String MyPREFERENCES = \"MyPrefs\" ;\nhone", "");
        String TokenValue = phoneDetails.getString("Token", "");

        JSONObject requestMap = new JSONObject();
        try {
            requestMap.put("phone", PhoneValue);
            requestMap.put("token", TokenValue);
            requestMap.put("mode", "create");

            JSONObject coordinates=new JSONObject();
            //coordinates = Double.toString(latitudeGPS) + "," + Double.toString(longitudeGPS);

            coordinates.put("latitude",Double.toString(latitudeGPS));
            coordinates.put("longitude",Double.toString(longitudeGPS));

            requestMap.put("location",coordinates);

        }
        catch(JSONException e){}



       testDisplay.setText(Double.toString(longitudeGPS)+ " "+ Double.toString(latitudeGPS));


        Log.d("Coordinates",Double.toString(longitudeGPS)+ " "+ Double.toString(latitudeGPS));
        Log.d("My OUTPUT", PhoneValue + " " + TokenValue);

        URLDataHash mydata = new URLDataHash();
        mydata.url = "192.168.43.231";
        mydata.apicall = "user/group/list";
        mydata.hashMap=requestMap;


        try {

            final JSONObject data = new nodeHttpRequest(this).execute(mydata).get();
            Log.d("MYAPP:", data.toString());


            String response = data.toString();

            if(response==NULL)
            {
                Log.d("NULL object","");

            }
            else {
                try{
                JSONArray groups = data.getJSONArray("resp");
                }catch(JSONException e){}


                JSONObject currentObj=new JSONObject();
                final JSONObject GroupIdNameHash=new JSONObject();
                final ArrayList<String> groupList=new ArrayList<String>();
                for(int i=0;i<groups.length();i++)
                {
                    try {
                        currentObj = groups.getJSONObject(i);
                        Log.d("MYAPP: Json Parse", currentObj.toString());
                        groupList.add(currentObj.getString("gname"));
                        GroupIdNameHash.put(currentObj.getString("gname"), currentObj.getString("gid"));
                    }
                    catch(JSONException e){}


                    Log.d("MYAPP: Group Names",groupList.toString());
                }


                listView = (ListView) findViewById(R.id.showGroups);

                final ArrayAdapter adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1,
                        groupList);


                listView.setAdapter(adapter);

                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addGroupButton);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
             /*   Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/




                        Intent Intent = new Intent(view.getContext(), DisplayContacts.class);
                        view.getContext().startActivity(Intent);
                    }
                });


            }

        }
        catch (InterruptedException e){
            e.printStackTrace();

        }
        catch (ExecutionException e){
            e.printStackTrace();

        }



    }


    private final LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitudeGPS = location.getLongitude();
            latitudeGPS = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    testDisplay.setText(longitudeGPS + "");
                    testDisplay.setText(latitudeGPS + "");
                    Toast.makeText(DisplayGroups.this, "Network Provider update", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };



}
