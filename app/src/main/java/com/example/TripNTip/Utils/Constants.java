package com.example.TripNTip.Utils;

public interface Constants {
    // Sign up error messages
    int EMPTY_EMAIL_MESSAGE = 1;
    int INVALID_EMAIL_MESSAGE = 2;
    int EMPTY_PASSWORD_MESSAGE = 3;
    int INVALID_PASSWORD_LENGTH_MESSAGE = 4;
    int INVALID_PASSWORD_CASE_MESSAGE = 5;
    int PASSWORD_CONTAINS_USERNAME_MESSAGE = 6;

    // Sign up password constants
    int PASSWORD_MINIMAL_LENGTH = 8;
    int PASSWORD_MAXIMAL_LENGTH = 14;

    // Trip Constants
    int SUMMER_MINIMAL_TEMPERATURE = 15;
    int SUMMER_MAXIMAL_TEMPERATURE = 32;
    int WINTER_MAXIMAL_TEMPERATURE = 20;
    int WINTER_MINIMAL_TEMPERATURE = 5;
    int MAXIMAL_HUMIDITY_VALUE = 80;
    int GOOD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW = 5;
    int BAD_CONDITIONS_FOR_TRAVELLING_RIGHT_NOW = 2;
    int TRIP_AVERAGE_DURATION = 3;

    // TripsBoard properties
    int TRIPS_BOARD_NUM_OF_COLUMNS = 3;

    // Trip Pictures properties
    long ONE_MEGABYTE = 1024 * 1024;

    // General Utils
    double TEMPERATURE_ABSOLUTE_ZERO = 273.15;
    int MILLISECOND_MEASURE = 1000;
    int OPEN_WEATHER_API_WAIT_THRESHOLD = 100000;

    // Fragments data pass keywords
    String ADD_ACTION = "add";
    String REPLACE_ACTION = "replace";

    // OpenWeatherAPI JSON loading properties
    String ISRAELI_CITIES_JSON_FILE_NAME = "IL_cities.json";

    // API response codes
    int SUCCESS = 200;
    int UNAUTHORIZED = 401;
    int TOO_MANY_REQUESTS = 429;

    // API properties
    String SHOULD_WE_LOAD_THE_TRIPS = "shouldLoadTrips";
    String SHOULD_WE_LOAD_THE_API_KEY = "shouldLoadApiKey";
    String API_KEY_LABEL = "apiKey";
    String TRIPS_LABEL = "trips";
    String TRIPS_ALBUM_LABEL = "tripsAlbum";

    // Lame Cities
    String PETAH_TIQWA = "Petah Tiqwa";

    // AddTrip constants
    String TRIP_CREATION_SUCCEED = "success";

   //for profile activity
    final String WAIT = "Please wait...";
    final String USER = "users";
    final String USERNAME = "username";
    final String EMAIL = "email";
    final String IMEGES = "email";
}
