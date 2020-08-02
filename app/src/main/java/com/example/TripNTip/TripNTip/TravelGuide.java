package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.Utils.Constants;
import com.example.TripNTip.WeatherAPI.WeatherAPIHandler;
import com.example.TripNTip.WeatherAPI.WeatherAPIResponse;

import java.util.Calendar;

public class TravelGuide implements Constants {
    /* An entity which provides information about trips.
     *
     * The Travel Guide provides the users information about the potential trips they might do.
     * In this assignment we implemented only the howMuchShouldITravelNowIn function, but the travel
     * guide class be easily extended and provide much more information and functionality.
     * */

    private String apiKey;
    private Context context;

    public TravelGuide(String apiKey, Context context) {
        this.apiKey = apiKey;
        this.context = context;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public int howMuchShouldITravelNowIn(Trip trip) {
        /* This function check on real-time how much should the user (in a scale of 1-10) travel
         * in the chosen trip.
         * The function uses the open weather api and decides by the following parameters:
         *
         * Temperature, humidity and current season,
         * Sunrise/sunset and current time.
         *
         * Each option gets a few points, which will accumulate to a 1-10 total grade.
         * */

        WeatherAPIHandler handler = new WeatherAPIHandler(apiKey, context);
        WeatherAPIResponse currentWeather = handler.handleWeatherAPIRequest(trip);

        waitForTimesToArrive(currentWeather);

        int grade = 0;
        grade += getSeasonAndTemperatureRecommendation(trip, currentWeather);
        grade += getTimeOfDayRecommendation(trip, currentWeather);
        return grade;
    }

    private void waitForTimesToArrive(WeatherAPIResponse currentWeather) {
        int counter = 0;
        int currentSunriseResult = 0;
        int currentSunsetResult = 0;
        while (counter < OPEN_WEATHER_API_WAIT_THRESHOLD || currentSunriseResult * currentSunsetResult == 0) {
            counter++;
            currentSunriseResult = currentWeather.getSunrise();
            currentSunsetResult = currentWeather.getSunset();
        }
    }

    private int getSeasonAndTemperatureRecommendation(Trip trip, WeatherAPIResponse currentWeather) {
        int seasonAndTempGrade = 0;
        double temperatureFeelsLike = currentWeather.getTemperatureFeelsLike();
        if (trip.getSummerTrip()) {
            if (temperatureFeelsLike > SUMMER_MINIMAL_TEMPERATURE && temperatureFeelsLike < SUMMER_MAXIMAL_TEMPERATURE)
                seasonAndTempGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else
                seasonAndTempGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            if (currentWeather.getHumidity() > MAXIMAL_HUMIDITY_VALUE)
                seasonAndTempGrade -= BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        } else if (temperatureFeelsLike < WINTER_MAXIMAL_TEMPERATURE && temperatureFeelsLike > WINTER_MINIMAL_TEMPERATURE) {
            seasonAndTempGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        } else
            seasonAndTempGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        return seasonAndTempGrade;
    }

    private int getTimeOfDayRecommendation(Trip trip, WeatherAPIResponse currentWeather) {
        int timeOfDayGrade = 0;
        int sunsetHour = currentWeather.getSunset();
        int sunriseHour = currentWeather.getSunrise();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (trip.getDayTrip()) {
            if (currentHour <= sunsetHour - TRIP_AVERAGE_DURATION)
                timeOfDayGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else
                timeOfDayGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        } else {
            if (currentHour > sunsetHour || currentHour < sunriseHour)
                timeOfDayGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else
                timeOfDayGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        }
        return timeOfDayGrade;
    }
}
