package com.adadapted.sdktestapp.ui.todo.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import com.adadapted.android.sdk.core.ad.AdContentListener;
import com.adadapted.android.sdk.core.atl.AddToListContent;
import com.adadapted.android.sdk.core.atl.AddToListItem;
import com.adadapted.android.sdk.core.view.AaZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;
import com.adadapted.sdktestapp.ui.todo.adapter.TodoListItemAdapter;
import com.adadapted.sdktestapp.ui.todo.dialog.NewListItemDialogFragment;

import java.util.List;
import java.util.UUID;

public class TodoListDetailFragment extends ListFragment implements AaZoneView.Listener, AdContentListener {
    private static final String TAG = TodoListDetailFragment.class.getName();

    private static final String ARG_LIST_ID = "listId";

    private UUID listId;
    private TodoList list;

    private TodoListItemAdapter adapter;
    private DialogFragment dialog;

    private AaZoneView aaZoneView;

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

        final Activity activity = getActivity();
        if(activity != null) {
            activity.setTitle(list.getName());
        }

        dialog = new NewListItemDialogFragment();
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called.");

        return inflater.inflate(R.layout.fragment_todo_list_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TodoListItemAdapter(getActivity(), list.getItems());
        getListView().setAdapter(adapter);

        aaZoneView = new AaZoneView(getActivity());
        aaZoneView.init("102110");
        aaZoneView.enableAdaptiveSizing(true);

        getListView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean isShown = aaZoneView.isShown(); //this determines if the adZoneView is actually visible on screen
                aaZoneView.setAdZoneVisibility(isShown); //set here for a more accurate representation of impressions
            }
        });

        getListView().addHeaderView(aaZoneView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflator) {
        super.onCreateOptionsMenu(menu, inflator);
    }

    public boolean onOptionsItemSelected (MenuItem item) {

        if(item.getItemId() == R.id.action_new_list_item) {
            dialog.show(getActivity().getSupportFragmentManager(), "NewListItemDialogFragment");
            return true;
        }
        if(item.getItemId() == R.id.action_settings) {
            return true;
        }
        if(item.getItemId() == R.id.zone_context_on) {
            aaZoneView.setAdZoneContextId("alex_recipe_id_1");
            return true;
        }
        if(item.getItemId() == R.id.zone_context_off) {
            aaZoneView.clearAdZoneContext();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
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
    public void onStart() {
        super.onStart();

        aaZoneView.onStart(this, this);
    }

    @Override
    public void onStop() {
        super.onStop();

        aaZoneView.onStop(this);
    }

    @Override
    public void onZoneHasAds(boolean hasAds) {
        Log.i(TAG, "Has Ads to serve:" + hasAds);
    }

    @Override
    public void onAdLoaded() {
        Log.i(TAG, "Ad Loaded.");

        Toast.makeText(getActivity(), "Ad Loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAdLoadFailed() {
        Log.i(TAG, "Ad Load FAILED.");

        Toast.makeText(getActivity(), "Ad Load FAILED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onContentAvailable(final String zoneId, final AddToListContent content) {
        //final Intent intent = new Intent(getActivity(), TodoListDetailFragment.class);
        //intent.putExtra("aa_atl_items", content);

        final List<AddToListItem> items = content.getItems();
        Log.i(TAG, "Received " + items.size() + " items.");

        for(final AddToListItem item : items) {
            Log.i(TAG, "Processing: " + item);
            TodoListManager.getInstance(getActivity()).addItemToList(listId, item.getTitle());
            content.itemAcknowledge(item);

            adapter.notifyDataSetChanged();
        }

        content.acknowledge();
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
