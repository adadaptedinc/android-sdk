package com.adadapted.demo.baskethandle.app.common;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.adadapted.demo.baskethandle.app.list.ListOverviewFragment;
import com.adadapted.demo.baskethandle.app.recipe.RecipeListFragment;

/**
 * Created by chrisweeden on 10/4/16.
 */

public class BasketHandlePagerAdapter extends FragmentPagerAdapter {
    private final RecipeListFragment recipeListFragment;
    private final ListOverviewFragment listFragment;

    public BasketHandlePagerAdapter(FragmentManager fm) {
        super(fm);

        recipeListFragment = RecipeListFragment.newInstance();
        listFragment = ListOverviewFragment.newInstance();
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return recipeListFragment;

            case 1:
                return listFragment;
        }

        return recipeListFragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch(position) {
            case 0:
                return "Recipes";

            case 1:
                return "Shopping List";
        }

        return "";
    }

    @Override
    public int getCount() {
        return 2;
    }
}
