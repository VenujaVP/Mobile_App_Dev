//Shashika Prabhath - IM-2021-086

package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem; // Updated import for MediaItem
import androidx.media3.exoplayer.ExoPlayer; // Updated import for ExoPlayer
import androidx.media3.ui.PlayerView; // Updated import for PlayerView

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Recapidetails extends AppCompatActivity {

    private TextView recipeNameText, recipeCookingTimeText, recipeIngredientsText, recipeInstructionsText, ratingHere;
    private PlayerView playerView;
    private ExoPlayer player;
    private RatingBar ratingBar;
    private DatabaseReference mDatabase;
    private String recipeId;
    private Button buttonEdit, buttonDelete;
    private ImageView shareButton;
    private String currentUserId;
    private String videoUrl; // For sharing the video URL

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapidetails);

        // Initialize Firebase Auth and get current user ID
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize views
        recipeNameText = findViewById(R.id.recipeNameText);
        ratingBar = findViewById(R.id.ratingBar);
        ratingHere = findViewById(R.id.ratingHere);
        recipeCookingTimeText = findViewById(R.id.recipeCookingTimeText);
        recipeIngredientsText = findViewById(R.id.recipeIngredientsText);
        recipeInstructionsText = findViewById(R.id.recipeInstructionsText);
        playerView = findViewById(R.id.detailVideo);
        buttonEdit = findViewById(R.id.buttonEdit);
        buttonDelete = findViewById(R.id.buttonDelete);
        shareButton = findViewById(R.id.shareButton); // Initialize the share button

        // Get the recipe ID from the intent
        recipeId = getIntent().getStringExtra("recipeId");

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch and display the recipe details
        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
            fetchAverageRating(); // Fetch and display average rating
        } else {
            Toast.makeText(this, "Invalid recipe ID", Toast.LENGTH_SHORT).show();
            finish(); // Close activity if no recipe ID is found
        }

        // Set up click listener for the edit button
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(Recapidetails.this, EditRecipeActivity.class);
            intent.putExtra("recipeId", recipeId);
            startActivity(intent);
        });

        ratingHere.setOnClickListener(v -> {
            Intent intent = new Intent(Recapidetails.this, RatingPage.class);
            intent.putExtra("recipeId", recipeId);
            startActivity(intent);
        });

        // Set up click listener for the delete button
        buttonDelete.setOnClickListener(v -> deleteRecipe(recipeId));

        // Set up click listener for the share button
        shareButton.setOnClickListener(v -> shareRecipeDetails());
    }

    private void fetchRecipeDetails(String recipeId) {
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String cookingTime = dataSnapshot.child("cookingTime").getValue(String.class);
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> instructions = new ArrayList<>();
                    videoUrl = dataSnapshot.child("videoUrl").getValue(String.class);
                    String recipeOwnerId = dataSnapshot.child("userId").getValue(String.class);

                    // Check if the current user is the owner of the recipe
                    if (recipeOwnerId != null && recipeOwnerId.equals(currentUserId)) {
                        buttonEdit.setVisibility(Button.VISIBLE); // Show Edit button
                        buttonDelete.setVisibility(Button.VISIBLE); // Show Delete button
                    } else {
                        buttonEdit.setVisibility(Button.GONE); // Hide Edit button
                        buttonDelete.setVisibility(Button.GONE); // Hide Delete button
                    }

                    for (DataSnapshot ingredientSnapshot : dataSnapshot.child("ingredients").getChildren()) {
                        String ingredient = ingredientSnapshot.getValue(String.class);
                        if (ingredient != null) {
                            ingredients.add(ingredient);
                        }
                    }

                    for (DataSnapshot instructionSnapshot : dataSnapshot.child("instructions").getChildren()) {
                        String instruction = instructionSnapshot.getValue(String.class);
                        if (instruction != null) {
                            instructions.add(instruction);
                        }
                    }

                    // Set recipe details to TextViews
                    recipeNameText.setText(name != null ? name : "N/A");
                    recipeCookingTimeText.setText(cookingTime != null ? cookingTime : "N/A");
                    recipeIngredientsText.setText(String.join(", ", ingredients));
                    recipeInstructionsText.setText(String.join("\n", instructions));

                    // Prepare video playback
                    if (videoUrl != null) {
                        setupPlayer(videoUrl);
                    }
                } else {
                    Toast.makeText(Recapidetails.this, "Recipe not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Recapidetails.this, "Failed to load recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAverageRating() {
        // Reference to the ratings for the specific recipe
        DatabaseReference ratingsRef = mDatabase.child(recipeId).child("ratings");

        ratingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    float totalRating = 0;
                    int ratingCount = 0;

                    // Loop through all the ratings to calculate the total and count
                    for (DataSnapshot ratingSnapshot : dataSnapshot.getChildren()) {
                        Float rating = ratingSnapshot.getValue(Float.class);
                        if (rating != null) {
                            totalRating += rating;
                            ratingCount++;
                        }
                    }

                    // Calculate the average rating
                    float averageRating = (ratingCount > 0) ? totalRating / ratingCount : 0;

                    // Update the RatingBar with the average rating
                    ratingBar.setRating(averageRating);

                    // Display the average rating as text
                    ratingHere.setText(String.format("Average Rating: %.1f (%d ratings)", averageRating, ratingCount));

                } else {
                    // Handle case when there are no ratings
                    ratingHere.setText("No ratings yet");
                    ratingBar.setRating(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Recapidetails.this, "Failed to load average rating", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupPlayer(String videoUrl) {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void deleteRecipe(String recipeId) {
        mDatabase.child(recipeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Recapidetails.this, "Recipe deleted", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(Recapidetails.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareRecipeDetails() {
        // Create share content
        String shareContent = "Check out this recipe!\n\n"
                + "Recipe Name: " + recipeNameText.getText().toString() + "\n"
                + "Cooking Time: " + recipeCookingTimeText.getText().toString() + "\n"
                + "Ingredients: " + recipeIngredientsText.getText().toString() + "\n"
                + "Instructions: " + recipeInstructionsText.getText().toString() + "\n";
        if (videoUrl != null) {
            shareContent += "Watch the recipe video: " + videoUrl;
        }

        // Create share intent
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);

        // Start share activity
        startActivity(Intent.createChooser(shareIntent, "Share Recipe via"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release(); // Release the player when activity is stopped
            player = null;
        }
    }
}
