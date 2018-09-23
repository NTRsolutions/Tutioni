package com.example.khokan.tutorisbdservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView setting_userName,setting_status;
    private Button update_button;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentUserId;
    private static final int GALLERY_PICK=1;
    private StorageReference userProfileImagesRef;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initializeFields();

//      firebase

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });

        retriveUserInfo();

//        profile Image change
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_PICK);
            }
        });

    }

    private void initializeFields() {
        profileImage  = findViewById(R.id.profile_image);
        setting_userName = findViewById(R.id.setting_userName);
        setting_status = findViewById(R.id.setting_userStatus);
        update_button = findViewById(R.id.user_setting_button);
        loadingBar = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK && data!=null)
        {
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK)
            {
                loadingBar.setTitle("Set Profile Image ");
                loadingBar.setMessage("Please wait, Your profile Image is updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                Uri resultUri = result.getUri();

                StorageReference filePath = userProfileImagesRef.child(currentUserId +".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(SettingActivity.this, "Profile Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();

                            rootRef.child("Users").child(currentUserId).child("image")
                                    .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(SettingActivity.this, "Image save in Database , Successfully!", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                    else
                                    {
                                        String error = task.getException().toString();
                                        Toast.makeText(SettingActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });
                        }
                        else
                        {
                            String error = task.getException().toString();
                            Toast.makeText(SettingActivity.this, "Error: "+error, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });

            }
        }
    }

    private void retriveUserInfo() {

        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()&& dataSnapshot.hasChild("name")&& dataSnapshot.hasChild("image"))
                {
                    //FireBase

                            String name = dataSnapshot.child("name").getValue().toString();
                            String image = dataSnapshot.child("image").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                    setting_userName.setText(name);
                    setting_status.setText(status);
                    Picasso.get().load(image).into(profileImage);
                        }
                else if (dataSnapshot.exists()&& dataSnapshot.hasChild("name"))
                {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();

                    setting_userName.setText(name);
                    setting_status.setText(status);

                }else
                    {
                        Toast.makeText(SettingActivity.this, "Please set and Update your profile!", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void updateProfile() {
        String user_name = setting_userName.getText().toString();
        String user_status = setting_status.getText().toString();

        if (TextUtils.isEmpty(user_name))
        {
            Toast.makeText(this, "Please input User Name...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(user_status))
        {
            Toast.makeText(this, "Plese insert status...", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, String> profileUpdateMap = new HashMap<>();
            profileUpdateMap.put("uid", currentUserId);
            profileUpdateMap.put("name", user_name);
            profileUpdateMap.put("status", user_status);

            rootRef.child("Users").child(currentUserId).setValue(profileUpdateMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Upadate successfully changed!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
