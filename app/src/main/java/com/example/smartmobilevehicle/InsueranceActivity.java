package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InsueranceActivity extends AppCompatActivity {
  String uid;
  int status=1;
  DatabaseReference sendInsuerence,locationDB;
  int PERMISSION_ID = 44;
  double myLat,myLon;
  Location mLastKnownLocation;
  FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insuerance);

        MyApplication application=new MyApplication();
        uid=application.getUid();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        sendInsuerence = FirebaseDatabase.getInstance().getReference("userDetails").child(uid);
        locationDB = FirebaseDatabase.getInstance().getReference("City").child(uid);

        getLastLocation();

        Button btnSend=findViewById(R.id.reg_btn);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(myLat!=0.00 && myLon !=0.00 && uid !=null){
                    locationDB.setValue(new LocationDetails(uid,myLat,myLon)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sendInsuerence.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(InsueranceActivity.this, "data added successfully", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(InsueranceActivity.this, "data didnt added", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }else {
                    recreate();
                }
            }
        });
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation(){

        if (checkpermission()) {

            if(isLocationEnabled()){

                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        mLastKnownLocation=task.getResult();

                        if(mLastKnownLocation==null){
                            requestNewLocationData();

                        }else {
                            // set location on map
                            mLastKnownLocation = (Location) task.getResult();
                            myLat=mLastKnownLocation.getLatitude();
                            myLon=mLastKnownLocation.getLongitude();

                            Toast.makeText(InsueranceActivity.this, String.valueOf(myLat), Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }

        }else {
            requestPermissions();
        }
    }

    private boolean checkpermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            }
        }else {
            Toast.makeText(InsueranceActivity.this, "please enable this", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
//        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            mLastKnownLocation = locationResult.getLastLocation();

        }
    };



}
