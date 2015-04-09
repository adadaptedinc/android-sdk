package com.adadapted.sdktestapp.ui.todo;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.ui.SingleFragmentActivity;

import java.util.UUID;

public class TodoListDetailActivity extends SingleFragmentActivity
        implements TodoListDetailFragment.OnFragmentInteractionListener{
    private static final String TAG = TodoListDetailActivity.class.getName();

    public static final String TODO_LIST_ID = TodoListDetailActivity.class.getName() + ".TODO_LIST_ID";

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        UUID listId = (UUID)intent.getSerializableExtra(TODO_LIST_ID);

        return TodoListDetailFragment.newInstance(listId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_new_list_item:
                Log.d(TAG, "New List Item menu item selected.");
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
