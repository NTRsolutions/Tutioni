package com.example.khokan.tutorisbdservice;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText userEmail, userPassword;
    private TextView already_have_account;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;
    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        initializeFields();


        already_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToLoginActivity();
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        String email = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please insert Email Address!", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "please insert Password!", Toast.LENGTH_SHORT).show();
        }else {



            mDialog.setTitle("creating Account!");
            mDialog.setMessage("please Wait while we are creating you account!");
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful())
                    {
                        String currentId= mAuth.getCurrentUser().getUid();
                        rootRef.child("Users").child(currentId).setValue("");
                        sendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }else
                    {
                        String error = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error: "+ error, Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                }
            });
        }

    }

    private void initializeFields() {

        createAccountButton = findViewById(R.id.create_button);
        already_have_account = findViewById(R.id.already_have_account_link);
        userEmail = findViewById(R.id.register_email);
        userPassword = findViewById(R.id.register_password);
        mDialog = new ProgressDialog(this);
    }

    private void sendToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
    private void sendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(mainIntent);
        finish();
    }

}
