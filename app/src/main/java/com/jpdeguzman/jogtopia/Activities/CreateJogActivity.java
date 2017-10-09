package com.jpdeguzman.jogtopia.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.jpdeguzman.jogtopia.Fragments.TimePickerFragment;
import com.jpdeguzman.jogtopia.R;

/**
 *  CreateJogActivity serves the purpose of allowing a user to select a location for the jog to
 *  begin. This location can be near the user's current location (which is automatically shown when
 *  the Google Map is first brought up) or a location that can be entered using Google Place API.
 *  Permission for {@link android.Manifest.permission#ACCESS_FINE_LOCATION} is requested at run time.
 *  If the permission is not granted, the user will not be notified that the map is unavailable.
 */

public class CreateJogActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener {

    private static final String TAG = CreateJogActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private static final int REQUEST_PLACE_PICKER = 2;
    private static final int DEFAULT_ZOOM_VALUE = 15;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mGoogleMap;
    private Location mLastLocation;
    private LatLng mMarkerPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_jog);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        FloatingActionButton searchForPlace = this.findViewById(R.id.map_find_place);
        searchForPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadPlacePicker();
            }
        });
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        getLastLocation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                placeMarkerOnMap(place.getLatLng());
            }
        }
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

    private void requestForLocationPermission() {
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_ACCESS_FINE_LOCATION);
    }

    private Boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Log.d(TAG, "getLastLocation:permissionStatus:" + isLocationPermissionGranted());
        if (isLocationPermissionGranted()) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.setOnMarkerClickListener(this);
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
                                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,
                                        DEFAULT_ZOOM_VALUE));
                            } else {
                                Log.d(TAG, "getLastLocation:exception:", task.getException());
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClick:markerIsClicked");
        setMarkerPosition(marker.getPosition());
        DialogFragment timePicker = new TimePickerFragment();
        timePicker.show(getFragmentManager(), "timePicker");
        return false;
    }

    private void placeMarkerOnMap(LatLng location) {
        MarkerOptions newMarker = new MarkerOptions().position(location);
        mGoogleMap.addMarker(newMarker);
    }

    private void loadPlacePicker() {
        PlacePicker.IntentBuilder placePicker = new PlacePicker.IntentBuilder();
        try {
            // If this works, we expect to receive the selected place during onActivityResult
            startActivityForResult(placePicker.build(CreateJogActivity.this), REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public LatLng getMarkerPosition() {
        return mMarkerPosition;
    }

    public void setMarkerPosition(LatLng markerPosition) {
        mMarkerPosition = markerPosition;
    }
}
