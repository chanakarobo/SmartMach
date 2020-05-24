package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.lang.Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.logging.LogRecord;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Location location;
    boolean request=true,pressrequest=false,cancelRequest,finishStatus;
    Location mLastKnownLocation;
    double myLat,myLon;
    double macLat,macLon;
    String uid;
    String custType="";
    String cityname="Kandy";
    String userEmail="";
    String machanicRateStatus;
    String machanicUid;
    String custName,custMobile,custEmail="";
    int rateValue;
    View mMapView;
    Uri custURL;
    RadioGroup radioGroup;
    DatabaseReference userDetailsDB,updateRequest,machanicLocation,machanicRates,machanicalDetails;
    private final Handler mSendSSLMessageHandler = new Handler();
    private BottomSheetBehavior bottomSheetBehavior;
    AlertDialog alertDialogView;
    View mapView;
    Polyline polyline=null;
    int i=0;
    int x=0;
    List<LatLng>list=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        MyApplication application=new MyApplication();
        uid=application.getUid();
        userEmail=application.getUserEmail();

        userDetailsDB= FirebaseDatabase.getInstance().getReference("City");
        machanicLocation= FirebaseDatabase.getInstance().getReference("MachanicLocation").child(uid);
        machanicRates=FirebaseDatabase.getInstance().getReference("reviews");
        updateRequest= FirebaseDatabase.getInstance().getReference("City").child(uid);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);

        mSendSSLMessageHandler.post(runnable);

        View bottomsheet=findViewById(R.id.bottom_sheet);

        bottomSheetBehavior=BottomSheetBehavior.from(bottomsheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        radioGroup =  findViewById(R.id.radio_group);


        FloatingActionButton machanicDetailBtn=findViewById(R.id.machanic_detail_btn);

        machanicDetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetails();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (i){


                    case R.id.light_rid:
                        custType="LightRigit";
                        request=true;
                        break;

                    case R.id.medium_rigit:
                        custType="MediumRigid";
                        request=true;
                        break;

                    case R.id.hevy_rigit:
                        custType="HevyRigid";
                        request=true;
                        break;

                    case R.id.havy_combination:
                        custType="HevyCombination";
                        request=true;
                        break;

                    case R.id.vehicle_carrier:
                        custType="Vehicle_Carrier";
                        request=true;
                        break;

                }

            }
        });

        getMachanicLocation();


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
       getLastLocation();

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
                           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                   new LatLng(mLastKnownLocation.getLatitude(),
                                           mLastKnownLocation.getLongitude()), 300));

                           Log.e("latitude ", String.valueOf(myLat));
                           Log.e("longtitude ", String.valueOf(myLon));

                           LatLng mylocation=new LatLng(myLat,myLon);

                           list.add(mylocation);

