package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextView displayUsername;
    private ImageView profileImage;
    private Button uploadButton,myRecipesButton;
    private ImageView homeIcon;
    private ImageView categoryIcon;
    private ImageView profileIcon;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize UI components
        displayUsername = findViewById(R.id.displayUsername);
        profileImage = findViewById(R.id.profileImage);
        uploadButton = findViewById(R.id.uploadButton);
        myRecipesButton=findViewById(R.id.myRecipesButton);
        homeIcon=findViewById(R.id.homeIcon);
        categoryIcon=findViewById(R.id.categoryIcon);
        profileIcon=findViewById(R.id.profileIcon);
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");
        mStorage = FirebaseStorage.getInstance().getReference("profile_images");

        // Set up the upload button click listener
        uploadButton.setOnClickListener(v -> openFileChooser());

        // Load user data
        loadUserData();
       myRecipesButton.setOnClickListener(v -> {
            startActivity(new Intent(profile.this, RecipesAdd.class));
        });
        homeIcon.setOnClickListener(v -> {
            // Redirect to RegisterActivity
            startActivity(new Intent(profile.this, MainActivity.class));
        });
        categoryIcon.setOnClickListener(v -> {
            // Redirect to RegisterActivity
            startActivity(new Intent(profile.this, category.class));
        });
        profileIcon.setOnClickListener(v -> {
            // Redirect to RegisterActivity
            startActivity(new Intent(profile.this, profile.class));
        });

    }

    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            mDatabase.child(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User userProfile = task.getResult().getValue(User.class);
                    if (userProfile != null) {
                        displayUsername.setText(userProfile.getUsername());
                        // Load the profile image if URL is available
                        if (userProfile.getProfileImageUrl() != null && !userProfile.getProfileImageUrl().isEmpty()) {
                            Glide.with(this)
                                    .load(userProfile.getProfileImageUrl())
                                    .circleCrop()  // This makes the image circular
                                    .into(profileImage);

                        }
                    }
                } else {
                    Toast.makeText(profile.this, "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri fileUri = data.getData();
            uploadImageToFirebase(fileUri);
        }
    }

    private void uploadImageToFirebase(Uri fileUri) {
        if (fileUri != null) {
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                StorageReference fileReference = mStorage.child(user.getUid() + ".jpg");
                UploadTask uploadTask = fileReference.putFile(fileUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update the profile image URL in Firebase Realtime Database
                        String profileImageUrl = uri.toString();
                        mDatabase.child(user.getUid()).child("profileImageUrl").setValue(profileImageUrl)
                                .addOnSuccessListener(aVoid -> {
                                    // Update the ImageView with the new profile image
                                    Glide.with(this)
                                            .load(profileImageUrl)
                                            .into(profileImage);
                                    Toast.makeText(profile.this, "Profile Image Updated", Toast.LENGTH_SHORT).show();
                                });
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(profile.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}
