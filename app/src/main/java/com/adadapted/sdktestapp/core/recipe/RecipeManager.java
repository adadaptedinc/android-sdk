package com.adadapted.sdktestapp.core.recipe;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class RecipeManager {
    private static RecipeManager ourInstance;

    public static synchronized RecipeManager getInstance(Context context) {
        if(ourInstance == null) {
            ourInstance = new RecipeManager(context);
        }

        return ourInstance;
    }

    public interface Listener {
        void onRecipesAvailable();
    }

    private final Set<Listener> listeners;

    //private final Context context;
    private boolean isLoaded;
    private final List<Recipe> recipes;

    private RecipeManager(Context context) {
        //this.context = context;

        listeners = new HashSet<>();

        recipes = new ArrayList<>();
        recipes.add(new Recipe("Pumpkin Pie"));
        recipes.add(new Recipe("Apple Pie"));

        isLoaded = true;
        notifyOnRecipesAvailable();
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public Recipe getRecipe(UUID recipeId) {
        for(Recipe recipe : recipes) {
            if(recipe.getId().equals(recipeId)) {
                return recipe;
            }
        }

        return null;
    }

    public void addListener(Listener listener) {
        this.listeners.add(listener);

        if(isLoaded) {
            listener.onRecipesAvailable();
        }
    }

    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }

    public void notifyOnRecipesAvailable() {
        for(Listener listener : listeners) {
            listener.onRecipesAvailable();
        }
    }
}
