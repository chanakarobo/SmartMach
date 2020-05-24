package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static com.example.smartmobilevehicle.NotificationApplication.CHANNEL_1_ID;

public class MapReciveActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DatabaseReference userDetailsDB,checkacceptDB,machanicDB,getCustomerTypes;
    String uid;
    String cityname="Kandy";
    String custType="LightRigit";
    String custName,custMobile,custEmail="";
    Uri custURL;
    String selectedUid;
    ArrayList<SelectedRequest> selctedList = new ArrayList<>();
    int PERMISSION_ID = 44;
    double myLat,myLon;
    boolean request,pressrequest;
    double userLat,userLon;
    FusedLocationProviderClient mFusedLocationClient;
    private static final String TAG = "GoogleActivity";
    Location mLastKnownLocation;
    ListView listView;
    ArrayList<String>custTypeList=new ArrayList<>();
    AlertDialog alertDialogView;
    private BottomSheetBehavior bottomSheetBehavior;
    Spinner spinner;
    private final Handler mSendSSLMessageHandler = new Handler();
    View mapView;
    private NotificationManagerCompat notificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_recive);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MyApplication application=new MyApplication();
        uid=application.getUid();

        View bottomsheet=findViewById(R.id.bottom_sheet);

        bottomSheetBehavior=BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        notificationManager=NotificationManagerCompat.from(this);

        mSendSSLMessageHandler.post(runnable);

        checkacceptDB=FirebaseDatabase.getInstance().getReference("City");
        machanicDB=FirebaseDatabase.getInstance().getReference("MachanicLocation");
        getCustomerTypes=FirebaseDatabase.getInstance().getReference("custormetType").child(uid);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        spinner=findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            int i=0;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                    custType= (String) parent.getItemAtPosition(position);
                    getRequest(custType);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        listView=findViewById(R.id.list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                TextView textUid = view.findViewById(R.id.uid);
                selectedUid = textUid.getText().toString();

                getLocationOFSelctedUser(selectedUid);


            }
        });

        FloatingActionButton userDetailButton=findViewById(R.id.user_detail_btn);
        userDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetails();
            }
        });

        if(uid !=null){
            getCustomerTypes();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(50, 0, 0, 400);
        }

        // Add a marker in Sydney and move the camera
        getLastLocation();
        locationForNotification();


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
                            mMap.setMyLocationEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }else {
                            // set location on map
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mLastKnownLocation = (Location) task.getResult();
                            myLat=mLastKnownLocation.getLatitude();
                            myLon=mLastKnownLocation.getLongitude();

                            Log.e("latitude ", String.valueOf(myLat));
                            Log.e("longtitude ", String.valueOf(myLon));

//                            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
//                            List<Address> addresses= null;
//                            try {
//                                addresses = geocoder.getFromLocation(myLat,myLon,1);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                            cityname=addresses.get(0).getLocality();
//                            Log.e("city is  ",cityname);
//                            checkNotification(cityname);
                        }
                    }
                });

            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }

        }else {
            requestPermissions();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }


    @SuppressLint("MissingPermission")
    private void locationForNotification(){

        if (checkpermission()) {

            if(isLocationEnabled()){
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        mLastKnownLocation=task.getResult();

                        if(mLastKnownLocation==null){
                            requestNewLocationData();
                            mMap.setMyLocationEnabled(false);
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }else {
                            // set location on map
                            mMap.setMyLocationEnabled(true);
                            mMap.getUiSettings().setMyLocationButtonEnabled(true);
                            mLastKnownLocation = (Location) task.getResult();
                            myLat=mLastKnownLocation.getLatitude();
                            myLon=mLastKnownLocation.getLongitude();

                            Log.e("latitude ", String.valueOf(myLat));
                            Log.e("longtitude ", String.valueOf(myLon));

                            Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> addresses= null;
                            try {
                                addresses = geocoder.getFromLocation(myLat,myLon,1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            cityname=addresses.get(0).getLocality();
//                            Log.e("city is  ",cityname);
                            checkNotification(cityname);
                        }
                    }
                });

            }else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            }

        }else {
            requestPermissions();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
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
            Toast.makeText(MapReciveActivity.this, "please enable this", Toast.LENGTH_SHORT).show();
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

    public void getRequest(final String custtype){

        checkacceptDB.addValueEventListener(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

             for(DataSnapshot snapshot :dataSnapshot.getChildren()){
                 HashMap<String,Object> value=(HashMap<String, Object>)snapshot.getValue();

                  String cityValue=value.get("city").toString();
                  String custValue=value.get("cusType").toString();

                 if(cityValue.equals(cityname) && custValue.equals(custtype) && (boolean)value.get("request")){
                     selctedList.clear();
                     Toast.makeText(MapReciveActivity.this,"object found", Toast.LENGTH_SHORT).show();
                     selctedList.add(new SelectedRequest(value.get("userEmail").toString(),value.get("uid").toString()));
                     MyListAdapter adapter=new MyListAdapter(MapReciveActivity.this,selctedList);
                     listView=findViewById(R.id.list);
                     listView.setAdapter(adapter);

                 }else {
                     Toast.makeText(MapReciveActivity.this,"object not found"+cityname, Toast.LENGTH_SHORT).show();
                 }

             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {

         }
     });


    }

    public class MyListAdapter extends ArrayAdapter<SelectedRequest>{


        private final Activity context;
        private final List requestList;

        public MyListAdapter(Activity context, List requestList) {
            super(context,R.layout.request_list,requestList);

            this.context=context;
            this.requestList=requestList;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            SelectedRequest selectedRequest=this.getItem(position);

            LayoutInflater inflater=context.getLayoutInflater();
            View rowView=inflater.inflate(R.layout.request_list,null,true);

            TextView textUsername=rowView.findViewById(R.id.email);
            TextView textUid=rowView.findViewById(R.id.uid);

            textUsername.setText(selectedRequest.getUserName());
            textUid.setText(selectedRequest.getUserUid());


            return rowView;
        }
    }

    public void getLocationOFSelctedUser(String uid) {

        request = false;

        checkacceptDB = FirebaseDatabase.getInstance().getReference("City").child(uid);
        userDetailsDB=FirebaseDatabase.getInstance().getReference("userDetails").child(uid);

        checkacceptDB.child("request").setValue(request);
        checkacceptDB.child("notification").setValue(request);


        checkacceptDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {

                    HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

                    if (!(boolean) value.get("cancelRequest")) {

                        userLat = (double) value.get("latitude");
                        userLon = (double) value.get("longtitude");

                        mMap.clear();
                        LatLng userLocation = new LatLng(userLat, userLon);
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("user marker"));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(userLat, userLon), 300));
                        saveMachanicLocation();

                    } else {
                        mMap.clear();
                        Toast.makeText(MapReciveActivity.this, "request was canceled", Toast.LENGTH_SHORT).show();
                    }
                }catch(NullPointerException e){
                    Toast.makeText(MapReciveActivity.this, "wait for sec", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        userDetailsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

                custName=(String)value.get("fullName");
                custMobile=(String)value.get("mobile");
//                custEmail=(String)value.get("email");
                custURL=Uri.parse((String)value.get("imageURL"));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveMachanicLocation(){


        RequestDetail requestDetail=new RequestDetail(myLat,myLon,uid,false);

        machanicDB.child(selectedUid).setValue(requestDetail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    pressrequest=true;
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    Toast.makeText(MapReciveActivity.this, "data added successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MapReciveActivity.this, "data didin't added", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void showUserDetails(){

        LayoutInflater li=LayoutInflater.from(MapReciveActivity.this);
        View view=li.inflate(R.layout.user_detail_view,null);

        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MapReciveActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        alertDialogBuilder.setView(view);

        TextView textName=view.findViewById(R.id.name_text);
        TextView textMobile=view.findViewById(R.id.mobile);
        TextView textEmail=view.findViewById(R.id.email);
        ImageView imageView=view.findViewById(R.id.user_image);
        Button cancelButton=view.findViewById(R.id.btn_cancel);
        Button finishButton=view.findViewById(R.id.btn_finish);

        textName.setText(custName);
        textMobile.setText(custMobile);
        textEmail.setText(custEmail);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkacceptDB.child("cancelRequest").setValue(true);
                checkacceptDB.child("request").setValue(true);
                alertDialogView.dismiss();
                recreate();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pressrequest=false;
                mSendSSLMessageHandler.removeCallbacks(runnable);
                machanicDB.child(selectedUid).child("finishStatus").setValue(true);
                alertDialogView.dismiss();
                recreate();
            }
        });

        Glide.with(this).load(custURL).into(imageView);


        alertDialogView=alertDialogBuilder.create();
        alertDialogView.show();
        alertDialogView.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    private final Runnable runnable=new Runnable() {

        @Override
        public void run() {

            try{
                if(pressrequest) {
                    requestNewLocationData();
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.execute();
                }else{
                    MyAsyncTask myAsyncTask = new MyAsyncTask();
                    myAsyncTask.wait();
                }
            }catch(Exception e){

            }
            mSendSSLMessageHandler.postDelayed(runnable,5000);

        }
    };

    class MyAsyncTask extends AsyncTask<Void,Void,String> {

        @Override
        protected void onPreExecute() {
            getLastLocation();
        }

        @Override
        protected String doInBackground(Void... voids) {
            updateUserLocation(myLat,myLon);
            return null;
        }

    }

    public void updateUserLocation(double mylat,double myLon){

        machanicDB.child(selectedUid).child("latitude").setValue(mylat);
        machanicDB.child(selectedUid).child("logtitude").setValue(myLon);


    }

    public void getCustomerTypes(){

        getCustomerTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                    HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();

                    custTypeList.add((String) value.get("custermerType"));
                }

                ArrayAdapter adapter=new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,custTypeList);
                spinner.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void checkNotification(final String city){

        checkacceptDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot :dataSnapshot.getChildren()) {
                    try {

                        HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();

                        String cityValue = value.get("city").toString();
                        boolean notify = (boolean) value.get("notification");

                        if (notify && cityValue.equals(city)) {

                            Notification notification = new NotificationCompat.Builder(MapReciveActivity.this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_launcher_logo)
                                    .setContentTitle("Request")
                                    .setContentText("You have new request from client !")
                                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                                    .setVibrate(new long[]{0, 1000})
                                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                    .build();

                            notificationManager.notify(1, notification);

                            Log.e("notification found", "this");
                        } else {
                            Log.e("notification not found", "this");
                        }
                    }catch (NullPointerException e){
                        Toast.makeText(MapReciveActivity.this, "wait for sec", Toast.LENGTH_SHORT).show();
                    }
                    }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
