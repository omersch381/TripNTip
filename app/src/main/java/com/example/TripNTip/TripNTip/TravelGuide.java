package com.example.TripNTip.TripNTip;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.R;
import com.example.TripNTip.Utils.Constants;
import com.example.TripNTip.WeatherAPI.TravelGuideRecommendation;
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
    private StringBuilder extendedRecommendation = new StringBuilder();
    private Resources res;

    public TravelGuide(String apiKey, Context context) {
        this.apiKey = apiKey;
        this.context = context;
        this.res = context.getResources();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public TravelGuideRecommendation howMuchShouldITravelNowIn(Trip trip) {
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

        grade = trip.getLocation().equals(PETAH_TIQWA) ? 1 : grade;
        extendedRecommendation = trip.getLocation().equals(PETAH_TIQWA) ? new StringBuilder().append(res.getString(R.string.dis_recommendation)) : extendedRecommendation;

        return new TravelGuideRecommendation(extendedRecommendation.toString(), grade);
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
        double temperatureFeelsLike = currentWeather.getTemperatureFeelsLike();

        boolean isSummerTrip = trip.getSummerTrip();
        boolean isSummerComfortableTemp = temperatureFeelsLike > SUMMER_MINIMAL_TEMPERATURE && temperatureFeelsLike < SUMMER_MAXIMAL_TEMPERATURE;
        boolean isSummerHumidityHigh = currentWeather.getHumidity() > MAXIMAL_HUMIDITY_VALUE;
        boolean isWinterComfortableTemp = temperatureFeelsLike < WINTER_MAXIMAL_TEMPERATURE && temperatureFeelsLike > WINTER_MINIMAL_TEMPERATURE;

        int seasonAndTempGrade = generateSeasonAndTemperatureGrade(isSummerTrip, isSummerComfortableTemp, isSummerHumidityHigh, isWinterComfortableTemp);
        generateSeasonAndTemperatureExtendedRecommendation(trip, temperatureFeelsLike, isSummerTrip, isSummerComfortableTemp, isSummerHumidityHigh, isWinterComfortableTemp);

        return seasonAndTempGrade;
    }

    private int generateSeasonAndTemperatureGrade(boolean isSummerTrip, boolean isSummerTempComfortable, boolean isSummerHumidityHigh, boolean isWinterTempComfortable) {
        int seasonAndTempGrade = 0;
        if (isSummerTrip) {
            if (isSummerTempComfortable)
                seasonAndTempGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else
                seasonAndTempGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;

            if (isSummerHumidityHigh)
                seasonAndTempGrade -= BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;

        } else if (isWinterTempComfortable) {
            seasonAndTempGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;

        } else // Which means it's winter with bad weather temperature
            seasonAndTempGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;

        return seasonAndTempGrade;
    }

    private void generateSeasonAndTemperatureExtendedRecommendation(Trip trip, double temperatureFeelsLike, boolean isSummerTrip, boolean isSummerTempComfortable, boolean isSummerHumidityHigh, boolean isWinterTempComfortable) {
        String tempString;
        int tempGrade;

        extendedRecommendation.append(res.getString(R.string.As)).append(trip.getName()).append(res.getString(R.string.is));
        tempString = isSummerTrip ? "" : res.getString(R.string.not);
        extendedRecommendation.append(tempString).append(res.getString(R.string.summer_trip_and));
        tempString = isSummerTempComfortable || isWinterTempComfortable ? "" : res.getString(R.string.not);
        extendedRecommendation.append(tempString).append(res.getString(R.string.comfortable)).append(" (").append((int) temperatureFeelsLike).append("), ").append(res.getString(R.string.we_decided_to_add));
        tempGrade = isSummerTempComfortable || isWinterTempComfortable ? GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW : BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        extendedRecommendation.append(tempGrade).append(res.getString(R.string.to_the_grade)).append("\n");
        if (isSummerHumidityHigh)
            extendedRecommendation.append(res.getString(R.string.as_the_humidity)).append(GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW).append(res.getString(R.string.from_the_grade)).append("\n");
    }

    private int getTimeOfDayRecommendation(Trip trip, WeatherAPIResponse currentWeather) {
        int sunsetHour = currentWeather.getSunset();
        int sunriseHour = currentWeather.getSunrise();
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        boolean isDayTrip = trip.getDayTrip();
        boolean isThereEnoughTimeToTravelDayTrip = currentHour <= sunsetHour - TRIP_AVERAGE_DURATION;
        boolean isThereEnoughTimeToTravelNightTrip = currentHour > sunsetHour || currentHour <= sunriseHour - TRIP_AVERAGE_DURATION;
        boolean isThereEnoughTimeNow = (isDayTrip && isThereEnoughTimeToTravelDayTrip) || (!isDayTrip && isThereEnoughTimeToTravelNightTrip);

        int timeOfDayGrade = generateTimeOfDayGrade(isDayTrip, isThereEnoughTimeToTravelDayTrip, isThereEnoughTimeToTravelNightTrip);
        generateTimeOfDayExtendedRecommendation(trip, isDayTrip, isThereEnoughTimeNow);

        return timeOfDayGrade;
    }

        private int generateTimeOfDayGrade(boolean isDayTrip, boolean isThereEnoughTimeToTravelDayTrip, boolean isThereEnoughTimeToTravelNightTrip) {
        int timeOfDayGrade = 0;

        if (isDayTrip) {
            if (isThereEnoughTimeToTravelDayTrip)
                timeOfDayGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else // It is a day trip, without enough time to do the trip
                timeOfDayGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        } else { // Not a day trip == a night trip
            if (isThereEnoughTimeToTravelNightTrip)
                timeOfDayGrade += GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
            else // Night trip, without enough time to do the trip
                timeOfDayGrade += BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW;
        }
        return timeOfDayGrade;
    }

    private void generateTimeOfDayExtendedRecommendation(Trip trip, boolean isDayTrip, boolean isThereEnoughTimeNow) {
        extendedRecommendation.append(res.getString(R.string.As)).append(trip.getName()).append(res.getString(R.string.is));
        extendedRecommendation.append(isDayTrip ? "" : res.getString(R.string.not));
        extendedRecommendation.append(res.getString(R.string.a_day_trip));
        extendedRecommendation.append(isThereEnoughTimeNow ? "" : res.getString(R.string.not));
        extendedRecommendation.append(res.getString(R.string.enough_time));
        extendedRecommendation.append(isThereEnoughTimeNow ? GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW : BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW);
        extendedRecommendation.append(res.getString(R.string.to_the_grade)).append("\n");
    }
}
