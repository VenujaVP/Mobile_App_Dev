package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class Flash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        // Delay for 3 seconds (3000 milliseconds) before navigating to the next activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Navigate to GetStartedActivity after the delay
                Intent intent = new Intent(Flash.this, Getstart.class);
                startActivity(intent);
                // Close the Flash (Splash) Activity so the user cannot return to it
                finish();
            }
        }, 3000); // 3-second delay
    }
}
