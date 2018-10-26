package com.example.khokan.tutorisbdservice.learning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.khokan.tutorisbdservice.R;

public class LearningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning);

        Toolbar learning_appBar = findViewById(R.id.learning_app_bar);
        setSupportActionBar(learning_appBar);
        getSupportActionBar().setTitle(" Learning ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_learning,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.video_tutorials)
        {
            Toast.makeText(this, "Coming Soon.....", Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.best_sites_for_learning)
        {
            Toast.makeText(this, "Coming Soon.....", Toast.LENGTH_SHORT).show();
        }if (item.getItemId() == R.id.learning_programming)
        {
            sendToProgramming();
        }
        return true;
    }

    private void sendToProgramming() {
        Intent programmingIntent = new Intent(LearningActivity.this, LearningProgrammingActivity.class);
        startActivity(programmingIntent);
    }
}
