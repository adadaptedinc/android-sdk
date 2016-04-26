package com.adadapted.sdktestapp.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.adadapted.sdktestapp.R;

/**
 * Created by chrisweeden on 4/8/15.
 */
public abstract class SpinnerActionBarActivity extends SingleFragmentActivity
        implements ActionBar.OnNavigationListener {
    protected String [] menuItems;

    private ActionBar actionBar;
    private SpinnerAdapter menuSpinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems = getResources().getStringArray(R.array.action_list);

        actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        menuSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);

        actionBar.setListNavigationCallbacks(menuSpinnerAdapter, this);
    }

    protected String getMenuItem(int i) {
        return menuItems[i];
    }

    protected void setSelectedListItem(String listItem) {
        for(int i = 0; i < menuItems.length; i++) {
            if(menuItems[i].equals(listItem)) {
                actionBar.setSelectedNavigationItem(i);
            }
        }
    }

    public abstract boolean onNavigationItemSelected(int i, long l);
}
