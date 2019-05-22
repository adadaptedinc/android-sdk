package com.adadapted.sdktestapp.ui.todo.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.adadapted.android.sdk.ui.adapter.AutoCompleteAdapter;
import com.adadapted.sdktestapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chrisweeden on 6/25/15.
 */
public class NewListItemDialogFragment extends DialogFragment {
    private static final String TAG = NewListItemDialogFragment.class.getName();

    public interface NewListItemDialogListener {
        void onDialogPositiveClick(String userEntry);
        void onDialogNegativeClick();
    }

    // Use this instance of the interface to deliver action events
    NewListItemDialogListener mListener;
    AutoCompleteAdapter adapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d(TAG, "onCreateDialog() Called.");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Add New Item");

        String[] autoCompleteList = getResources().getStringArray(R.array.list_of_groceries);
        List<String> items = new ArrayList<>(Arrays.asList(autoCompleteList));

        adapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, items);

        final AutoCompleteTextView actv = new AutoCompleteTextView(getActivity());
        actv.setAdapter(adapter);
        actv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { }
        });
        builder.setView(actv);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick() Save Called.");
                String userEntry = actv.getText().toString();

                adapter.suggestionSelected(userEntry);

                mListener.onDialogPositiveClick(userEntry);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Log.d(TAG, "onClick() Cancel Called.");
                mListener.onDialogNegativeClick();
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NewListItemDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
