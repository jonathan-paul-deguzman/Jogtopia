package com.jpdeguzman.jogtopia.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jpdeguzman.jogtopia.R;

/**
 *  CreateJogActivity serves the purpose of allowing a user to select a location for the jog to
 *  begin. This location can be near the user's current location (which is automatically shown when
 *  the Google Map is first brought up) or a location that can be entered using Google Place API.
 *  Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run time.
 *  If the permission is not granted, the user will not be notified that the map is unavailable.
 */

public class CreateJogActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = CreateJogActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jog);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isLocationPermissionGranted()) {
            requestForLocationPermission();
        } 
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getLastLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult:permissionGranted");
                getLastLocation();
            } else {
                Log.d(TAG, "onRequestPermissionsResult:permissionFailed");
                Toast.makeText(getApplicationContext(),
                        R.string.location_permissions_denied, Toast.LENGTH_SHORT);
            }
        }
    }

    private Boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Log.d(TAG, "getLastLocation:permissionStatus:" + isLocationPermissionGranted());
        if (isLocationPermissionGranted()) {
            mGoogleMap.setMyLocationEnabled(true);
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Log.d(TAG, "getLastLocation:success");
                                mLastLocation = task.getResult();
                                LatLng location = new LatLng(mLastLocation.getLatitude(),
                                        mLastLocation.getLongitude());
                                placeMarkerOnMap(location);
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13));
                            } else {
                                Log.d(TAG, "getLastLocation:exception:", task.getException());
                            }
                        }
                    });
        }
    }

    private void placeMarkerOnMap(LatLng location) {
        MarkerOptions newMarker = new MarkerOptions().position(location);
        mGoogleMap.addMarker(newMarker);
    }
}
