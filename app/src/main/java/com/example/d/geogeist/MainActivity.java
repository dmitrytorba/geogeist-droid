package com.example.d.geogeist;

import android.Manifest;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";

    private FusedLocationProviderClient mFusedLocationClient;
    private Intent mapIntent;

    private TextView mCountyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCountyName = (TextView) findViewById(R.id.countyName);
        mapIntent = new Intent(this, MapsActivity.class);
        FloatingActionButton mapButton = findViewById(R.id.fab);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mapIntent);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.ello
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                changeLocation(location);
                            } else {
                                // TODO: show error
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // TODO
    }

    public void changeLocation(Location location) {
        mapIntent.putExtra(LONGITUDE, location.getLongitude());
        mapIntent.putExtra(LATITUDE, location.getLatitude());
        String url = "https://geo.torba.us/data?lat=37.8787&lon=-122.1728";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        renderGeoData(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error

                    }
                });

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void renderGeoData(JSONObject data) {
        try {
            JSONObject county = data.getJSONObject("county");
            String countyName = county.getString("name");
            ((TextView) findViewById(R.id.countyName)).setText(countyName + " County");
            JSONObject population = county.getJSONObject("population");
            Integer totalPeople = population.getInt("total");
            Integer houses = county.getInt("houses");
            String text = withSuffix(totalPeople) + " people in " + withSuffix(houses) + " houses";
            ((TextView) findViewById(R.id.countyPopulation)).setText(text);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static String withSuffix(long count) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%.1f %c",
                count / Math.pow(1000, exp),
                "KMGTPE".charAt(exp-1));
    }
}
