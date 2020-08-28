package com.example.TripNTip.WeatherAPI;

import com.example.TripNTip.Utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class WeatherAPIResponse implements Constants {

    /**
     * An entity that parses and gathers all the required data for the trips' weather.
     */

    private int id;
    private String name;
    private double temperatureFeelsLike;
    private double humidity;
    private int sunriseHour;
    private int sunsetHour;

    public WeatherAPIResponse() {
    }

    public void parseWeather(String response) {
        JSONObject obj;
        try {
            obj = new JSONObject(response);
            this.id = obj.getInt("id");
            this.name = obj.getString("name");

            this.temperatureFeelsLike = obj.getJSONObject("main").getDouble("feels_like") - TEMPERATURE_ABSOLUTE_ZERO;
            this.humidity = obj.getJSONObject("main").getDouble("humidity");

            Calendar calendarInstance = Calendar.getInstance();
            long sunriseTimestamp = obj.getJSONObject("sys").getLong("sunrise");
            calendarInstance.setTimeInMillis(sunriseTimestamp * MILLISECOND_MEASURE);
            this.sunriseHour = calendarInstance.get(Calendar.HOUR_OF_DAY);

            long sunsetTimestamp = obj.getJSONObject("sys").getLong("sunset");
            calendarInstance.setTimeInMillis(sunsetTimestamp * MILLISECOND_MEASURE);
            this.sunsetHour = calendarInstance.get(Calendar.HOUR_OF_DAY);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getTemperatureFeelsLike() {
        return temperatureFeelsLike;
    }

    public double getHumidity() {
        return humidity;
    }

    public int getSunrise() {
        return sunriseHour;
    }

    public int getSunset() {
        return sunsetHour;
    }
}

