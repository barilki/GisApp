package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.view.View;

public class GISmobile extends FragmentActivity implements OnMapReadyCallback, PopupMenu.OnMenuItemClickListener {

    private static final float DEFAULT_ZOOM = 10;
    private TextView Latitude, Longitude, Country, City;

    //callback method to get a handle to the GoogleMap object.
    //The callback is triggered when the map is ready to be used
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Map = googleMap;
        Map.getUiSettings().setZoomControlsEnabled(true);
        Map.getUiSettings().setMyLocationButtonEnabled(true);



        Log.d(TAG, "onMapReady: map is ready");
        if (mLocationPermissionsGranted) {
            getDevicesLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Map.setMyLocationEnabled(true);
            Map.getUiSettings().setMyLocationButtonEnabled(false);

            init();
        }

    }

    private static final String TAG = "MapActivity";
    public static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private Boolean mLocationPermissionsGranted = false;
    public static final int LOCATION_PERMISSION_REQUEST_CODE =1234;
    private GoogleMap Map;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //widgets
    private EditText mSearchText;
    private ImageView ic_magnify,mGPS;


    //set the layout file as the content view
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        mSearchText = findViewById(R.id.input_search);
        ic_magnify = findViewById(R.id.ic_magnify);
        mGPS = findViewById(R.id.mGPS);

        Latitude = findViewById(R.id.Latitude);
        Longitude = findViewById(R.id.Longitude);
        Country = findViewById(R.id.Country);
        City = findViewById(R.id.City);

        mGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevicesLocation();
            }
        });
        getLocationPermission();
    }

    //method that creates an object of the menu for the map types and activates the function
    public void showMaptype(View v){
        PopupMenu maptype =  new PopupMenu(this, v);
        maptype.setOnMenuItemClickListener(this);
        maptype.inflate((R.menu.popup_menu));
        maptype.show();
    }

    //method that is triggered when the user selects a particular map type and displays the selected type
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.Normal:
              Map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.Hybrid:
                Map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.Satellite:
                Map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.Terrain:
                Map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
              return true;
            default:
                return false;
        }
    }

    //method for initializing and activating the search function when entering input in the search box
    private void init()
    {
        Log.d(TAG, "init: init() called");
        ic_magnify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(GISmobile.this, "Searching..", Toast.LENGTH_SHORT).show();
                geolocate();
                hideSoftKeyboard();

            }
        });




//     mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//         @Override
//         public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//             Log.d(TAG, "onEditorAction: called");
//             if (actionId == EditorInfo.IME_ACTION_SEARCH
//                     || actionId == EditorInfo.IME_ACTION_DONE
//                     || event.getAction() == KeyEvent.ACTION_DOWN
//                     || event.getAction() == KeyEvent.KEYCODE_ENTER
//             )
//             {
//                 Toast.makeText(MapsActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
//                 // execute our Method for searching
//                 geolocate();
//
//             }
//             return false;
//         }
//     });
    }

    //Method of searching and displaying location information, latitude and longitude, city and country.
    private void geolocate()
    {
        Log.d(TAG, "geolocate: geolocating");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(GISmobile.this);
        List<Address> list = new ArrayList<>();
        try {
            list = geocoder.getFromLocationName(searchString,1);
        }catch (IOException e)
        {
            Log.d(TAG, "geolocate: IOEOexception" +e.getMessage());
        }
        if (list.size() >0){
            Address address = list.get(0);

            Log.d(TAG, "geolocate: found a location" + address.toString());
            Latitude.setText("" + list.get(0).getLatitude());
            Longitude.setText("" + list.get(0).getLongitude());
            Country.setText("" + list.get(0).getCountryName());
            City.setText("" + list.get(0).getLocality());

            moveCamera(new LatLng(address.getLatitude(),address.getLongitude()),DEFAULT_ZOOM
            ,address.getAddressLine(0));
        }
    }

    // Get the current location and show it on the map
    private void getDevicesLocation()
    {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Log.d(TAG, "getDevicesLocation: getting the devices location");
        Context context;

        try {
            if (mLocationPermissionsGranted)
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task)
                    {
                        if (task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();

                            Latitude.setText("" + currentLocation.getLatitude());
                            Longitude.setText("" + currentLocation.getLongitude());

                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");

                        }else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(GISmobile.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }



                    }});
            }


        }catch (SecurityException e)
        {

        }
    }

    //Move the map to the desired location
    private void moveCamera(LatLng latLng , float zoom,String title)
    {
        Log.d(TAG, "moveCanera: moving the camera to lat:"+latLng.latitude+"lng:" + latLng.longitude);
        Map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if (!title.equals("My Location"))
        {
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title(title);
            Map.addMarker(options);
        }
        hideSoftKeyboard();
    }
    private void initMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        Log.d(TAG, "initMap: initializing map");
        mapFragment.getMapAsync(this);
    }

    //Obtaining user permissions for the current location
    private void getLocationPermission()
    {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION)== PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionsGranted = true;
                initMap();
            }else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    //OnRequestPermissionsResultCallback - This interface is the contract for receiving the results for permission requests.
    //Callback for the result from requesting permissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        mLocationPermissionsGranted  = false;
        switch (requestCode)
        {
            case LOCATION_PERMISSION_REQUEST_CODE:
            {
                if (grantResults.length>0){
                    for (int i = 0; i <grantResults.length ; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted =false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }

                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void hideSoftKeyboard()
    {
        Log.d(TAG, "hideSoftKeyboard: Called");
        GISmobile.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}