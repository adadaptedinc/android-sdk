package com.adadapted.sdktestapp.ui.todo;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.widget.ArrayAdapter;

import com.adadapted.sdktestapp.core.todo.TodoItem;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TodoListDetailFragment.OnFragmentInteractionListener} interface
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

        if (getArguments() != null) {
            listId = (UUID)getArguments().getSerializable(ARG_LIST_ID);
        }

        TodoListManager.getInstance(getActivity()).addListener(this);
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
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
