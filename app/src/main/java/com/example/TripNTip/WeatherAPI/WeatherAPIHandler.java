package com.example.TripNTip.WeatherAPI;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.example.TripNTip.R;
import com.example.TripNTip.TripNTip.Trip;
import com.example.TripNTip.Utils.Constants;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

    public class WeatherAPIHandler implements Constants {

    private String apiKey;
    private WeatherAPIResponse myResponse;
    private BaseWeatherAPI apiHandler;
    private Context context;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public WeatherAPIHandler(String apiKey, Context context) {
        this.apiKey = apiKey;
        this.context = context;
        this.apiHandler = getApiHandler(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private BaseWeatherAPI getApiHandler(Context context) {
        return new IsraeliWeatherAPIHandler(context);
    }

    public WeatherAPIResponse handleWeatherAPIRequest(final Trip trip) {
        myResponse = new WeatherAPIResponse();

        final URL url = generateUrlRequest(trip);

        sendAPIRequest(url);

        return myResponse;
    }


    private URL generateUrlRequest(final Trip trip) {
        int tripId = apiHandler.getID(trip.getLocation());
        URL url = null;

        try {
            //todo omer!use constant
            url = new URL("https://api.openweathermap.org/data/2.5/weather?id=" + tripId + "&appid=" + apiKey);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    private void sendAPIRequest(final URL url) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection urlConnection;
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    if (urlConnection.getResponseCode() == SUCCESS) //200
                        handleSuccessfulResponse(urlConnection, context);

                    else if (urlConnection.getResponseCode() == UNAUTHORIZED) { //401 - Invalid API key
                        Toast.makeText(context, context.getResources().getString(R.string.invalidAPIKeyErrorMessage), Toast.LENGTH_LONG).show();
                        System.exit(2);
                    } else if (urlConnection.getResponseCode() == TOO_MANY_REQUESTS) { //429
                        Toast.makeText(context, context.getResources().getString(R.string.tooManyRequestsErrorMessage), Toast.LENGTH_LONG).show();
                        System.exit(2);
                    } else { // Bad response from server
                        Toast.makeText(context, context.getResources().getString(R.string.generalErrorMessage), Toast.LENGTH_LONG).show();
                        System.exit(2);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleSuccessfulResponse(HttpURLConnection urlConnection, Context context) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line, response;

        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append("\n");
        }
        //todo omer check the static method
        response = stringBuilder.toString();
        myResponse.parseWeather(response);
        close(reader, context);
        urlConnection.disconnect();
    }

    private void close(Closeable x, Context context) {
        try {
            if (x != null) {
                x.close();
            }
        } catch (IOException e) {
            Toast.makeText(context, context.getResources().getString(R.string.streamClosingErrorMessage), Toast.LENGTH_LONG).show();
        }
    }
}
