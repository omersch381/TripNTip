package com.example.TripNTip.WeatherAPI;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

public class IsraeliWeatherAPIHandler extends BaseWeatherAPI implements Constants {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public IsraeliWeatherAPIHandler(Context context) {
        this.cities_translator = new HashMap<>();

        try {
            loadCities(context);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadCities(Context context) throws JSONException {
        JSONObject json = new JSONObject(Objects.requireNonNull(loadJSONFromAsset(context)));
        JSONArray jArray = json.getJSONArray("israeliCities");
        cities = new String[jArray.length()];

        for (int i = 0; i < jArray.length(); i++) {
            JSONObject json_data = jArray.getJSONObject(i);
            cities_translator.put(json_data.getString("name"), json_data.getInt("id"));
            cities[i] = json_data.getString("name");
        }
    }

    private String loadJSONFromAsset(Context context) {
        String json;
        try {
            InputStream is = context.getAssets().open(ISRAELI_CITIES_JSON_FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public Integer getID(String tripName) {
        return super.getID(tripName);
    }

    @Override
    public String[] getCities() {
        return cities;
    }
}
