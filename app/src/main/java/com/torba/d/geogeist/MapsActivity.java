    package com.torba.d.geogeist;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.torba.d.geogeist.MainActivity.LATITUDE;
import static com.torba.d.geogeist.MainActivity.LONGITUDE;

    public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button clickButton = (Button) findViewById(R.id.button);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                LatLng latLng = marker.getPosition();
                Intent resultIntent = new Intent();
                resultIntent.putExtra(LONGITUDE, latLng.longitude);
                resultIntent.putExtra(LATITUDE, latLng.latitude);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Bundle extras = getIntent().getExtras();
        Double lat = (Double) extras.get(LATITUDE);
        Double lon = (Double) extras.get(LONGITUDE);
        LatLng here = new LatLng(lat, lon);
        marker = mMap.addMarker(new MarkerOptions().position(here).title("You are here"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng position) {
                marker.remove();
                marker = mMap.addMarker(new MarkerOptions().position(position));
            }
        });

    }
}
