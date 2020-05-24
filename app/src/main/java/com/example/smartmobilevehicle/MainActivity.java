package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    UserRegister userRegister;
    NavigationView navigationView;
    View hview;
    DatabaseReference userDetailsDB;
    String useruid;
    String username,userEmail,imageUrl;
    TextView fullName_text;
    TextView email_text;
    ImageView imageView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userRegister=new UserRegister();
        MyApplication myApplication=new MyApplication();



        useruid=myApplication.getUid();

        userDetailsDB=FirebaseDatabase.getInstance().getReference("userDetails").child(useruid);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,
                drawer,
                toolbar,
                R.string.nav_open_drawer,
                R.string.nav_close_drawer);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        hview = navigationView.inflateHeaderView(R.layout.nav_header);

        fullName_text=hview.findViewById(R.id.username);
        email_text=hview.findViewById(R.id.email);
        imageView=hview.findViewById(R.id.profile_image);

        email_text.setText(myApplication.getUserEmail());
        userEmail=myApplication.getUserEmail();
        userDetailsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap<String,Object> value=(HashMap<String, Object>)dataSnapshot.getValue();

                fullName_text.setText((String)value.get("fullName"));
                Glide.with(MainActivity.this).load(value.get("imageURL")).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // dashbord functioning

        RecyclerView dashrecycler=findViewById(R.id.dashrecycler);

        String[] dashnames=new String[DashBaodImages.dashimages.length];

        for(int i=0;i<dashnames.length;i++){
            dashnames[i]=DashBaodImages.dashimages[i].getName();
        }

        int[] dashImages=new int[DashBaodImages.dashimages.length];

        for(int i=0;i<dashImages.length;i++){
            dashImages[i]=DashBaodImages.dashimages[i].getImageResourceId();
        }

        imageAdapter adapter=new imageAdapter(dashnames,dashImages);
        dashrecycler.setAdapter(adapter);
        GridLayoutManager layoutManager=new GridLayoutManager(this,2);
        dashrecycler.setLayoutManager(layoutManager);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        Intent intent = null;

        switch(id){

            case R.id.nav_sent:
                // hear the activity you want to start
                FirebaseAuth.getInstance().signOut();
                intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_feedback:
//                intent=new Intent(MainActivity.this,LoginActivity.class);
//                intent.putExtra(LoginActivity.EXTRA_DRINKID,userEmail);
//                startActivity(intent);
                break;


        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }


    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }
}
