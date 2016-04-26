package com.adadapted.sdktestapp.ui.todo.activity;

import android.net.Uri;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.ui.SingleFragmentActivity;
import com.adadapted.sdktestapp.ui.todo.fragment.ItemDetailFragment;

import java.util.UUID;

public class ItemDetailActivity extends SingleFragmentActivity
        implements ItemDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = ItemDetailActivity.class.getName();

    public static final String TODO_LIST_ID = ItemDetailActivity.class.getName()  + ".TODO_LIST_ID";
    public static final String TODO_ITEM_ID = ItemDetailActivity.class.getName()  + ".TODO_ITEM_ID";

    @Override
    protected Fragment createFragment() {
        UUID listId = (UUID)getIntent().getSerializableExtra(TODO_LIST_ID);
        UUID itemId = (UUID)getIntent().getSerializableExtra(TODO_ITEM_ID);

        return ItemDetailFragment.newInstance(listId, itemId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_detail, menu);
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
    public void onFragmentInteraction(Uri uri) {

    }
}
