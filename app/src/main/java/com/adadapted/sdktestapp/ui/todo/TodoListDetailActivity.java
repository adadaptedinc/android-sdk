package com.adadapted.sdktestapp.ui.todo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {}
}
