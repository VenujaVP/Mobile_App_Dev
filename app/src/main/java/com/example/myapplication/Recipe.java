package com.example.myapplication;

import java.util.ArrayList;

public class Recipe {
    private String recipeId;
    private String userId;
    private String name;
    private String cookingTime;
    private ArrayList<String> ingredients;
    private ArrayList<String> instructions;
    private String imageUrl;
    private String videoUrl; // Add videoUrl field

    // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
//    public Recipe() {
//    }

    // Updated constructor with videoUrl parameter
    public Recipe(String recipeId, String userId, String name, String cookingTime,
                  ArrayList<String> ingredients, ArrayList<String> instructions,
                  String imageUrl, String videoUrl) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.name = name;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;
        this.videoUrl = videoUrl; // Initialize videoUrl
    }

    public Recipe(String recipeId, String userId, String name, String cookingTime,
                  ArrayList<String> ingredients, ArrayList<String> instructions,
                  String imageUrl) {
        this.recipeId = recipeId;
        this.userId = userId;
        this.name = name;
        this.cookingTime = cookingTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUrl = imageUrl;// Initialize videoUrl
    }

    // Getters for all fields
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

    public String getVideoUrl() { // Add getter for videoUrl
        return videoUrl;
    }
}
