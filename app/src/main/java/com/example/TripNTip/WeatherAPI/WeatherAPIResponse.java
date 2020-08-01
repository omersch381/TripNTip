package com.example.WeatherAPI;

import com.example.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class WeatherAPIResponse implements Constants {

    private int id;
    private String name;
    private double longitude;
    private double latitude;
    private String mainWeather;
    private String description;
    private double temperature;
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
            this.longitude = obj.getJSONObject("coord").getDouble("lon");
            this.latitude = obj.getJSONObject("coord").getDouble("lat");

            JSONArray weatherArr = obj.getJSONArray("weather");
            this.mainWeather = weatherArr.getJSONObject(0).getString("main");
            this.description = weatherArr.getJSONObject(0).getString("description");
            this.temperature = obj.getJSONObject("main").getDouble("temp") - TEMPERATURE_ABSOLUTE_ZERO;
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

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getMainWeather() {
        return mainWeather;
    }

    public String getDescription() {
        return description;
    }

    public double getTemperature() {
        return temperature;
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

