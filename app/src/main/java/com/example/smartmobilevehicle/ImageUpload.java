package com.example.smartmobilevehicle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class ImageUpload extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST=1;

    Button uploadButton;
    ImageView imageView;
    ProgressBar progressBar;
    Uri mImageUri;
    StorageReference mStorageReference;
    DatabaseReference mDatabaseReference;
    StorageTask mStorageTask;
    String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        MyApplication application=new MyApplication();
        uid=application.getUid();

        Log.e("uid is ",uid);

        mStorageReference= FirebaseStorage.getInstance().getReference("userDetails");
        mDatabaseReference= FirebaseDatabase.getInstance().getReference("userDetails").child(uid);


        uploadButton=findViewById(R.id.btn_upload);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progress_bar);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mStorageTask != null && mStorageTask.isInProgress()){
                    Toast.makeText(ImageUpload.this, "upload in progress", Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile();
                }

            }
        });
    }


    public void openFileChooser(){

        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data != null && data.getData() != null){

            mImageUri=data.getData();
            Glide.with(this).load(mImageUri).into(imageView);
        }

    }

    public String getFileExtention(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));

    }

    public void uploadFile(){

      if(mImageUri !=null){

       final StorageReference fileReference=mStorageReference.child(uid + "." + getFileExtention(mImageUri));

          imageView.setDrawingCacheEnabled(true);
          imageView.buildDrawingCache();
          Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
          byte[] data = baos.toByteArray();

          UploadTask uploadTask = fileReference.putBytes(data);

          uploadTask.addOnFailureListener(new OnFailureListener() {
              @Override
              public void onFailure(@NonNull Exception exception) {
                  Toast.makeText(ImageUpload.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
              }
          }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                  Toast.makeText(ImageUpload.this, "image upload sceessfull", Toast.LENGTH_SHORT).show();

              }
          }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
              @Override
              public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                  double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                  progressBar.setProgress((int)progress);
              }
          });

          uploadTask=fileReference.putFile(mImageUri);
          mStorageTask=fileReference.putFile(mImageUri);

          Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
              @Override
              public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                  if (!task.isSuccessful()) {
                      throw task.getException();
                  }

                  // Continue with the task to get the download URL
                  return fileReference.getDownloadUrl();
              }
          }).addOnCompleteListener(new OnCompleteListener<Uri>() {
              @Override
              public void onComplete(@NonNull Task<Uri> task) {
                  if (task.isSuccessful()) {
                      Uri downloadUri = task.getResult();
                      mDatabaseReference.child("imageURL").setValue(downloadUri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {
                              if(task.isSuccessful()){
                                  Intent intent=new Intent(ImageUpload.this,LoginActivity.class);
                                  startActivity(intent);
                              }
                          }
                      });

                  } else {
                      Toast.makeText(ImageUpload.this, "fail to set download link", Toast.LENGTH_SHORT).show();
                  }
              }
          });

      }else{

      }

    }
}
