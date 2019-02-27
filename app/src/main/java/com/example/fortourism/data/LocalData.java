package com.example.fortourism.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.example.fortourism.util.Utility;
import com.example.fortourism.gson.Weather;

public class LocalData implements DataSource{
    private Context mCtx;

    public LocalData(Context context) {
        mCtx = context;
    }

    @Override
    public void loadWeatherData(String city, LoadWeatherDataCallback callback) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.toObject(weatherString, Weather.class);
            if (weather != null && TextUtils.isEmpty(city)) {
                callback.onWeatherDataLoaded(weather);
            } else if (weather != null && city.equals(weather.basic.cityName)) {
                callback.onWeatherDataLoaded(weather);
            } else {
                callback.onWeatherDataNotAvailable();
            }
        } else {
            callback.onWeatherDataNotAvailable();
        }
    }

    @Override
    public void loadAQIData(String city, LoadAQIDataCallback callback) {

    }

    @Override
    public void saveWeatherData(String city, String weatherString) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mCtx).edit();
        editor.putString("weather", weatherString);
        editor.apply();
    }

    @Override
    public void saveAQIData(String city, String aqiString) {

    }
}
