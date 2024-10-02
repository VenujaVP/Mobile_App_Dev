package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Getstart extends AppCompatActivity {
    private Button Get;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Always call super first

        EdgeToEdge.enable(this);  // Set Edge-to-Edge mode
        setContentView(R.layout.activity_getstart);  // Set the content view first

        // Initialize the button after setting the content view
        Get = findViewById(R.id.Get);
        Get.setOnClickListener(v -> {
            // Redirect to Login Activity
            startActivity(new Intent(Getstart.this, login.class));
        });

        // Set up window insets to ensure proper padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
