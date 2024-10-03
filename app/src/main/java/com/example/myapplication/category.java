package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class category extends AppCompatActivity {

    private ImageView homeIcon;
    private ImageView categoryIcon;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        homeIcon = findViewById(R.id.homeIcon);
        categoryIcon = findViewById(R.id.categoryIcon);
        profileIcon = findViewById(R.id.profileIcon);


        // Set click listeners for icons
        homeIcon.setOnClickListener(v -> startActivity(new Intent(category.this, MainActivity.class)));
        categoryIcon.setOnClickListener(v -> startActivity(new Intent(category.this, category.class)));
        profileIcon.setOnClickListener(v -> startActivity(new Intent(category.this, profile.class)));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}