//                           Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());
//                           List<Address>addresses= null;
//                           try {
//                               addresses = geocoder.getFromLocation(myLat,myLon,1);
//                           } catch (IOException e) {
//                               e.printStackTrace();
//                           }
//                           cityname=addresses.get(0).getLocality();
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
    public void getLocationForUpdate(){

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
                            List<Address>addresses= null;
                            try {
                                addresses = geocoder.getFromLocation(myLat,myLon,1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                             cityname=addresses.get(0).getLocality();
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
            Toast.makeText(MapActivity.this, "please enable this", Toast.LENGTH_SHORT).show();
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

  public void saveLocationDetails(double latitude,double longitude,String custType,boolean request){

      LocationDetails locationDetails=new LocationDetails(uid,latitude,longitude,request,custType,cityname,userEmail,false,true);

      userDetailsDB.child(uid).setValue(locationDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  pressrequest=true;
                  Toast.makeText(MapActivity.this, "data added successfully", Toast.LENGTH_SHORT).show();
              }else{
                  Toast.makeText(MapActivity.this, "data didn't added", Toast.LENGTH_SHORT).show();
              }
          }
      });

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


  class MyAsyncTask extends AsyncTask<Void,Void,String>{


      @Override
      protected void onPreExecute() {
          getLocationForUpdate();
      }

      @Override
      protected String doInBackground(Void... voids) {
//          saveLocationDetails(myLat,myLon);
          updateUserLocation(myLat,myLon);
          return null;
      }

  }

  public void getRequest(View view){

      saveLocationDetails(myLat,myLon,custType,request);

  }


    public void getMachanicLocation(){

          updateRequest.addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                  try {

                      HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

                      if (!(boolean) value.get("request")) {

                          machanicLocation.addValueEventListener(new ValueEventListener() {
                              @Override
                              public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                  try {

                                      HashMap<String, Object> machanicValues = (HashMap<String, Object>) dataSnapshot.getValue();

                                      macLat = (double) machanicValues.get("latitude");
                                      macLon = (double) machanicValues.get("logtitude");
                                      machanicUid = (String) machanicValues.get("uid");
                                      finishStatus = (boolean) machanicValues.get("finishStatus");

                                      getMachanicalDetails(machanicUid);

                                      if (finishStatus) {
                                          mSendSSLMessageHandler.removeCallbacks(runnable);
                                          Toast.makeText(MapActivity.this, "Activity running", Toast.LENGTH_SHORT).show();

                                          if(!MapActivity.this.isFinishing()) {
                                              showRatingAlert();
                                          }
                                      }

                                      Log.e("machanical longitude ", String.valueOf(macLon));

                                      LatLng userLocation = new LatLng(macLat, macLon);
                                      mMap.clear();
                                      mMap.addMarker(new MarkerOptions().position(userLocation).title("user marker"));
                                      mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(macLat, macLon), 200));


                                      list.add(userLocation);



                                     if(i==0){
                                         drawpolyLines();
                                         i++;
                                     }


                                  } catch (NullPointerException ex) {
                                      Toast.makeText(MapActivity.this, "wait for sec", Toast.LENGTH_SHORT).show();
                                  }
                              }

                              @Override
                              public void onCancelled(@NonNull DatabaseError databaseError) {

                              }
                          });


                      } else {
                          Toast.makeText(MapActivity.this, "unable to find location", Toast.LENGTH_SHORT).show();
                          mMap.clear();
                      }
                  }catch(NullPointerException e){
                      Toast.makeText(MapActivity.this, "wait for sec 1", Toast.LENGTH_SHORT).show();
                  }
              }

              @Override
              public void onCancelled(@NonNull DatabaseError databaseError) {

              }
          });



  }

  public void showRatingAlert(){

   AlertDialog alertDialog=new AlertDialog.Builder(MapActivity.this).create();
   alertDialog.setTitle("Rate your Machanic");
   alertDialog.setMessage("If your enjoye this service please rate our machanics");

   alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Good",
           new DialogInterface.OnClickListener() {
               @Override
               public void onClick(DialogInterface dialogInterface, int i) {
                   machanicRateStatus="Good";
                   rateValue=5;
                   setRate(machanicRateStatus,rateValue);
                   dialogInterface.dismiss();
               }
           });

      alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Perfect",
              new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      machanicRateStatus="Perfect";
                      rateValue=10;
                      setRate(machanicRateStatus,rateValue);
                      dialogInterface.dismiss();
                  }
              });

      alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Bad",
              new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                      machanicRateStatus="Bad";
                      rateValue=0;
                      setRate(machanicRateStatus,rateValue);
                      dialogInterface.dismiss();
                  }
              });
   alertDialog.show();
   alertDialog.setCanceledOnTouchOutside(false);

  }

  public void setRate(String machanicRateStatus,int rateValue){

      MachanicRateValue machanicRateValue=new MachanicRateValue(machanicUid,machanicRateStatus,rateValue);
      machanicRates.push().setValue(machanicRateValue).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              if(task.isSuccessful()){
                  updateRequest.removeValue();
                  machanicLocation.removeValue();
                  recreate();
              }
          }
      });


}

public void getMachanicalDetails(String machanicalUid){

    machanicalDetails=FirebaseDatabase.getInstance().getReference("userDetails").child(machanicalUid);

      machanicalDetails.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();

              custName=(String)value.get("fullName");
              custMobile=(String)value.get("mobile");
//              custEmail=(String)value.get("email");
              custURL= Uri.parse((String)value.get("imageURL"));

          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });
}


    public void showUserDetails(){

        LayoutInflater li=LayoutInflater.from(MapActivity.this);
        View view=li.inflate(R.layout.user_detail_view,null);

        final AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MapActivity.this,R.style.Theme_AppCompat_Dialog_Alert);
        alertDialogBuilder.setView(view);

        TextView textName=view.findViewById(R.id.name_text);
        TextView textMobile=view.findViewById(R.id.mobile);
        TextView textEmail=view.findViewById(R.id.email);
        ImageView imageView=view.findViewById(R.id.user_image);
        Button cancelButton=view.findViewById(R.id.btn_cancel);
        Button finishButton=view.findViewById(R.id.btn_finish);

        finishButton.setVisibility(View.GONE);

        textName.setText(custName);
        textMobile.setText(custMobile);
        textEmail.setText(custEmail);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateRequest.child("cancelRequest").setValue(true);
                updateRequest.child("request").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            recreate();
                        }
                    }
                });

            }
        });


        Glide.with(this).load(custURL).into(imageView);

        alertDialogView=alertDialogBuilder.create();
        alertDialogView.show();
        alertDialogView.getWindow().setLayout(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);

    }

    public void updateUserLocation(double myLat, double myLon){

        updateRequest.child("latitude").setValue(myLat);
        updateRequest.child("longtitude").setValue(myLon);

    }

    public void drawpolyLines(){

        if(polyline !=null)polyline.remove();

        PolylineOptions polygonOptions=new PolylineOptions()
                .addAll(list).clickable(true);
        polyline=mMap.addPolyline(polygonOptions);

    }


    @Override
    protected void onPause() {
        super.onPause();
        getLastLocation();
    }
}
