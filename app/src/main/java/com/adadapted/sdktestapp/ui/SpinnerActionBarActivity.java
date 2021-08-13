package com.adadapted.sdktestapp.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import androidx.appcompat.app.ActionBar;

import com.adadapted.sdktestapp.R;

public abstract class SpinnerActionBarActivity extends SingleFragmentActivity
        implements ActionBar.OnNavigationListener {
    protected String [] menuItems;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        menuItems = getResources().getStringArray(R.array.action_list);

        actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        }

        SpinnerAdapter menuSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list,
                android.R.layout.simple_spinner_dropdown_item);
        if(menuSpinnerAdapter != null) {
            actionBar.setListNavigationCallbacks(menuSpinnerAdapter, this);
        }
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
