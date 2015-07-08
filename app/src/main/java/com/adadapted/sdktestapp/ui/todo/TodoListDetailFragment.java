package com.adadapted.sdktestapp.ui.todo;

import android.app.Activity;
import android.app.DialogFragment;
import android.support.v4.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.adadapted.android.sdk.AdAdapted;
import com.adadapted.android.sdk.core.content.ContentPayload;
import com.adadapted.android.sdk.ui.view.AaZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;

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
public class TodoListDetailFragment extends ListFragment implements AdAdapted.ContentListener {
    private static final String TAG = TodoListDetailFragment.class.getName();

    private static final String ARG_LIST_ID = "listId";

    private UUID listId;
    private TodoList list;

    private TodoListItemAdapter adapter;
    private AaZoneView aaZoneView;
    //private AaFeedAdapter feedAdapter;
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TodoListItemAdapter(getActivity(), list.getItems());
        //feedAdapter = new AaFeedAdapter(getActivity(), adapter, "100682", 3, 90, 8);
        //setListAdapter(feedAdapter);
        setListAdapter(adapter);

        aaZoneView = new AaZoneView(getActivity());
        aaZoneView.init("100682");
        ListView listView = getListView();
        listView.addFooterView(aaZoneView);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.w(TAG, "onResume() called.");

        AdAdapted.getInstance().addListener(this);
        //feedAdapter.onStart();
        aaZoneView.onStart();
    }

    @Override
    public void onPause() {
        Log.w(TAG, "onPause() called.");

        super.onPause();

        AdAdapted.getInstance().removeListener(this);
        //feedAdapter.onStop();
        aaZoneView.onStop();
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

        //Object item = feedAdapter.getItem(position);
        Object item = adapter.getItem(position);

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

                //feedAdapter.notifyDataSetChanged();
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
