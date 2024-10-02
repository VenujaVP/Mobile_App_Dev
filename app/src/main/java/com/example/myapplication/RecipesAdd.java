package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;

public class RecipesAdd extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private ImageView recipeImage;
    private Button uploadImageButton, uploadVideoButton, addIngredientButton, addInstructionButton, submitRecipeButton;
    private LinearLayout ingredientsContainer, instructionsContainer;
    private Uri imageUri, videoUri;
    private EditText recipeNameInput, cookingTimeInput;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private ArrayList<EditText> ingredientInputs = new ArrayList<>();
    private ArrayList<EditText> instructionInputs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes_add);

        // Initialize UI components
        recipeImage = findViewById(R.id.recipeImage);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        addInstructionButton = findViewById(R.id.addInstructionButton);
        submitRecipeButton = findViewById(R.id.submitRecipeButton);
        instructionsContainer = findViewById(R.id.instructionsContainer);
        recipeNameInput = findViewById(R.id.recipeNameInput);
        cookingTimeInput = findViewById(R.id.cookingTimeInput);

        ingredientInputs.add(findViewById(R.id.ingredientInput1));
        instructionInputs.add(findViewById(R.id.instructionStep1));

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
        mStorage = FirebaseStorage.getInstance().getReference("recipe_media");

        // Set listeners for buttons
        uploadImageButton.setOnClickListener(v -> openImageChooser());
        uploadVideoButton.setOnClickListener(v -> openVideoChooser());
        addIngredientButton.setOnClickListener(v -> addIngredientField());
        addInstructionButton.setOnClickListener(v -> addInstructionField());
        submitRecipeButton.setOnClickListener(v -> submitRecipe());
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Image"), PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Recipe Video"), PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                recipeImage.setImageBitmap(bitmap); // Display the image
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            Toast.makeText(this, "Video selected successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    private void addIngredientField() {
        EditText newIngredientInput = new EditText(this);
        newIngredientInput.setHint("Enter ingredient");
        newIngredientInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        ingredientsContainer.addView(newIngredientInput);
        ingredientInputs.add(newIngredientInput);
    }

    private void addInstructionField() {
        EditText newInstructionInput = new EditText(this);
        newInstructionInput.setHint("Enter step " + (instructionInputs.size() + 1));
        newInstructionInput.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        instructionsContainer.addView(newInstructionInput);
        instructionInputs.add(newInstructionInput);
    }

    private void submitRecipe() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && imageUri != null && videoUri != null) {
            String userId = user.getUid();
            String recipeName = recipeNameInput.getText().toString().trim();
            String cookingTime = cookingTimeInput.getText().toString().trim();

            StorageReference imageRef = mStorage.child(userId + "/images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(imageUrl -> {

                    StorageReference videoRef = mStorage.child(userId + "/videos/" + System.currentTimeMillis() + ".mp4");
                    videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot1 -> {
                        videoRef.getDownloadUrl().addOnSuccessListener(videoUrl -> {
                            ArrayList<String> ingredientsList = new ArrayList<>();
                            ArrayList<String> instructionsList = new ArrayList<>();

                            for (EditText ingredientInput : ingredientInputs) {
                                String ingredient = ingredientInput.getText().toString().trim();
                                if (!ingredient.isEmpty()) {
                                    ingredientsList.add(ingredient);
                                }
                            }

                            for (EditText instructionInput : instructionInputs) {
                                String instruction = instructionInput.getText().toString().trim();
                                if (!instruction.isEmpty()) {
                                    instructionsList.add(instruction);
                                }
                            }

                            if (recipeName.isEmpty() || cookingTime.isEmpty() || ingredientsList.isEmpty() || instructionsList.isEmpty()) {
                                Toast.makeText(RecipesAdd.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String recipeId = mDatabase.push().getKey();
                            Recipe recipe = new Recipe(recipeId, userId, recipeName, cookingTime, ingredientsList, instructionsList, imageUrl.toString(), videoUrl.toString());

                            if (recipeId != null) {
                                mDatabase.child(recipeId).setValue(recipe).addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RecipesAdd.this, "Recipe added successfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else {
                                        Toast.makeText(RecipesAdd.this, "Failed to add recipe", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    });
                });
            }).addOnFailureListener(e -> Toast.makeText(RecipesAdd.this, "Failed to upload image/video.", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(RecipesAdd.this, "Please select an image and video.", Toast.LENGTH_SHORT).show();
        }
    }
}
