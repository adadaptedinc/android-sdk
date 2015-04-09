package com.adadapted.sdktestapp.ui.recipe;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adadapted.sdktestapp.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeDetailActivityFragment extends Fragment {

    public RecipeDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipe_detail, container, false);
    }
}
