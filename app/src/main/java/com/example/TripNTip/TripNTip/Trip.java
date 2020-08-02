package com.example.TripNTip.TripNTip;

import java.io.Serializable;

public class Trip implements Serializable {

    private String name;
    private String description;
    private boolean summerTrip;
    private boolean dayTrip;
    private int id;
    private String location;

    public Trip(String name, String description, boolean summerTrip, boolean dayTrip, String location) {
        this.name = name;
        this.description = description;
        this.summerTrip = summerTrip;
        this.dayTrip = dayTrip;
        this.location = location;
    }

    public Trip() {

    }

    public boolean getSummerTrip() {
        return summerTrip;
    }

    public boolean getDayTrip() {
        return dayTrip;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }
}
