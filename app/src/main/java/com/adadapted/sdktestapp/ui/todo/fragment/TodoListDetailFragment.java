package com.adadapted.sdktestapp.ui.todo.fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.adadapted.android.sdk.ui.adapter.AaFeedAdapter;
import com.adadapted.android.sdk.ui.listener.AaContentListener;
import com.adadapted.android.sdk.ui.model.ContentPayload;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.adapter.TodoListItemAdapter;
import com.adadapted.sdktestapp.ui.todo.dialog.NewListItemDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TodoListDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodoListDetailFragment extends ListFragment implements AaContentListener {
    private static final String TAG = TodoListDetailFragment.class.getName();

    private static final String ARG_LIST_ID = "listId";

    private UUID listId;
    private TodoList list;

    private TodoListItemAdapter adapter;
    private AaFeedAdapter feedAdapter;
    private DialogFragment dialog;

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

    public TodoListDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called.");

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            listId = (UUID)getArguments().getSerializable(ARG_LIST_ID);
        }

        list = TodoListManager.getInstance(getActivity()).getList(listId);

        getActivity().setTitle(list.getName());

        dialog = new NewListItemDialogFragment();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called.");

        View view = inflater.inflate(R.layout.fragment_todo_list_detail, container, false);

        adapter = new TodoListItemAdapter(getActivity(), list.getItems());
        feedAdapter = new AaFeedAdapter(getActivity(), adapter, "100682", 3);
        setListAdapter(feedAdapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        feedAdapter.onStart(this);
    }

    @Override
    public void onPause() {
        super.onPause();

        feedAdapter.onStop(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator) {
        super.onCreateOptionsMenu(menu, inflator);
    }

    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_list_item:
                dialog.show(getActivity().getFragmentManager(), "NewListItemDialogFragment");
                return true;

            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Object item = feedAdapter.getItem(position);
        Log.d(TAG, "Feed Item Clicked: " + item);
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
    public void onContentAvailable(String zoneId, ContentPayload contentPayload) {
        try {
            JSONArray array = contentPayload.getPayload().getJSONArray("add_to_list_items");
            for(int i = 0; i < array.length(); i++) {
                String item = array.getString(i);
                TodoListManager.getInstance(getActivity()).addItemToList(listId, item);

                adapter.notifyDataSetChanged();
            }

            contentPayload.acknowledge();
        }
        catch(JSONException ex) {
            Log.w(TAG, "Problem parsing JSON.");
        }
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
}
