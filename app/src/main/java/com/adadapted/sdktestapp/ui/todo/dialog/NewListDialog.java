package com.adadapted.sdktestapp.ui.todo.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;


/**
 * Created by chrisweeden on 4/7/15.
 */
public class NewListDialog extends DialogFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = getActivity().getLayoutInflater();
    }
}
