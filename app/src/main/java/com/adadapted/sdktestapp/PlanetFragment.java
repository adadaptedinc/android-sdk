package com.adadapted.sdktestapp;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adadapted.android.sdk.AAZoneView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlanetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PlanetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlanetFragment extends Fragment {
    private static final String TAG = PlanetFragment.class.getName();

    public static final String ARG_PLANET_NUMBER = "com.adadapted.sdktestapp.ARG_PLANET_NUMBER";

    private String[] mPlanetTitles;
    private int planetNumber;

    private OnFragmentInteractionListener mListener;

    private AAZoneView zoneView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlanetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlanetFragment newInstance() {
        Bundle args = new Bundle();

        PlanetFragment fragment = new PlanetFragment();
        fragment.setArguments(args);

        return fragment;
    }

    public PlanetFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_planet, container, false);

        mPlanetTitles = getResources().getStringArray(R.array.planets_array);

        Bundle args = getArguments();
        planetNumber = args.getInt(ARG_PLANET_NUMBER);

        TextView planetName = (TextView) view.findViewById(R.id.planet_name);
        planetName.setText(mPlanetTitles[planetNumber]);

        zoneView = (AAZoneView)view.findViewById(R.id.fragment_planet_zone);
        zoneView.setZoneLabel(mPlanetTitles[planetNumber]);
        zoneView.init("10");

        return view;
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
    public void onStart() {
        super.onStart();

        zoneView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();

        zoneView.onStop();
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
