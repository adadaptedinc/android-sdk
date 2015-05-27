package com.adadapted.sdktestapp.ui.recipe;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.ui.SpinnerActionBarActivity;
import com.adadapted.sdktestapp.ui.todo.TodoListsActivity;

public class RecipesActivity extends SpinnerActionBarActivity
        implements RecipesFragment.OnFragmentInteractionListener {
    @Override
    protected Fragment createFragment() {
        return new RecipesFragment();
    }

    @Override
    protected void onCreate(Bundle savedsInstanceState) {
        super.onCreate(savedsInstanceState);

        setSelectedListItem("Recipes");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        if(getMenuItem(i).equals("Lists")) {
            Intent intent = new Intent(this, TodoListsActivity.class);
            startActivity(intent);

            return true;
        }

        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
