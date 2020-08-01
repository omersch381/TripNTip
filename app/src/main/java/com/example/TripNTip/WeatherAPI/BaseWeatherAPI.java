package com.example.WeatherAPI;

import java.util.HashMap;

public abstract class BaseWeatherAPI {
    /* Base Weather API class.
     *
     * */

    protected HashMap<String, Integer> cities_translator;

    protected Integer getID(String tripName) {
        return cities_translator.get(tripName);
    }
}
