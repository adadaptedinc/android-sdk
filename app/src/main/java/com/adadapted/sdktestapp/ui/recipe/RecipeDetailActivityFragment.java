package com.adadapted.sdktestapp.ui.recipe;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adadapted.android.sdk.ui.view.AAZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.recipe.Recipe;
import com.adadapted.sdktestapp.core.recipe.RecipeManager;

import java.util.UUID;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeDetailActivityFragment extends Fragment implements RecipeManager.Listener {
    private static final String RECIPE_ID = "recipe_id";

    private UUID recipeId;
    private Recipe recipe;
    private AAZoneView aaZoneView;

    public static RecipeDetailActivityFragment newInstance(UUID recipeId) {
        RecipeDetailActivityFragment fragment = new RecipeDetailActivityFragment();

        Bundle args = new Bundle();
        args.putSerializable(RECIPE_ID, recipeId);

        fragment.setArguments(args);

        return fragment;
    }

    public RecipeDetailActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if(getArguments() != null) {
            recipeId = (UUID)getArguments().getSerializable(RECIPE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        aaZoneView = (AAZoneView)view.findViewById(R.id.recipeDetail_aaZoneView);
        aaZoneView.init("100671");

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecipeManager.getInstance(getActivity()).addListener(this);

        aaZoneView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();

        RecipeManager.getInstance(getActivity()).removeListener(this);

        aaZoneView.onStop();
    }

    @Override
    public void onRecipesAvailable() {
        recipe = RecipeManager.getInstance(getActivity()).getRecipe(recipeId);

        getActivity().setTitle(recipe.getName());

        aaZoneView.setZoneLabel("RecipeDetailActivityFragment:"+recipe.getName());
    }
}
