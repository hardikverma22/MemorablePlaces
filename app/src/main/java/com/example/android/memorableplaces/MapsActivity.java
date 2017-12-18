package com.example.android.memorableplaces;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.icu.text.Normalizer2;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;

    public void centerMapOnLocation(Location location,String title){

        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.clear();
        if(title=="Your Location"){
            mMap.addMarker(new MarkerOptions().position(current).title(title).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        mMap.addMarker(new MarkerOptions().position(current).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current,17));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation,"Your Location");
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        mMap.setOnMapLongClickListener(this);

        Intent intent = getIntent();
        //Toast.makeText(getApplication(),intent.getStringExtra("placeNumber"),Toast.LENGTH_SHORT).show();

if(intent.getIntExtra(("placeNumber"),0)==0)
{
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            centerMapOnLocation(location,"Your Location");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    if (Build.VERSION.SDK_INT < 23) {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    else
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation !=null){

            }
            centerMapOnLocation(lastKnownLocation,"Your Location");


        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
}else
{
    mMap.clear();

    Location userLocation = new Location(LocationManager.GPS_PROVIDER);
    if(userLocation!=null){
        userLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra(("placeNumber"),0)).latitude);
        userLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra(("placeNumber"),0)).longitude);
        centerMapOnLocation(userLocation,MainActivity.places.get(intent.getIntExtra(("placeNumber"),0)));
        // mMap.addMarker(new MarkerOptions().position(MainActivity.locations.get(intent.getIntExtra(("placeNumber"),0))).title(MainActivity.places.get(intent.getIntExtra(("placeNumber"),0))));
    }

}


}

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listaddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);

            if(listaddress!=null && listaddress.size()>0)
            {
                if(listaddress.get(0).getThoroughfare()!=null)
                {
                    if(listaddress.get(0).getSubThoroughfare()!=null)
                    {
                        address+=listaddress.get(0).getSubThoroughfare()+" ,";
                    }
                    address+=listaddress.get(0).getThoroughfare();
                }

            }
            Log.i("full location",listaddress.get(0).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        if(address==""){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd MMM YYYY");
            String currentDateandTime = sdf.format(new Date());
            address=currentDateandTime;
        }
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        Toast.makeText(getApplicationContext(),"Location Saved!",Toast.LENGTH_SHORT).show();
        MainActivity.places.add(address);
        MainActivity.locations.add(latLng);


        SharedPreferences sharedPreferences= this.getSharedPreferences("com.example.android.memorableplaces", MODE_PRIVATE);

        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longitude= new ArrayList<>();

        for(LatLng coordinates :MainActivity.locations){
            latitude.add(Double.toString(coordinates.latitude));
            longitude.add(Double.toString(coordinates.longitude));
        }
        try {
            sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.places)).apply();
            sharedPreferences.edit().putString("latitude", ObjectSerializer.serialize(latitude)).apply();
            sharedPreferences.edit().putString("longitude", ObjectSerializer.serialize(longitude)).apply();

        } catch (IOException e) {
            e.printStackTrace();
        }

        MainActivity.listAdapter.notifyDataSetChanged();

    MainActivity.listView.setAdapter(MainActivity.listAdapter);


    }
}
