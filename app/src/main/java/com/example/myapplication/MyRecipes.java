//IM_2021_056 -- Venuja Prasanjith

package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MyRecipes extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomRecipeAdapter customRecipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseReference mDatabase;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recipes);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeList = new ArrayList<>();
        customRecipeAdapter = new CustomRecipeAdapter(recipeList, this);
        recyclerView.setAdapter(customRecipeAdapter);

        // Get the current user ID from Firebase Auth
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        } else {
            // Handle user not logged in
            Toast.makeText(this, "User is not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        // Load recipes that belong to the current user
        loadUserRecipes();
    }

    private void loadUserRecipes() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipeList.clear(); // Clear the existing list

                if (dataSnapshot.exists()) {
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        String recipeId = recipeSnapshot.child("recipeId").getValue(String.class);
                        String userId = recipeSnapshot.child("userId").getValue(String.class);
                        String name = recipeSnapshot.child("name").getValue(String.class);
                        String imageUrl = recipeSnapshot.child("imageUrl").getValue(String.class);
                        String cookingTime = recipeSnapshot.child("cookingTime").getValue(String.class);

                        // Get ingredients and instructions
                        ArrayList<String> ingredients = new ArrayList<>();
                        ArrayList<String> instructions = new ArrayList<>();

                        for (DataSnapshot ingredientSnapshot : recipeSnapshot.child("ingredients").getChildren()) {
                            String ingredient = ingredientSnapshot.getValue(String.class);
                            if (ingredient != null) {
                                ingredients.add(ingredient);
                            }
                        }

                        for (DataSnapshot instructionSnapshot : recipeSnapshot.child("instructions").getChildren()) {
                            String instruction = instructionSnapshot.getValue(String.class);
                            if (instruction != null) {
                                instructions.add(instruction);
                            }
                        }

                        // Check if the recipe belongs to the current user
                        if (userId != null && userId.equals(currentUserId)) {
                            // Create a Recipe object and add it to the list
                            if (recipeId != null && name != null && imageUrl != null && cookingTime != null) {
                                Recipe recipe = new Recipe(recipeId, userId, name, cookingTime, ingredients, instructions, imageUrl);
                                recipeList.add(recipe);
                            }
                        }
                    }

                    customRecipeAdapter.notifyDataSetChanged(); // Notify adapter of data change
                } else {
                    Toast.makeText(MyRecipes.this, "No recipes found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyRecipes.this, "Error loading recipes", Toast.LENGTH_SHORT).show();
                Log.e("MyRecipes", "Database error: " + databaseError.getMessage());
            }
        });
    }
}
