package com.example.TripNTip.FeatureScreens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.TripNTip.TripAdapter;
import com.example.TripNTip.TripNTip.TripsBoard;
import com.example.TripNTip.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


public class GridFragment extends Fragment implements Constants {
    public final static String QUERY_RECEIVED = "";
    private TripAdapter tripAdapter;
    private TripsBoard board;
    private GridView gridView;
    private String apiKey;
    private HashMap<String, Trip> trips;

    public GridFragment(String apiKey) {
        this.apiKey = apiKey;
    }

    public GridFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trips = new HashMap<>();
        Bundle b = this.getArguments();
        assert b != null;
        if (b.getSerializable("trips") != null)
            trips = (HashMap<String, Trip>) b.getSerializable(TRIPS_KEYWORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.grid_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gridView = requireActivity().findViewById(R.id.TripsGrid);
        board = new TripsBoard(trips);
        tripAdapter = new TripAdapter(getContext(), board);

        gridView.setAdapter(tripAdapter);
        gridView.setNumColumns(board.getNumOfColumns());

        setOnClick();
    }

    private void setOnClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                showTrip(board.getTrip(index));
            }
        });
    }

    private void showTrip(Trip trip) {
        TripDetailsFragment alertDialog = TripDetailsFragment.newInstance("Trip information", trip, apiKey, getContext());
        alertDialog.show(getChildFragmentManager(), "");
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null && args.getString(QUERY_RECEIVED) != null)
            parseData(Objects.requireNonNull(args.getString(QUERY_RECEIVED)));
    }

    private void parseData(String string) {
        ArrayList<Trip> filteredTrips = new ArrayList<>();
        if (string.isEmpty())
            filteredTrips.addAll(trips.values());
        else
            for (Trip trip : trips.values())
                if (trip.getName().equals(string))
                    filteredTrips.add(trip);
        tripAdapter.filter(filteredTrips);
        tripAdapter.notifyDataSetChanged();
    }
}