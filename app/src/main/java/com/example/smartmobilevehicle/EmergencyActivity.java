package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EmergencyActivity extends AppCompatActivity {

    DatabaseReference userDetailsDB,locationDB;
    String uid;
    String mobileNumber;
    String fullName;
    String text;
    double userLat,userLon;
    double myLat,myLon;
    private static final int PERMISSION_REQUEST_CODE = 1;
    int PERMISSION_ID = 44;
    Location mLastKnownLocation;
    FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        final TextView textView=findViewById(R.id.mobile_text);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MyApplication application=new MyApplication();
        uid=application.getUid();

        Button sendButton=findViewById(R.id.send_message);

        userDetailsDB= FirebaseDatabase.getInstance().getReference("userDetails").child(uid);
        locationDB= FirebaseDatabase.getInstance().getReference("City").child(uid);


        userDetailsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();


                mobileNumber=(String)value.get("emergencyContact");
                fullName=(String)value.get("fullName");

                textView.setText(mobileNumber);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

         getLastLocation();

            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });


    }

    public void sendMessage(){

        if (ContextCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[] { Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_CODE);
        }else {

            text = "Hi my name is " + fullName + " i am in an emergency please can you help me please";

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);

            String uri = "http://maps.google.com/maps?saddr=" + myLat + "," + myLon;
            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(Uri.parse(uri));
            smsManager.sendTextMessage("94" + mobileNumber, null, text + "\n" + smsBody.toString(), pi, null);

            Toast.makeText(EmergencyActivity.this, "message sent successfully", Toast.LENGTH_SHORT).show();

            userDetailsDB.child("suvasariyaStatus").setValue(1);
            locationDB.setValue(new LocationDetails(uid,myLat,myLon));
        }
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

                            Toast.makeText(EmergencyActivity.this, String.valueOf(myLat), Toast.LENGTH_LONG).show();

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
            getLastLocation();
            Toast.makeText(EmergencyActivity.this, "please enable this", Toast.LENGTH_SHORT).show();
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
