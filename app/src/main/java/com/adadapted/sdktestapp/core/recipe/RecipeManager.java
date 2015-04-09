package com.adadapted.sdktestapp.core.recipe;

/**
 * Created by chrisweeden on 4/6/15.
 */
public class RecipeManager {
    private static RecipeManager ourInstance = new RecipeManager();

    public static RecipeManager getInstance() {
        return ourInstance;
    }

    private RecipeManager() {
    }
}
