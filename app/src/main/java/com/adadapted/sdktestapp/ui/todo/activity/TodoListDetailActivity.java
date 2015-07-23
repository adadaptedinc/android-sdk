package com.adadapted.sdktestapp.ui.todo.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.SingleFragmentActivity;
import com.adadapted.sdktestapp.ui.todo.dialog.NewListItemDialogFragment;
import com.adadapted.sdktestapp.ui.todo.fragment.TodoListDetailFragment;

import java.util.UUID;

public class TodoListDetailActivity extends SingleFragmentActivity
        implements TodoListDetailFragment.OnFragmentInteractionListener,
        NewListItemDialogFragment.NewListItemDialogListener {
    private static final String TAG = TodoListDetailActivity.class.getName();

    public static final String TODO_LIST_ID = TodoListDetailActivity.class.getName() + ".TODO_LIST_ID";

    private UUID listId;

    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        listId = (UUID)intent.getSerializableExtra(TODO_LIST_ID);

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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}

    @Override
    public void onDialogPositiveClick(String userEntry) {
        Log.i(TAG, "User Entry: " + userEntry);

        if(listId != null) {
            TodoListManager.getInstance(this).addItemToList(listId, userEntry);
        }
    }

    @Override
    public void onDialogNegativeClick() {

    }
}
