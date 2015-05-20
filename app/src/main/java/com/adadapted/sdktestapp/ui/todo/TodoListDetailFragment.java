package com.adadapted.sdktestapp.ui.todo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.support.v4.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

import com.adadapted.android.sdk.ui.AAZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoItem;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoListDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoListDetailFragment extends ListFragment
        implements TodoListManager.Listener {
    private static final String TAG = TodoListDetailFragment.class.getName();

    private static final String ARG_LIST_ID = "listId";

    private UUID listId;
    private TodoList list;

    private AAZoneView aaZoneView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listId Parameter 1.
     * @return A new instance of fragment TodoListDetailFragment.
     */
    public static TodoListDetailFragment newInstance(UUID listId) {
        TodoListDetailFragment fragment = new TodoListDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_LIST_ID, listId);

        fragment.setArguments(args);

        return fragment;
    }

    public TodoListDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            listId = (UUID)getArguments().getSerializable(ARG_LIST_ID);
        }

        aaZoneView = new AAZoneView(getActivity());
        aaZoneView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 120));
        aaZoneView.init("100670");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator) {
        super.onCreateOptionsMenu(menu, inflator);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_list_item:
                DialogFragment dialog = new NewListItemDialogFragment();
                dialog.show(getActivity().getFragmentManager(), "NewListItemDialogFragment");
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onTodoListsAvailable() {
        list = TodoListManager.getInstance(getActivity()).getList(listId);

        getActivity().setTitle(list.getName());

        ArrayAdapter<TodoItem> adapter = new ArrayAdapter<TodoItem>(getActivity(),
                android.R.layout.simple_list_item_1, list.getItems());
        setListAdapter(adapter);

        aaZoneView.setZoneLabel("TodoListDetailFragment:" + list.getName());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public static class NewListItemDialogFragment extends DialogFragment {
        public interface NewListItemDialogListener {
            void onDialogPositiveClick(DialogFragment dialog);
            void onDialogNegativeClick(DialogFragment dialog);
        }

        // Use this instance of the interface to deliver action events
        NewListItemDialogListener mListener;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Fire Missiles?")
                    .setPositiveButton("Fire", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogPositiveClick(NewListItemDialogFragment.this);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            mListener.onDialogNegativeClick(NewListItemDialogFragment.this);
                        }
                    });
            // Create the AlertDialog object and return it
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
}
