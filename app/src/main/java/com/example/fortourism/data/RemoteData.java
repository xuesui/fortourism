package com.example.fortourism.data;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.fortourism.gson.Weather;
import com.example.fortourism.gson.AQI;
import com.example.fortourism.util.Utility;
import com.example.fortourism.util.HttpUtil;

public class RemoteData implements DataSource{
    private static final String WEATHER_URL = "https://free-api.heweather.com/s6/weather?" +
            "key=06f318727d9545b08493c25e69ef91bf&location=";
    private static final String AQI_URL = "https://free-api.heweather.com/s6/air/now?" +
            "key=06f318727d9545b08493c25e69ef91bf&location=";

    public RemoteData() {
    }

    @Override
    public void loadWeatherData(String city, LoadWeatherDataCallback callback) {
        requestWeather(city, callback);
    }

    @Override
    public void loadAQIData(String city, LoadAQIDataCallback callback) {
        requestAQI(city, callback);
    }

    @Override
    public void saveWeatherData(String city, String weatherString) {
        // current do nothing here
    }

    @Override
    public void saveAQIData(String city, String aqiString) {
        // current do nothing here
    }

    public void requestWeather(final String countyName, final LoadWeatherDataCallback callback) {
        String weatherUrl =  WEATHER_URL + countyName;
        HttpUtil.sendOkHttpRequest(weatherUrl).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onWeatherDataNotAvailable();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleHeAPIResponse(responseText, Weather.class);
                callback.onWeatherDataLoaded(weather);
            }
        });
    }

    public void requestAQI(final String countyName, final LoadAQIDataCallback callback) {
        String aqiUrl = AQI_URL + countyName;
        HttpUtil.sendOkHttpRequest(aqiUrl).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onAQIDataNotAvailable();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final AQI aqi = Utility.handleHeAPIResponse(responseText, AQI.class);
                callback.onAQIDataLoaded(aqi);
            }
        });
    }
}
