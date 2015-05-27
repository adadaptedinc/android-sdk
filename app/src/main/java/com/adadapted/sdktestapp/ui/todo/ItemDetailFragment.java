package com.adadapted.sdktestapp.ui.todo;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adadapted.android.sdk.ui.view.AAZoneView;
import com.adadapted.sdktestapp.R;
import com.adadapted.sdktestapp.core.todo.TodoItem;
import com.adadapted.sdktestapp.core.todo.TodoList;
import com.adadapted.sdktestapp.core.todo.TodoListManager;

import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {
    private static final String TAG = ItemDetailFragment.class.getName();

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TodoList list;
    private TodoItem item;

    private TextView nameTextView;
    private AAZoneView aaZoneView;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param listId Parameter 1.
     * @param itemId Parameter 2.
     * @return A new instance of fragment ItemDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance(UUID listId, UUID itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, listId);
        args.putSerializable(ARG_PARAM2, itemId);

        fragment.setArguments(args);

        return fragment;
    }

    public ItemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            UUID listId = (UUID)getArguments().getSerializable(ARG_PARAM1);
            UUID itemId = (UUID)getArguments().getSerializable(ARG_PARAM2);

            list = TodoListManager.getInstance(getActivity()).getList(listId);
            item = list.getItem(itemId);

            getActivity().setTitle(item.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_item_detail, container, false);

        nameTextView = (TextView)view.findViewById(R.id.fragment_item_detail_name_textView);
        nameTextView.setText(item.getName());

        aaZoneView = (AAZoneView)view.findViewById(R.id.fragment_item_detail_aa_zone);
        aaZoneView.setZoneLabel("ItemDetailFragment:"+item.getName());
        aaZoneView.init("10");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        aaZoneView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        aaZoneView.onStop();
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
