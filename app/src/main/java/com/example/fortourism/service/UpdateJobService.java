package com.example.fortourism.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.fortourism.util.HttpUtil;
import com.example.fortourism.util.Utility;
import com.example.fortourism.gson.AQI;
import com.example.fortourism.gson.Weather;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class UpdateJobService extends JobService {
    public boolean onStartJob(JobParameters params) {
        updateWeather();
        updateAQI();
        updateBingPic();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }

    private void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weathrString = prefs.getString("weather", null);
        if (weathrString != null) {
            Weather weather = Utility.handleHeAPIResponse(weathrString, Weather.class);
            String cityName = weather.basic.cityName;

            String queryUrl = "https://free-api.heweather.com/s6/weather?" +
                    "key=06f318727d9545b08493c25e69ef91bf&location=" + cityName;
            HttpUtil.sendOkHttpRequest(queryUrl).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather = Utility.handleHeAPIResponse(responseText, Weather.class);
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager
                                .getDefaultSharedPreferences(UpdateJobService.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                    }
                }
            });

        }
    }

    private void updateAQI() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weathrString = prefs.getString("weather", null);
        if (weathrString != null) {
            Weather weather = Utility.handleHeAPIResponse(weathrString, Weather.class);
            String cityName = weather.basic.cityName;

            String aqiUrl = "https://free-api.heweather.com/s6/air/now?" +
                    "key=06f318727d9545b08493c25e69ef91bf&location=" + cityName;
            HttpUtil.sendOkHttpRequest(aqiUrl).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String responseText = response.body().string();
                    final AQI aqi = Utility.handleHeAPIResponse(responseText, AQI.class);

                    if (aqi != null && "ok".equals(aqi.status)) {
                        SharedPreferences.Editor editor =
                                PreferenceManager.getDefaultSharedPreferences(UpdateJobService.this).edit();
                        editor.putString("aqi", responseText);
                        editor.apply();
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        String requestBingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPicUrl).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(UpdateJobService.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
            }
        });
    }
}
