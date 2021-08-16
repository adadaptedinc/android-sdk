package com.adadapted.sdktestapp.ui.recipe.activity;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;

import com.adadapted.sdktestapp.ui.SingleFragmentActivity;
import com.adadapted.sdktestapp.ui.recipe.fragment.RecipeDetailActivityFragment;

import java.util.UUID;

public class RecipeDetailActivity extends SingleFragmentActivity {
    public static final String RECIPE_ID = RecipeDetailActivity.class.getName() + ".RECIPE_ID";

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        UUID recipeId = (UUID)intent.getSerializableExtra(RECIPE_ID);

        return RecipeDetailActivityFragment.newInstance(recipeId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
