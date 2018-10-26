package com.example.khokan.tutorisbdservice.learning;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.khokan.tutorisbdservice.MyBooksAdapter;
import com.example.khokan.tutorisbdservice.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class BooksUploadActivity extends AppCompatActivity {
    Button selectFile, uploadFile;
    TextView notification, total_books;
    EditText book_name;
    private final static int REQUEST_CODE=1;
    Uri pdfUri;// uri are
    ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    String books_uploader_id;
    int i=1;
    int total=0;

    FirebaseStorage storage; // user for uploading files
    FirebaseDatabase database;// used to store URLs of uploaded file;
    DatabaseReference databaseReference;

    private RecyclerView booklist_recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_upload);

        storage = FirebaseStorage.getInstance();//return an object of Firebase storage
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        books_uploader_id = mAuth.getCurrentUser().getUid();


        selectFile = findViewById(R.id.select_book);
        uploadFile = findViewById(R.id.upload_book);
        notification = findViewById(R.id.upload_book_name);
        book_name = findViewById(R.id.input_file_name);
        total_books = findViewById(R.id.total_books);
        total_books.setText("Total Books: "+total);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Uploaded Books");


        booklist_recyclerView = findViewById(R.id.booklist_recyclerView);

        booklist_recyclerView.setLayoutManager(new LinearLayoutManager(BooksUploadActivity.this));
        MyBooksAdapter myBooksAdapter = new MyBooksAdapter(booklist_recyclerView, BooksUploadActivity.this,new ArrayList<String>(),new ArrayList<String>());
        booklist_recyclerView.setAdapter(myBooksAdapter);

        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String fileName = dataSnapshot.getKey();
                String url = dataSnapshot.getValue().toString();

                ((MyBooksAdapter)booklist_recyclerView.getAdapter()).update(fileName, url);
                        total_books.setText("Total Books:" +i++);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

        selectFile.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                        selectPdf();

                    } else {
                        ActivityCompat.requestPermissions(BooksUploadActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);

                    }
                }
                else { //permission is automatically granted on sdk<23 upon installation

                }
            }
        });

        uploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pdfUri!= null) {
                    uploadBooks();
                }else
                {
                    Toast.makeText(BooksUploadActivity.this, "Select a File", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void uploadBooks() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading Book");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName = System.currentTimeMillis()+".pdf";
//        final String filenameForDatabase = System.currentTimeMillis()+"";

        StorageReference storageReference = storage.getReference();// return the root path;
        final String store_book_name =book_name.getText().toString() ;
        storageReference.child("Uploads_books").child(fileName).putFile(pdfUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        String url = taskSnapshot.getDownloadUrl().toString();// it return your uploaded files
                        //store the url in realtime database
                        DatabaseReference reference = database.getReference(); // return the path to root
                        reference.child("Uploaded Books").child(store_book_name).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                
                                if (!TextUtils.isEmpty(store_book_name)) {
                                    if (!store_book_name.matches("[-.? ]*")) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(BooksUploadActivity.this, "File Successfully Uploaded", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            book_name.setText("");

                                        } else {
                                            Toast.makeText(BooksUploadActivity.this, "File Not uploaded", Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                        }
                                    } else {
                                        Toast.makeText(BooksUploadActivity.this, "Please Dont User of These [-.? ]* character....", Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                    {
                                        Toast.makeText(BooksUploadActivity.this, "Books Name can not be empty...!", Toast.LENGTH_SHORT).show();
                                    }
                                
                            }
                        });



                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BooksUploadActivity.this, "File Not uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //track the progress of = our
                int currentProgress =(int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(requestCode==1 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
            selectPdf();

        }else
        {
            Toast.makeText(this, "Please Provide Permission...", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectPdf() {
        //to offer user to select a file using file manager

        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 86);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether user has selected a file or not
        if (requestCode==86 && resultCode==RESULT_OK && data!=null)
        {

            pdfUri = data.getData();// return the uri of selected file
            notification.setText("Selected File: "+ pdfUri.getLastPathSegment());
        }else
        {
            Toast.makeText(this, "Please Select a File", Toast.LENGTH_SHORT).show();
        }
    }

}
