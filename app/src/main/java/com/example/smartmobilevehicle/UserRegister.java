package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserRegister extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    TextInputLayout textusername;
    TextInputLayout textConpassword;
    TextInputLayout textpassword;
    String username,password,password2,uid,email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        mAuth = FirebaseAuth.getInstance();

        Button button=findViewById(R.id.signin_btn);

        textusername = findViewById(R.id.username);
        textConpassword = findViewById(R.id.con_password);
        textpassword=findViewById(R.id.password);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


              username= textusername.getEditText().getText().toString().trim();
              password= textConpassword.getEditText().getText().toString().trim();
              password2= textpassword.getEditText().getText().toString().trim();

             if(password.equals(password2)) {
                 if (validation()) {
                     signIn(username, password);
                 }
             }else {
                 Toast.makeText(UserRegister.this, "password didn't mach",
                         Toast.LENGTH_SHORT).show();
             }
            }

        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void signIn(String email,String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(UserRegister.this, "Sign In success.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(UserRegister.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });



    }

    private void updateUI(FirebaseUser user){

        if (user != null) {

            // get user details from that

            uid=user.getUid();
            email=user.getEmail();
            Intent intent=new Intent(this,Registration.class);
            intent.putExtra(Registration.EXTRA_UID,uid);
            intent.putExtra(Registration.EXTRA_EMAIL,email);
            startActivity(intent);


        }else{
            return;
        }

    }

    public void signOut() {
        FirebaseAuth.getInstance().signOut();
    }

    public boolean validation(){

        if(username.isEmpty()){
            textusername.setError("feild is empty");
            return false;

        }else if(!Patterns.EMAIL_ADDRESS.matcher(username).matches()){
            textusername.setError("please enter a valid email address");
            return false;
        }else if(password.isEmpty()){
            textConpassword.setError("feild is empty");
            return false;
        }else if(password2.isEmpty()){
            textpassword.setError("feild is empty");
            return false;
        } else{
            textusername.setError(null);
            textusername.setError(null);
            return true;
        }


    }






}
