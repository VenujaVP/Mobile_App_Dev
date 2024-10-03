package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class EditRecipeActivity extends AppCompatActivity {

    private EditText recipeNameInput, cookingTimeInput;
    private ImageView recipeImage;
    private Button uploadImageButton, uploadVideoButton, addIngredientButton, addInstructionButton, submitRecipeButton;
    private LinearLayout ingredientsContainer, instructionsContainer;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;
    private String recipeId;
    private Uri recipeImageUri; // To store the URI of the uploaded image
    private Uri recipeVideoUri;  // To store the URI of the uploaded video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        // Initialize views
        recipeNameInput = findViewById(R.id.recipeNameInput);
        cookingTimeInput = findViewById(R.id.cookingTimeInput);
        recipeImage = findViewById(R.id.recipeImage);
        uploadImageButton = findViewById(R.id.uploadImageButton);
        uploadVideoButton = findViewById(R.id.uploadVideoButton);
        addIngredientButton = findViewById(R.id.addIngredientButton);
        addInstructionButton = findViewById(R.id.addInstructionButton);
        submitRecipeButton = findViewById(R.id.submitRecipeButton);
        ingredientsContainer = findViewById(R.id.ingredientsContainer);
        instructionsContainer = findViewById(R.id.instructionsContainer);

        // Get recipe ID from the intent
        recipeId = getIntent().getStringExtra("recipeId");

        // Initialize Firebase Database and Storage references
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");
        mStorage = FirebaseStorage.getInstance().getReference("recipe_images");

        // Fetch the current recipe details for editing
        if (recipeId != null) {
            fetchRecipeDetails(recipeId);
        }

        // Handle adding more ingredients dynamically
        addIngredientButton.setOnClickListener(v -> addIngredientField());

        // Handle adding more instruction steps dynamically
        addInstructionButton.setOnClickListener(v -> addInstructionField());

        // Handle the image upload
        uploadImageButton.setOnClickListener(v -> uploadImage());

        // Handle the video upload
        uploadVideoButton.setOnClickListener(v -> uploadVideo());

        // Handle the submit button
        submitRecipeButton.setOnClickListener(v -> submitUpdatedRecipe());
    }

    private void fetchRecipeDetails(String recipeId) {
        mDatabase.child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Fetch recipe details and set them in the input fields
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String cookingTime = dataSnapshot.child("cookingTime").getValue(String.class);
                    ArrayList<String> ingredients = new ArrayList<>();
                    ArrayList<String> instructions = new ArrayList<>();

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

                    // Populate the fields
                    recipeNameInput.setText(name != null ? name : "");
                    cookingTimeInput.setText(cookingTime != null ? cookingTime : "");

                    // Populate ingredients and instructions dynamically
                    populateFields(ingredientsContainer, ingredients);
                    populateFields(instructionsContainer, instructions);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(EditRecipeActivity.this, "Failed to load recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateFields(LinearLayout container, ArrayList<String> items) {
        for (String item : items) {
            EditText editText = new EditText(EditRecipeActivity.this);
            editText.setText(item);
            container.addView(editText);
        }
    }

    private void addIngredientField() {
        EditText newIngredient = new EditText(this);
        newIngredient.setHint("Enter ingredient");
        ingredientsContainer.addView(newIngredient);
    }

    private void addInstructionField() {
        EditText newInstruction = new EditText(this);
        newInstruction.setHint("Enter step");
        newInstruction.setMinLines(3);
        instructionsContainer.addView(newInstruction);
    }

    private void uploadImage() {
        // Launch image picker to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    private void uploadVideo() {
        // Launch video picker to select a video
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            recipeImageUri = data.getData();
            recipeImage.setImageURI(recipeImageUri);
        } else if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            recipeVideoUri = data.getData();
            // You can handle the selected video URI here (e.g., show it in a VideoView)
        }
    }

    private void submitUpdatedRecipe() {
        // Gather the updated details and submit to Firebase
        String updatedName = recipeNameInput.getText().toString();
        String updatedCookingTime = cookingTimeInput.getText().toString();
        ArrayList<String> updatedIngredients = new ArrayList<>();
        ArrayList<String> updatedInstructions = new ArrayList<>();

        for (int i = 0; i < ingredientsContainer.getChildCount(); i++) {
            EditText ingredientInput = (EditText) ingredientsContainer.getChildAt(i);
            updatedIngredients.add(ingredientInput.getText().toString());
        }

        for (int i = 0; i < instructionsContainer.getChildCount(); i++) {
            EditText instructionInput = (EditText) instructionsContainer.getChildAt(i);
            updatedInstructions.add(instructionInput.getText().toString());
        }

        // Push the updates to Firebase
        mDatabase.child(recipeId).child("name").setValue(updatedName);
        mDatabase.child(recipeId).child("cookingTime").setValue(updatedCookingTime);
        mDatabase.child(recipeId).child("ingredients").setValue(updatedIngredients);
        mDatabase.child(recipeId).child("instructions").setValue(updatedInstructions);

        // Upload the image if selected
        if (recipeImageUri != null) {
            uploadImageToFirebase(recipeImageUri);
        }

        // Upload the video if selected
        if (recipeVideoUri != null) {
            uploadVideoToFirebase(recipeVideoUri);
        }

        // Show a success message if everything is submitted
        Toast.makeText(this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
        finish();  // Close the activity
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileReference = mStorage.child(recipeId + ".jpg");
        fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditRecipeActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                // Update the image URL in the database
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    mDatabase.child(recipeId).child("imageUrl").setValue(uri.toString());
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditRecipeActivity.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadVideoToFirebase(Uri videoUri) {
        // Create a StorageReference for the video
        final StorageReference videoReference = FirebaseStorage.getInstance().getReference("recipe_videos").child(recipeId + ".mp4");
        videoReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditRecipeActivity.this, "Video uploaded successfully!", Toast.LENGTH_SHORT).show();
                // Update the video URL in the database
                videoReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    mDatabase.child(recipeId).child("videoUrl").setValue(uri.toString());
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditRecipeActivity.this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}