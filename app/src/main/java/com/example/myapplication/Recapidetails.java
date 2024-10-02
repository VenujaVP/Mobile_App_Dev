package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.util.ArrayList;

public class Recapidetails extends AppCompatActivity {

    private TextView recipeNameText;
    private TextView recipeCookingTimeText;
    private TextView recipeIngredientsText;
    private TextView recipeInstructionsText;
    private PlayerView playerView;  // ExoPlayer's PlayerView
    private ExoPlayer exoPlayer;    // ExoPlayer instance

    private DatabaseReference mDatabase;
    private String recipeId; // Store recipeId for later use

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recapidetails);

        // Initialize views
        recipeNameText = findViewById(R.id.recipeNameText);
        recipeCookingTimeText = findViewById(R.id.recipeCookingTimeText);
        recipeIngredientsText = findViewById(R.id.recipeIngredientsText);
        recipeInstructionsText = findViewById(R.id.recipeInstructionsText);
        playerView = findViewById(R.id.playerView); // Initialize PlayerView

        // Get the recipe ID from the intent
        recipeId = getIntent().getStringExtra("recipeId");

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch and display the recipe details
        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
        }

        // Initialize buttons
        Button buttonEdit = findViewById(R.id.buttonEdit);
        Button buttonDelete = findViewById(R.id.buttonDelete);

        // Set up click listener for the edit button
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Recapidetails.this, EditRecipeActivity.class);
                intent.putExtra("recipeId", recipeId);
                startActivity(intent);
            }
        });

        // Set up click listener for the delete button
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipe(recipeId);
            }
        });
    }

    private void fetchRecipeDetails(String recipeId) {
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the recipe details
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String cookingTime = dataSnapshot.child("cookingTime").getValue(String.class);
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> instructions = new ArrayList<>();
                    String videoUrl = dataSnapshot.child("videoUrl").getValue(String.class); // Get video URL

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

                    // Set the recipe details to TextViews
                    recipeNameText.setText(name);
                    recipeCookingTimeText.setText(cookingTime);
                    recipeIngredientsText.setText(String.join(", ", ingredients));
                    recipeInstructionsText.setText(String.join("\n", instructions));

                    // Set video to ExoPlayer's PlayerView
                    if (videoUrl != null) {
                        // Initialize ExoPlayer and set the video
                        exoPlayer = new ExoPlayer.Builder(Recapidetails.this).build();
                        playerView.setPlayer(exoPlayer);
                        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(videoUrl));
                        exoPlayer.setMediaItem(mediaItem);
                        exoPlayer.prepare();
                        exoPlayer.play();
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

    private void deleteRecipe(String recipeId) {
        mDatabase.child(recipeId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(Recapidetails.this, "Recipe deleted successfully", Toast.LENGTH_SHORT).show();
                finish(); // Close this activity and return to the previous one
            } else {
                Toast.makeText(Recapidetails.this, "Failed to delete recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release ExoPlayer when done
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
