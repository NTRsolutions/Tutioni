package com.example.khokan.tutorisbdservice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupsChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton sendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayMessage;
    private String currentGroupName,currentUserID,currentUserName,currentDate,currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, groupNameRef,groupMessageRefKey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups_chat);


        currentGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(GroupsChatActivity.this, currentGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.keepSynced(true);
        groupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);
        userRef.keepSynced(true);


        
        
        initializeFirelds();
        getUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageInfoToDataBase();
                userMessageInput.setText("");
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
        
    }

//    onstart method


    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseAuth onlineAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = onlineAuth.getCurrentUser();
        onlineRef.child(currentUser.getUid()).child("online").setValue(true);

        groupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists())
                {
                    displayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    //    initialize  fields
    private void initializeFirelds() {
        mToolbar = findViewById(R.id.groups_chat_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);
        sendMessageButton = findViewById(R.id.send_message_button);
        userMessageInput = findViewById(R.id.input_group_message);
        mScrollView = findViewById(R.id.my_scrollView);
        displayMessage = findViewById(R.id.group_chat_text_display);
    }

    private void getUserInfo() {

        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void saveMessageInfoToDataBase() {
        String message = userMessageInput.getText().toString();
        String messageKey = groupNameRef.push().getKey();
        
        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please write your Messages...", Toast.LENGTH_SHORT).show();
        }else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate= currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm, a");
            currentTime= currentTimeFormat.format(calForDate.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupNameRef.updateChildren(groupMessageKey);

            groupMessageRefKey = groupNameRef.child(messageKey);

            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message",message);
            messageInfoMap.put("date", currentDate);
            messageInfoMap.put("time", currentTime);

            groupMessageRefKey.updateChildren(messageInfoMap);

        }
    }

//    display messages
    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatName = (String) ((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();


            displayMessage.append(chatName + " :\n" + chatMessage +"\n" + chatTime +"      " + chatDate + "\n\n\n");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


}
