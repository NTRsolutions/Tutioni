package com.example.khokan.tutorisbdservice.learnning;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.khokan.tutorisbdservice.R;

public class LearningProgrammingActivity extends AppCompatActivity {
    Toolbar programing_appBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_programming);

        programing_appBar = findViewById(R.id.programming_app_bar);
        setSupportActionBar(programing_appBar);
        getSupportActionBar().setTitle("Learning Programming");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

    }
}
