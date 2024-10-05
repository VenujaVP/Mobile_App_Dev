package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class category extends AppCompatActivity {

    private ImageView homeIcon;
    private ImageView categoryIcon;
    private ImageView profileIcon;
    private RecyclerView recyclerView;
    private CustomRecipeAdapter adapter;
    private List<Recipe> favoriteRecipes;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_category);

        homeIcon = findViewById(R.id.homeIcon);
        categoryIcon = findViewById(R.id.categoryIcon);
        profileIcon = findViewById(R.id.profileIcon);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get favorite recipes
        favoriteRecipes = new ArrayList<>();
        fetchFavoriteRecipes();

        // Set adapter
        adapter = new CustomRecipeAdapter(favoriteRecipes, this);
        recyclerView.setAdapter(adapter);


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

    private void fetchFavoriteRecipes() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                favoriteRecipes.clear(); // Clear existing list
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Boolean isFavorite = recipeSnapshot.child("isFavorite").getValue(Boolean.class);
                    if (isFavorite != null && isFavorite) {
                        String recipeId = recipeSnapshot.child("recipeId").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);
                        String imageUrl = recipeSnapshot.child("imageUrl").getValue(String.class);

                        // Log the values for debugging
                        Log.d("FavoriteRecipes", "Recipe ID: " + recipeId + ", Name: " + name + ", Image URL: " + imageUrl);

                        // Create the Recipe object and add it to the list
                        if (recipeId != null && name != null && imageUrl != null) {
                            Recipe recipe = new Recipe(recipeId, null, name, null, null, null, imageUrl, null, true);
                            favoriteRecipes.add(recipe);
                        } else {
                            Log.d("FavoriteRecipes", "Null values found for recipe ID: " + recipeId);
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(category.this, "Error loading favorite recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }
}