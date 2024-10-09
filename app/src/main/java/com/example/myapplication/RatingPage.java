//IM_2021_056 -- Venuja Prasanjith

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RatingPage extends AppCompatActivity {

    private static final String TAG = "RatingPage"; // Tag for logging
    private RatingBar ratingBar;
    private TextView ratingScale;
    private Button button;
    private DatabaseReference ratingsDatabase;
    private String recipeId;
    private ImageView backIcon;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_page);
        ratingBar = findViewById(R.id.ratingBar2);
        ratingScale = findViewById(R.id.textView4);
        button = findViewById(R.id.button2);

        try {
            // Get current user ID and recipe ID from intent
            FirebaseAuth auth = FirebaseAuth.getInstance();
            if (auth.getCurrentUser() != null) {
                currentUserId = auth.getCurrentUser().getUid();
            } else {
                Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
                finish(); // Exit the activity if no user is logged in
                return;
            }

            recipeId = getIntent().getStringExtra("recipeId");
            if (recipeId == null) {
                Toast.makeText(this, "No recipe ID provided", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Initialize Firebase reference to the ratings node
            ratingsDatabase = FirebaseDatabase.getInstance().getReference("recipes").child(recipeId).child("ratings");

            // Check if the user has already rated this recipe
            fetchUserRating();

            ratingBar.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        ratingScale.setText("Very Bad");
                        break;
                    case 2:
                        ratingScale.setText("Bad");
                        break;
                    case 3:
                        ratingScale.setText("Good");
                        break;
                    case 4:
                        ratingScale.setText("Great");
                        break;
                    case 5:
                        ratingScale.setText("Awesome");
                        break;
                    default:
                        ratingScale.setText("");
                        break;
                }
            });

            // Save or update rating
            button.setOnClickListener(v -> {
                float rating = ratingBar.getRating();
                Toast.makeText(RatingPage.this, "You rated " + rating + " stars", Toast.LENGTH_SHORT).show();

                saveOrUpdateRating(rating);
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to check if the user has already rated
    private void fetchUserRating() {
        ratingsDatabase.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User has already rated, load the rating
                    Float existingRating = dataSnapshot.getValue(Float.class);
                    if (existingRating != null) {
                        ratingBar.setRating(existingRating);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "Database error: " + databaseError.getMessage());
                Toast.makeText(RatingPage.this, "Failed to load rating", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to save or update the user's rating in Firebase
    private void saveOrUpdateRating(float rating) {
        ratingsDatabase.child(currentUserId).setValue(rating).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(RatingPage.this, "Rating saved", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity and go back to Recapidetails
            } else {
                Log.e(TAG, "Failed to save rating: " + task.getException());
                Toast.makeText(RatingPage.this, "Failed to save rating", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
