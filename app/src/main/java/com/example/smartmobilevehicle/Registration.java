package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class Registration extends AppCompatActivity {
    public static final String EXTRA_UID = "uid";
    public static final String EXTRA_EMAIL = "user_email";
    String uid;
    DatabaseReference userDetailsDB, custermerTypeDB, insueranceCompany;

    TextInputLayout fullNametext, emailtext, mobiletext, nictext, emegencytext, addresstext, vehicle_numbtext, insuerncetext;
    CheckBox vehicleUser, lightRid, medium, hevy, hevyComb, cerrier;
    String custType;
    String insueranceName, insueranceId,email;
    ArrayList<String> companyList = new ArrayList<>();
    String imageUrl = "";
    int status = 0;
    int suvasariyaStatus = 0;
    SpinnerDialog spinnerDialogemp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        uid = (String) getIntent().getExtras().get(EXTRA_UID);
        email=(String)getIntent().getExtras().get(EXTRA_EMAIL);
        Log.e("user id is ", uid);

        new MyApplication(uid);

        userDetailsDB = FirebaseDatabase.getInstance().getReference("userDetails");
        custermerTypeDB = FirebaseDatabase.getInstance().getReference("custormetType").child(uid);
        insueranceCompany = FirebaseDatabase.getInstance().getReference("InsuaranceCompany");

        fullNametext = findViewById(R.id.fname);
        emailtext = findViewById(R.id.email);
        mobiletext = findViewById(R.id.mobile);
        nictext = findViewById(R.id.nic);
        emegencytext = findViewById(R.id.emergency_numb);
        addresstext = findViewById(R.id.address);
        vehicle_numbtext = findViewById(R.id.vehicle_number);
        vehicleUser = findViewById(R.id.vehicle_user);
        lightRid = findViewById(R.id.light_rid);
        medium = findViewById(R.id.medium);
        hevy = findViewById(R.id.hevy);
        hevyComb = findViewById(R.id.hevy_comb);
        cerrier = findViewById(R.id.carrier);

        emailtext.setVisibility(View.GONE);

        Button btnLogin = findViewById(R.id.reg_btn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adddetails();
            }
        });

        selectInsuerance();

        Button btnInsuerance = findViewById(R.id.btn_insuerance);

        btnInsuerance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerDialogemp.showSpinerDialog();
            }
        });
    }

    private void adddetails() {

        String fullname = fullNametext.getEditText().getText().toString().trim();
        String mobile = mobiletext.getEditText().getText().toString().trim();
        String nic = nictext.getEditText().getText().toString().trim();
        String enmergencyNumb = emegencytext.getEditText().getText().toString().trim();
        String address = addresstext.getEditText().getText().toString().trim();
        String vehicleNumb = vehicle_numbtext.getEditText().getText().toString().trim();

        if (emptyValidation() | validateCheckbox()) {

            UserDetails user = new UserDetails(
                    uid,
                    fullname,
                    email, mobile,
                    nic,
                    enmergencyNumb,
                    address,
                    vehicleNumb,
                    insueranceName,
                    imageUrl,
                    insueranceId,
                    status,
                    suvasariyaStatus);


            userDetailsDB.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Registration.this, ImageUpload.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(Registration.this, "data adding fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    public void getVehicleUserOnly(View view) {

        custType = "vehicla_user_only";

        CustormerType custormerType = new CustormerType(custType, vehicleUser.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void getLightRigit(View view) {

        custType = "LightRigit";

        CustormerType custormerType = new CustormerType(custType, lightRid.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void getMediumRigit(View view) {

        custType = "MediumRigid";

        CustormerType custormerType = new CustormerType(custType, medium.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void getHevyRigid(View view) {

        custType = "HevyRigid";

        CustormerType custormerType = new CustormerType(custType, hevy.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void getHevyComb(View view) {

        custType = "HevyCombination";

        CustormerType custormerType = new CustormerType(custType, hevyComb.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void getVehicleCarrier(View view) {

        custType = "Vehicle_Carrier";

        CustormerType custormerType = new CustormerType(custType, cerrier.isChecked());

        custermerTypeDB.push().setValue(custormerType);

        Toast.makeText(Registration.this, "data added successfully", Toast.LENGTH_SHORT).show();

    }

    public void selectInsuerance() {

        insueranceCompany.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, Object> value = (HashMap<String, Object>) snapshot.getValue();

                    companyList.add((String) value.get("CompName") + "\n" + (String) value.get("UID"));

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        spinnerDialogemp = new SpinnerDialog(this, companyList, "Slect company ", "Close");
        spinnerDialogemp = new SpinnerDialog(this, companyList, "Slect company", R.style.DialogAnimations_SmileWindow, "Close");

        spinnerDialogemp.setCancellable(true);
        spinnerDialogemp.setCancellable(false);

        spinnerDialogemp.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                String parts[] = item.split("\n", 2);
                insueranceName = parts[0];
                insueranceId = parts[1];

                Button btnInsuerance = findViewById(R.id.btn_insuerance);
                btnInsuerance.setText(insueranceName);
            }
        });

    }


    public boolean emptyValidation() {

        String fullname = fullNametext.getEditText().getText().toString().trim();
        String mobile = mobiletext.getEditText().getText().toString().trim();
        String nic = nictext.getEditText().getText().toString().trim();
        String enmergencyNumb = emegencytext.getEditText().getText().toString().trim();
        String address = addresstext.getEditText().getText().toString().trim();
        String vehicleNumb = vehicle_numbtext.getEditText().getText().toString().trim();

        if (fullname.isEmpty()) {
            fullNametext.setError("this field can't be empty");
            return false;
        } else if (mobile.isEmpty()) {
            mobiletext.setError("this field can't be empty");
            return false;

        } else if (nic.isEmpty()) {
            nictext.setError("this field can't be empty");
            return false;

        } else if (enmergencyNumb.isEmpty()) {
            emegencytext.setError("this field can't be empty");
            return false;

        } else if (address.isEmpty()) {
            addresstext.setError("this field can't be empty");
            return false;

        } else if (vehicleNumb.isEmpty()) {
            vehicle_numbtext.setError("this field can't be empty");
            return false;

        } else {

            fullNametext.setError(null);
            mobiletext.setError(null);
            nictext.setError(null);
            emegencytext.setError(null);
            addresstext.setError(null);
            vehicle_numbtext.setError(null);

            return true;
        }


    }

    public boolean validateCheckbox(){

        if(vehicleUser.isChecked() || lightRid.isChecked() || lightRid.isChecked() || medium.isChecked() || hevy.isChecked() || hevyComb.isChecked() || cerrier.isChecked()){
            return true;
        }
        else {
            Toast.makeText(Registration.this, "please check atleast one checkbox", Toast.LENGTH_SHORT).show();
            return false;
        }

    }


}