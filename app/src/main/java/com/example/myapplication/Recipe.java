//Prashan  IM/2021/53

package com.example.myapplication;

import java.util.ArrayList;

public class Recipe {
    private String recipeId; // Unique ID for the recipe
    private String userId;   // User ID who created or owns the recipe
    private String name;     // Name of the recipe
    private String cookingTime; // Cooking time required for the recipe
    private ArrayList<String> ingredients; // List of ingredients for the recipe
    private ArrayList<String> instructions; // Cooking instructions for the recipe
    private String imageUrl; // URL for the recipe's image
    private String videoUrl; // Optional URL for a video of the recipe
    private boolean isFavorite; // Track if the recipe is marked as a favorite

    // Default constructor (required for Firebase DataSnapshot)
    public Recipe() {
    }

    // Constructor with all fields
    public Recipe(String recipeId, String userId, String name, String cookingTime,
                  ArrayList<String> ingredients, ArrayList<String> instructions,
                  String imageUrl, String videoUrl, boolean isFavorite) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.name = name;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl;
        this.isFavorite = isFavorite; // Initialize isFavorite
    }

    // Constructor without videoUrl (for cases where video is not provided)
    public Recipe(String recipeId, String userId, String name, String cookingTime,
                  ArrayList<String> ingredients, ArrayList<String> instructions,
                  String imageUrl) {
        this(recipeId, userId, name, cookingTime, ingredients, instructions, imageUrl, null, false);
    }

    // Getters and Setters...
    public String getRecipeId() {
        return recipeId;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getCookingTime() {
        return cookingTime;
    }

    public ArrayList<String> getIngredients() {
        return ingredients;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCookingTime(String cookingTime) {
        this.cookingTime = cookingTime;
    }

    public void setIngredients(ArrayList<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
