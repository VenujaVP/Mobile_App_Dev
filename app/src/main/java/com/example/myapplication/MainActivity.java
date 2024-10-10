//IM_2021_056 - Nadeeka Kariyawasam

package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CustomRecipeAdapter recipeAdapter;
    private List<Recipe> recipeList;
    private DatabaseReference mDatabase;
    private ImageView homeIcon;
    private ImageView categoryIcon;
    private ImageView profileIcon;
    private EditText searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        recyclerView = findViewById(R.id.recipeRecyclerView);
        homeIcon = findViewById(R.id.homeIcon);
        categoryIcon = findViewById(R.id.categoryIcon);
        profileIcon = findViewById(R.id.profileIcon);
        searchBar = findViewById(R.id.searchBar);

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recipeList = new ArrayList<>();
        recipeAdapter = new CustomRecipeAdapter(recipeList, this);
        recyclerView.setAdapter(recipeAdapter);

        // Initialize Firebase Database reference
        mDatabase = FirebaseDatabase.getInstance().getReference("recipes");

        // Load all recipe images
        loadAllRecipeImages();

        // Set up TextWatcher for search bar
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed here
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call the search method whenever the text changes
                searchRecipes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed here
            }
        });

        // Set click listeners for icons
        homeIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, MainActivity.class)));
        categoryIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, category.class)));
        profileIcon.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, profile.class)));
    }

    private void loadAllRecipeImages() {
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

                        // Create a Recipe object and add it to the list
                        if (recipeId != null && userId != null && name != null && imageUrl != null && cookingTime != null) {
                            Recipe recipe = new Recipe(recipeId, userId, name, cookingTime, ingredients, instructions, imageUrl);
                            recipeList.add(recipe);
                        }
                    }
                    recipeAdapter.notifyDataSetChanged(); // Notify adapter of data change
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Error loading recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchRecipes(String query) {
        // Create a new list to hold the filtered recipes
        List<Recipe> filteredList = new ArrayList<>();

        // Loop through the original recipe list and filter based on the recipe name
        for (Recipe recipe : recipeList) {
            if (recipe.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(recipe);
            }
        }

        // Update the adapter with the filtered list
        recipeAdapter.updateList(filteredList); // Call the correct method to update the list
    }
}
