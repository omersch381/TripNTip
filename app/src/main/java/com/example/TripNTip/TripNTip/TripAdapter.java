package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;

import androidx.annotation.RequiresApi;
import androidx.core.view.ViewCompat;

import com.example.TripNTip.Utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class TripAdapter extends BaseAdapter implements Constants {

    private TripsBoard board;
    private Context myContext;
    private ArrayList<Trip> filteredTrips;
    private HashMap<String, Bitmap> tripsAlbum;

    public TripAdapter(Context myContext, TripsBoard board, HashMap<String, Bitmap> tripsAlbum) {
        this.board = board;
        this.myContext = myContext;
        this.tripsAlbum = tripsAlbum;
        filteredTrips = new ArrayList<>();
        initializeTrips();
    }

    private void initializeTrips() {
        for (int i = 0; i < board.getBoardSize(); i++)
            filteredTrips.add(board.getTrip(i));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TripView tripView = (TripView) convertView;
        if (tripView == null)
            tripView = new TripView(myContext);

        final Trip trip = filteredTrips.get(position);

        tripView = getASquaredTile(tripView);

        setTripViewBackground(tripView, trip);

        tripView.setText(trip.getName());

        return tripView;
    }

    private void setTripViewBackground(TripView tripView, Trip trip) {
        //TODO Niv: make sure we add the pictures in the same way the set them in this method.
        BitmapDrawable background = new BitmapDrawable(myContext.getResources(), tripsAlbum.get(trip.getId() + ".png"));
        ViewCompat.setBackground(tripView, background);
    }

    public void filter(ArrayList<Trip> filteredTrips) {
        setFilteredTrips(filteredTrips);
    }

    public void setFilteredTrips(ArrayList<Trip> filteredTrips) {
        this.filteredTrips = filteredTrips;
    }

    @Override
    public int getCount() {
        return filteredTrips.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredTrips.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private TripView getASquaredTile(View convertView) {
        final TripView tripView;
        if (convertView == null)
            tripView = new TripView(myContext);
        else
            tripView = (TripView) convertView;

        tripView.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        tripView.getViewTreeObserver().removeOnPreDrawListener(this);
                        tripView.setLayoutParams(new GridView.LayoutParams(tripView.getWidth(), tripView.getWidth()));
                        return false;
                    }
                }
        );
        return tripView;
    }
}
