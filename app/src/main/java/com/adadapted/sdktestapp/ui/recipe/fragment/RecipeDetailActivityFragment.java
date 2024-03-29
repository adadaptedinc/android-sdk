package com.adadapted.sdktestapp.ui.recipe.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.adadapted.android.sdk.core.view.AaZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.recipe.Recipe;
import com.adadapted.sdktestapp.core.recipe.RecipeManager;

import java.util.UUID;

public class RecipeDetailActivityFragment extends Fragment implements AaZoneView.Listener, RecipeManager.Listener {
    private static final String TAG = RecipeDetailActivityFragment.class.getName();

    private static final String RECIPE_ID = "recipe_id";

    private UUID recipeId;
    private Recipe recipe;
    private AaZoneView aaZoneView;

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

//        aaZoneView = (AaZoneView)view.findViewById(R.id.recipeDetail_aaZoneView);
//        aaZoneView.init("102110"); //102110

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        RecipeManager.getInstance(getActivity()).addListener(this);

        aaZoneView.onStart(this, null);
    }

    @Override
    public void onPause() {
        super.onPause();

        RecipeManager.getInstance(getActivity()).removeListener(this);

        aaZoneView.onStop(null);
    }

    @Override
    public void onRecipesAvailable() {
        recipe = RecipeManager.getInstance(getActivity()).getRecipe(recipeId);

        getActivity().setTitle(recipe.getName());
    }

    @Override
    public void onZoneHasAds(boolean hasAds) {
        Log.i(TAG, "Has Ads to serve:" + hasAds);
    }

    @Override
    public void onAdLoaded() {
        Log.i(TAG, "Ad Loaded.");

        Toast.makeText(getActivity(), "Ad Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoadFailed() {
        Log.i(TAG, "Ad Load FAILED.");

        Toast.makeText(getActivity(), "Ad Load FAILED", Toast.LENGTH_SHORT).show();
    }
}
