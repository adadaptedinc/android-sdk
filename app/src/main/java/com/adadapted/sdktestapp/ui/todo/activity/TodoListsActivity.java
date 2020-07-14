package com.adadapted.sdktestapp.ui.todo.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.ui.SpinnerActionBarActivity;
import com.adadapted.sdktestapp.ui.recipe.activity.RecipesActivity;
import com.adadapted.sdktestapp.ui.todo.fragment.TodoListsFragment;

public class TodoListsActivity extends SpinnerActionBarActivity
        implements TodoListsFragment.OnFragmentInteractionListener {
    private static final String TAG = TodoListsActivity.class.getName();

    @Override
    protected Fragment createFragment() {
        return TodoListsFragment.newInstance();
    }

    @Override
    protected void onCreate(Bundle savedsInstanceState) {
        super.onCreate(savedsInstanceState);

        setSelectedListItem("Lists");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_lists, menu);
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

        if (id == R.id.deeplink_test) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(this.getString(R.string.test_deeplink)));
            startActivity(browserIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    public boolean onNavigationItemSelected(int i, long l) {
        if(getMenuItem(i).equals("Recipes")) {
            final Intent intent = new Intent(this, RecipesActivity.class);
            startActivity(intent);

            return true;
        }

        return false;
    }
}
