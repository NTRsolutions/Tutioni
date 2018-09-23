package com.example.khokan.tutorisbdservice;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {
    private Toolbar main_page_appbar;
    private AppBarLayout myAppbarLayout;
    private TabLayout myTabLayout;
    private ViewPager myViewPager;
    private TabsAccessAdapter myTabsAccessAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
//    Firebase
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        rootRef= FirebaseDatabase.getInstance().getReference();
//        menu


        main_page_appbar = findViewById(R.id.main_page_bar);
        setSupportActionBar(main_page_appbar);
        getSupportActionBar().setTitle("TutorsBD Servives");

        myViewPager = findViewById(R.id.main_tabs_pager);
        myTabsAccessAdapter = new TabsAccessAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAccessAdapter);

        myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null)
        {
            sendToLogin();
        }
        else
            {
                varifiyUserExistence();
            }

    }

    private void varifiyUserExistence() {
        String currentUserId= mAuth.getCurrentUser().getUid();
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome..", Toast.LENGTH_SHORT).show();
                }else
                    {
                       sendToSettingActivity();
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.main_finds_friend_option)
        {
            sendToFindFriendsActivity();
        }
        if (item.getItemId() == R.id.main_setting_option)
        {
            sendToSettingActivity();
        }

        if (item.getItemId() == R.id.main_create_group)
        {
            createNewGroupChat();
        }

        if (item.getItemId() == R.id.main_logout_option)
        {
            mAuth.signOut();
            sendToLogin();
        }
        return true;
    }

    private void createNewGroupChat() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.alertDialog);
        builder.setTitle("Enter Your Group Name : ");
        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g math_sir");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(MainActivity.this, "Please inset a Group Name!", Toast.LENGTH_SHORT).show();
                }else
                {
                    creatNewGroup(groupName);
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void creatNewGroup(final String groupName) {

        rootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName + " group is created successfully.....!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


    //    sendint intent
    private void sendToSettingActivity() {
        Intent settingIntent = new Intent(MainActivity.this,SettingActivity.class);
        settingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingIntent);
        finish();
    }

    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void sendToFindFriendsActivity() {
        Intent friendIntent = new Intent(MainActivity.this, FindsFriendsActivity.class);
        startActivity(friendIntent);
        finish();
    }
}
