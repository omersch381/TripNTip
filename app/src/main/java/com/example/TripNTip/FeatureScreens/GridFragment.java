package com.example.TripNTip.FeatureScreens;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
    private GridView gridView;
    private String apiKey;
    private HashMap<String, Trip> trips;
    private ArrayList<Trip> filteredTrips;
    private HashMap<String, Bitmap> tripsAlbum;

    public GridFragment(String apiKey, HashMap<String, Bitmap> tripsAlbum) {
        this.tripsAlbum = tripsAlbum;
        this.apiKey = apiKey;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        trips = new HashMap<>();
        Bundle b = this.getArguments();
        assert b != null;
        if (b.getSerializable(TRIPS_LABEL) != null)
            trips = (HashMap<String, Trip>) b.getSerializable(TRIPS_LABEL);

        filteredTrips = new ArrayList<>();
        filteredTrips.addAll(trips.values());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.grid_fragment, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handleViews();
    }


    public void handleViews() {
        gridView = requireActivity().findViewById(R.id.TripsGrid);
        TripsBoard board = new TripsBoard(trips);
        tripAdapter = new TripAdapter(getContext(), board, tripsAlbum);

        gridView.setAdapter(tripAdapter);
        gridView.setNumColumns(board.getNumOfColumns());

        setOnClick();
    }

    private void setOnClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Trip selectedTrip = filteredTrips.get(index);
                //TODO Omer: use a constant instead of .png in all the project locations
                Bitmap bitmap = tripsAlbum.get(selectedTrip.getId() + ".png");
                showTrip(selectedTrip, bitmap);
            }
        });
    }

    private void showTrip(Trip trip, Bitmap tripBitmap) {
        TripDetailsFragment alertDialog = TripDetailsFragment.newInstance(trip, apiKey, getContext(), tripBitmap);
        alertDialog.show(getChildFragmentManager(), "");
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null && args.getString(QUERY_RECEIVED) != null)
            parseData(Objects.requireNonNull(args.getString(QUERY_RECEIVED)));
    }

    private void parseData(String string) {
        filteredTrips = new ArrayList<>();

        if (string.isEmpty())
            filteredTrips.addAll(trips.values());
        else
            for (Trip trip : trips.values())
                if (trip.getName().toLowerCase().contains((string.toLowerCase())))
                    filteredTrips.add(trip);

        tripAdapter.filter(filteredTrips);
        tripAdapter.notifyDataSetChanged();
    }
}