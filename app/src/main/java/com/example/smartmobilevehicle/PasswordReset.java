package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordReset extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        mAuth = FirebaseAuth.getInstance();
        Button sendbtn=findViewById(R.id.btn_reset);
        final EditText emai_text=findViewById(R.id.email_text);


        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=emai_text.getText().toString().trim();

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(PasswordReset.this, "email is empty", Toast.LENGTH_SHORT).show();

                }else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Toast.makeText(PasswordReset.this, "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(PasswordReset.this,LoginActivity.class);
                                startActivity(intent);
                            } else {
                                String error = task.getException().getMessage();
                                Log.e("firbase error is ", error);
                            }

                        }
                    });

                }
            }
        });


    }
}
