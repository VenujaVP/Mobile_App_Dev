package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class CustomRecipeAdapter extends RecyclerView.Adapter<CustomRecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    public CustomRecipeAdapter(List<Recipe> recipeList, Context context) {
        this.recipeList = recipeList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Load image using Glide
        Glide.with(holder.itemView.getContext())
                .load(recipe.getImageUrl())
                .into(holder.recipeImage);

        // Set the recipe name and cooking time
        holder.recipeName.setText(recipe.getName());
        holder.cookingTime.setText(recipe.getCookingTime());

        // Set favorite icon based on isFavorite status
        holder.favourite.setImageResource(recipe.isFavorite() ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);

        // Set onClickListener for the favorite icon
        holder.favourite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !recipe.isFavorite();
            recipe.setFavorite(newFavoriteStatus); // Update favorite status
            holder.favourite.setImageResource(newFavoriteStatus ? R.drawable.baseline_favorite_24 : R.drawable.baseline_favorite_border_24);

            // Update the favorite status in Firebase
            DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("recipes").child(recipe.getRecipeId());
            recipeRef.child("isFavorite").setValue(newFavoriteStatus);
        });

        // Set onClickListener to navigate to RecipeDetails
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Recapidetails.class);
            intent.putExtra("recipeId", recipe.getRecipeId()); // Pass the recipeId or other necessary data
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // Method to update the list based on search results
    public void updateList(List<Recipe> newList) {
        recipeList = newList;
        notifyDataSetChanged(); // Notify adapter about the updated data
    }

    static class RecipeViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        ImageView favourite; // Add reference to favourite ImageView
        TextView recipeName;
        TextView cookingTime;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            favourite = itemView.findViewById(R.id.favourite); // Initialize favourite ImageView
            recipeName = itemView.findViewById(R.id.recipeName);
            cookingTime = itemView.findViewById(R.id.cookingTime);
        }
    }
}
