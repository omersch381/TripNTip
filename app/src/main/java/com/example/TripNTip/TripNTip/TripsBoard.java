package com.example.TripNTip.TripNTip;

import com.example.TripNTip.Utils.Constants;

import java.util.HashMap;
import java.util.Iterator;

public class TripsBoard implements Constants {
    private Trip[] grid;
    private HashMap<String, Trip> trips;

    public TripsBoard(HashMap<String, Trip> trips) {
        this.trips = trips;
        this.grid = initiateBoard();
    }

    public Trip[] initiateBoard() {
        Trip[] board = new Trip[trips.size()];
        Iterator<Trip> iter = trips.values().iterator();
        for (int i = 0; i < trips.size(); i++)
            board[i] = iter.next();
        return board;
    }

    public int getBoardSize() {
        return trips.size();
    }

    public Trip getTrip(int position) {
        return grid[position];
    }

    public int getNumOfColumns() {
        return TRIPS_BOARD_NUM_OF_COLUMNS;
    }

    public Trip[] getGrid() {
        return grid;
    }
}
