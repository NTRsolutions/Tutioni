package com.example.khokan.tutorisbdservice;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private String receiverUserID, senderUserID, currentState;

    private TextView userProfileName,userProfilePhone, userProfileEmail, userProfileAddress;
    private TextView userProfileStatus;
    private TextView userProfession,userPrivateTurosOrNot,userGender;
    private Button sendMessageRequestButton;
    private Button declineMessageRequestButton;
    private CircleImageView userProfileImage;
    private ImageView coverImage;
    private DatabaseReference userRef, chatRequestRef, contactsRef, notificationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        notificationRef = FirebaseDatabase.getInstance().getReference().child("Notifications");
        userRef.keepSynced(true);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        chatRequestRef.keepSynced(true);
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        contactsRef.keepSynced(true);

        receiverUserID = getIntent().getExtras().get("visit_user_id").toString();
        senderUserID = mAuth.getCurrentUser().getUid();

        userProfileImage = findViewById(R.id.visit_profile_image);
        userProfileName = findViewById(R.id.visit_user_name);
        userProfileStatus = findViewById(R.id.visit_user_status);
        userProfession = findViewById(R.id.visit_user_profession);
        userProfileEmail = findViewById(R.id.visit_user_email);
        userProfilePhone = findViewById(R.id.visit_user_phone);
        userProfileAddress = findViewById(R.id.visit_user_address);

        userPrivateTurosOrNot = findViewById(R.id.visit_user_private_tutors);
        userGender = findViewById(R.id.visit_user_gender);
        Button userLocation = findViewById(R.id.user_location);

        userLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locattionIntent = new Intent(ProfileActivity.this, GoogleMapsActivity.class);
                Toast.makeText(ProfileActivity.this, "Coming Soon...........!", Toast.LENGTH_LONG).show();
                startActivity(locattionIntent);
            }
        });


        sendMessageRequestButton = findViewById(R.id.send_message_request_button);
        declineMessageRequestButton = findViewById(R.id.decine_message_request_button);
        currentState = "new";

        retrieveUserInfo();
    }

    private void retrieveUserInfo() {
        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String email=" ";
                String phone=" ";
                String address=" ";

                if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("image")))
                {
                    final String userImage = dataSnapshot.child("image").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String profession = dataSnapshot.child("profession").getValue().toString();
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String privateTutor = dataSnapshot.child("private_tutors").getValue().toString();
                     email = dataSnapshot.child("user_email").getValue().toString();
                     phone = dataSnapshot.child("user_phone").getValue().toString();
                     address = dataSnapshot.child("address").getValue().toString();

//                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                    Picasso.get().load(userImage).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_image)
                            .into(userProfileImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(userProfileImage);
                                }
                            });

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    userProfession.setText("Profession: "+profession);
                    userGender.setText("Gender :"+gender);
                    userPrivateTurosOrNot.setText("Private Tutor :"+ privateTutor);
                    userProfileEmail.setText(email);
                    userProfilePhone.setText(phone);
                    userProfileAddress.setText(address);

                    manageChatRequest();
                }
                else
                {
                    String gender = dataSnapshot.child("gender").getValue().toString();
                    String privateTutor = dataSnapshot.child("private_tutors").getValue().toString();
                    String userName = dataSnapshot.child("name").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();
                    String profession = dataSnapshot.child("profession").getValue().toString();
                    email = dataSnapshot.child("user_email").getValue().toString();
                    phone = dataSnapshot.child("user_phone").getValue().toString();
                    address = dataSnapshot.child("address").getValue().toString();



                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    userProfession.setText("Profession: "+profession);
                    userGender.setText("Gender :"+gender);
                    userPrivateTurosOrNot.setText("Private Tutor :"+ privateTutor);
                    userProfileEmail.setText(email);
                    userProfilePhone.setText(phone);
                    userProfileAddress.setText(address);
                    manageChatRequest();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void manageChatRequest() {

        chatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserID))
                        {
                            String request_type = dataSnapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                currentState = "request_sent";
                                sendMessageRequestButton.setText("Cancel Chat Request");
                            }
                            else if(request_type.equals("received"))
                            {

                                currentState = "request_received";
                                sendMessageRequestButton.setText("Accept Chat Request");

                                declineMessageRequestButton.setVisibility(View.VISIBLE);
                                declineMessageRequestButton.setEnabled(true);
                                declineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                        {
                            contactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserID))
                                    {
                                        currentState = "friends";
                                        sendMessageRequestButton.setText("Remove This Contact");
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        if (!senderUserID.equals(receiverUserID))
        {

            sendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMessageRequestButton.setEnabled(false);
                    if (currentState.equals("new"))
                    {
                        sendChatRequest();
                    }
                    if (currentState.equals("request_sent"))
                    {
                        cancelChatRequest();
                    }
                    if (currentState.equals("request_received"))
                    {
                        acceptChatRequest();
                    }
                    if (currentState.equals("friends"))
                    {
                        removeSpecificContact();
                    }
                }
            });

        }else
        {
            sendMessageRequestButton.setVisibility(View.INVISIBLE);
        }


    }

    private void removeSpecificContact() {
        contactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            contactsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                sendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                sendMessageRequestButton.setText("Send Messages");

                                                declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                declineMessageRequestButton.setEnabled(false);
                                            }

                                        }
                                    });
                        }
                    }
                });

    }

    private void acceptChatRequest() {
        contactsRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           contactsRef.child(receiverUserID).child(senderUserID)
                                   .child("Contacts").setValue("Saved")
                                   .addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {
                                           if (task.isSuccessful())
                                           {
                                                chatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    chatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    sendMessageRequestButton.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    sendMessageRequestButton.setText("Remove This Contact");
                                                                                    declineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    declineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                           }
                                       }
                                   });
                       }
                    }
                });
    }

    private void cancelChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                     if (task.isSuccessful())
                     {
                         chatRequestRef.child(receiverUserID).child(senderUserID)
                                 .removeValue()
                                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                                     @Override
                                     public void onComplete(@NonNull Task<Void> task)
                                     {
                                         if (task.isSuccessful())
                                         {
                                             sendMessageRequestButton.setEnabled(true);
                                             currentState = "new";
                                             sendMessageRequestButton.setText("Send Messages");

                                             declineMessageRequestButton.setVisibility(View.GONE);
                                             declineMessageRequestButton.setEnabled(false);
                                         }

                                     }
                                 });
                     }
                    }
                });
    }

    private void sendChatRequest() {

        chatRequestRef.child(senderUserID).child(receiverUserID)
                .child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            chatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {

                                                HashMap<String, String> chatNotificationMap = new HashMap<>();
                                                chatNotificationMap.put("from",senderUserID);
                                                chatNotificationMap.put("type", "request");

                                                notificationRef.child(receiverUserID).push()
                                                        .setValue(chatNotificationMap)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful())
                                                                {
                                                                    sendMessageRequestButton.setEnabled(true);
                                                                    currentState="request_sent";
                                                                    sendMessageRequestButton.setText("Cancel Chat Request");
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth onlineAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = onlineAuth.getCurrentUser();
        onlineRef.child(currentUser.getUid()).child("online").setValue(true);
    }
}
