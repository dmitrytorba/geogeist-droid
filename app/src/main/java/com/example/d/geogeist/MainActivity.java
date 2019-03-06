package com.example.d.geogeist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATITUDE = "LATITUDE";
    public static final String API_URL = "https://us-central1-geogeist-227901.cloudfunctions.net/coords";
    public static final String IMG_URL = "https://storage.googleapis.com/geogeist-227901.appspot.com/";
    public static final int COORDS = 1;
    public static final double MSMPH = 2.237;
    public static final double MFT = 3.281;


    private FusedLocationProviderClient mFusedLocationClient;
    private Intent mapIntent;

    private ProgressBar spinner;
    private SwipeRefreshLayout swiperefresh;
    private RecyclerView recyclerView;
    private RecyclerController recyclerController;

    private Location lastLocation;

    private void snackBar(String msg) {
        Snackbar bar = Snackbar.make(
                findViewById(R.id.coordinator),
                msg,
                Snackbar.LENGTH_SHORT);
        bar.show();
    }

    private void trackLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        final TextView locationText = findViewById(R.id.locationText);
        final TextView speedText = findViewById(R.id.speedText);
        final DecimalFormat df = new DecimalFormat("#.##");

        LocationListener gpsListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                locationText.setText("GPS location: " + location.getLongitude() + ", " + location.getLatitude());
                speedText.setText("Altitude: " + df.format(location.getAltitude()*MFT) + "ft Speed: " + df.format(location.getSpeed()*MSMPH) + "mph Bearing: " + location.getBearing());
                changeLocation(location.getLongitude(), location.getLatitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        LocationListener netListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                locationText.setText("Network location: " + location.getLongitude() + ", " + location.getLatitude());
                speedText.setText("Altitude: " + location.getAltitude()*MFT + "ft Speed: " + location.getSpeed()*MSMPH + "mph Bearing: " + location.getBearing());
                changeLocation(location.getLongitude(), location.getLatitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 100, netListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, gpsListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner = findViewById(R.id.spinner);
        mapIntent = new Intent(this, MapsActivity.class);
        FloatingActionButton mapButton = findViewById(R.id.fab);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(mapIntent, COORDS);
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
            trackLocation();
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                lastLocation = location;
                                changeLocation(location.getLongitude(), location.getLatitude());
                            } else {
                                // TODO: show error
                            }
                        }
                    });
        }


//        swiperefresh = findViewById(R.id.swiperefresh);
//        swiperefresh.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        if (lastLocation != null) {
//                            changeLocation(lastLocation.getLongitude(), lastLocation.getLatitude());
//                        }
//                    }
//                }
//        );

        recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        recyclerController = new RecyclerController();
        recyclerView.setAdapter(recyclerController);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        // TODO
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (COORDS) : {
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Double lat = (Double) extras.get(LATITUDE);
                    Double lon = (Double) extras.get(LONGITUDE);
                    changeLocation(lon, lat);
                }
                break;
            }
        }
    }

    public void changeLocation(Double lon, Double lat) {
        spinner.setVisibility(View.VISIBLE);
        mapIntent.putExtra(LONGITUDE, lon);
        mapIntent.putExtra(LATITUDE, lat);
        String url = API_URL + "?lat=" + lat + "&lon=" + lon;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        spinner.setVisibility(View.GONE);
//                        swiperefresh.setRefreshing(false);
                        recyclerController.loadData(response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swiperefresh.setRefreshing(false);
                        spinner.setVisibility(View.GONE);
                        snackBar("Server Error");

                    }
                });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

}
