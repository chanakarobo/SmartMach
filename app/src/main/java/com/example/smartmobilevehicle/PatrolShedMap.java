package com.example.smartmobilevehicle;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class PatrolShedMap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double myLat=7.2734513,myLon=80.6192817;
    String type;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patrol_shed_map);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


       Spinner spinner=findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                     mMap.clear();
                    type= (String) parent.getItemAtPosition(position);
                    filteringMethord(type);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;





    }

    public void filteringMethord(String type){

        String typeFuel="Fuel stations";
        String typeTire="Tire centers";

      if(type.equals(typeFuel)){


          LatLng sydney1 = new LatLng(myLat, myLon);
          mMap.addMarker(new MarkerOptions().position(sydney1).title("Havelock tyre center"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney1));

          LatLng sydney2 = new LatLng(myLat+0.01, myLon+0.1);
          mMap.addMarker(new MarkerOptions().position(sydney2).title("Gold Line Tours & Tyre Centre"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney2));

          LatLng sydney3 = new LatLng(myLat+1, myLon+0.1);
          mMap.addMarker(new MarkerOptions().position(sydney3).title("Attidiya Tyre Center"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney3));

          LatLng sydney4 = new LatLng(myLat+2, myLon+0.2);
          mMap.addMarker(new MarkerOptions().position(sydney4).title("Purnima Tyre Centrer"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney4));

          LatLng sydney5 = new LatLng(myLat+0.3, myLon+0.6);
          mMap.addMarker(new MarkerOptions().position(sydney5).title("Sandagiri Tyre Center"));
          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat+0.3,myLon+0.6),8));


      } else if(type.equals(typeTire)){

          LatLng sydney6 = new LatLng(myLat+0.2, myLon+0.6);
          mMap.addMarker(new MarkerOptions().position(sydney6).title("S N Jayasinghe IOC Lanka Fuel Filling Station"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney6));

          LatLng sydney7 = new LatLng(myLat+0.05, myLon+0.7);
          mMap.addMarker(new MarkerOptions().position(sydney7).title("CEYPETCO"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney7));

          LatLng sydney8 = new LatLng(myLat+0.6, myLon+0.4);
          mMap.addMarker(new MarkerOptions().position(sydney8).title("Niroshana Enterprises Fuel Station"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney8));

          LatLng sydney9 = new LatLng(myLat+0.8, myLon+0.6);
          mMap.addMarker(new MarkerOptions().position(sydney9).title("Lanka IOC Fuel Filling Station"));
          mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney9));

          LatLng sydney10 = new LatLng(myLat+0.4, myLon+0.2);
          mMap.addMarker(new MarkerOptions().position(sydney10).title("LAUGFS Fuel Filling Station"));
          mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat+0.3,myLon+0.6),8));


      }

    }


}
