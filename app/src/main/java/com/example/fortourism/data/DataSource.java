package com.example.fortourism.data;
import com.example.fortourism.gson.Weather;
import com.example.fortourism.gson.AQI;

public interface DataSource {
    interface LoadWeatherDataCallback {
        void onWeatherDataLoaded(Weather weather);
        void onWeatherDataNotAvailable();
    }

    interface LoadAQIDataCallback {
        void onAQIDataLoaded(AQI aqi);
        void onAQIDataNotAvailable();
    }

    void loadWeatherData(String city, LoadWeatherDataCallback callback);

    void loadAQIData(String city, LoadAQIDataCallback callback);

    void saveWeatherData(String city, String weatherString);

    void saveAQIData(String city, String aqiString);
}
