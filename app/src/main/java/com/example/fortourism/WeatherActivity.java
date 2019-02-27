package com.example.fortourism;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fortourism.data.DataRepository;
import com.example.fortourism.data.DataSource;
import com.example.fortourism.data.LocalData;
import com.example.fortourism.data.RemoteData;
import com.example.fortourism.gson.AQI;
import com.example.fortourism.gson.Forecast;
import com.example.fortourism.gson.Suggestion;
import com.example.fortourism.gson.Weather;
import com.example.fortourism.service.UpdateJobService;
import com.example.fortourism.util.HttpUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherScrollView;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private LinearLayout suggestionLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private ImageView bingImg;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    private String mCurrentCityName;

    public DrawerLayout mDrawerLayout;
    private Button navButton;
    private DataRepository mDataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        mDataRepository = DataRepository.getInstance(new RemoteData(), new LocalData(this));
        mCurrentCityName = getIntent().getStringExtra("county_name");

        weatherScrollView = findViewById(R.id.sv_weather);
        titleCity = findViewById(R.id.tv_title_city);
        titleUpdateTime = findViewById(R.id.tv_title_update_time);
        degreeText = findViewById(R.id.tv_degree);
        weatherInfoText = findViewById(R.id.tv_weather_info);
        forecastLayout = findViewById(R.id.layout_forecast);
        suggestionLayout = findViewById(R.id.layout_suggestion);
        aqiText = findViewById(R.id.tv_aqi);
        pm25Text = findViewById(R.id.tv_pm25);
        bingImg = findViewById(R.id.img_bing);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.btn_nav);


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mDataRepository != null) {
                    mDataRepository.loadWeatherDataFromRemoteSource(mCurrentCityName, mLoadWeatherDataCallback);
                    mDataRepository.loadAQIDataFromRemoteSource(mCurrentCityName, mLoadAQIDataCallback);
                }
            }
        });

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        loadAllData(mCurrentCityName);



        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPicUrl = prefs.getString("bing_pic", null);
        if (bingPicUrl != null) {
            Glide.with(this).load(bingPicUrl).into(bingImg);
        } else {
            loadBingPicPathFromGuolinAPI();
        }

    }

    public void loadAllData(String cityName) {
        mCurrentCityName = cityName;
        mDataRepository.loadWeatherData(cityName, mLoadWeatherDataCallback);
        mDataRepository.loadAQIData(cityName, mLoadAQIDataCallback);
    }

    final DataSource.LoadWeatherDataCallback mLoadWeatherDataCallback = new DataSource.LoadWeatherDataCallback() {
        @Override
        public void onWeatherDataLoaded(final Weather weather) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (weather != null && "ok".equals(weather.status)) {
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this,
                                "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);
                    loadBingPicPathFromGuolinAPI();
                }
            });
        }

        @Override
        public void onWeatherDataNotAvailable() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this,
                            "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    };

    final DataSource.LoadAQIDataCallback mLoadAQIDataCallback = new DataSource.LoadAQIDataCallback() {
        @Override
        public void onAQIDataLoaded(final AQI aqi) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (aqi != null && "ok".equals(aqi.status)) {
                        showAQIInfo(aqi);
                    } else {
                        Toast.makeText(WeatherActivity.this,
                                "获取空气质量信息失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onAQIDataNotAvailable() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WeatherActivity.this,
                            "获取空气质量信息失败", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };



    private void showAQIInfo(AQI aqi) {
        aqiText.setText(aqi.aqiNowInfo.aqi);
        pm25Text.setText(aqi.aqiNowInfo.pm25);
    }

    private void loadBingPicPathFromGuolinAPI() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic).enqueue(new Callback(){
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingImg);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        titleCity.setText(weather.basic.cityName);
        titleUpdateTime.setText(weather.update.localUpdateTime.split(" ")[1]);
        degreeText.setText(weather.now.temperature + "℃");
        weatherInfoText.setText(weather.now.weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.tv_data);
            TextView infoText = view.findViewById(R.id.tv_info);
            TextView maxText = view.findViewById(R.id.tv_max);
            TextView minText = view.findViewById(R.id.tv_min);
            dateText.setText(forecast.date);
            infoText.setText(forecast.weatherInfoDay);
            maxText.setText(forecast.maxTemper);
            minText.setText(forecast.minTemper);
            forecastLayout.addView(view);
        }

        for (Suggestion suggestion : weather.suggestionList) {
            View view = LayoutInflater.from(this).inflate(R.layout.suggestion_item, suggestionLayout, false);
            TextView indexText = view.findViewById(R.id.tv_index);
            TextView suggestionText = view.findViewById(R.id.tv_suggestion);
            indexText.setText("程度 : " + suggestion.level);
            suggestionText.setText("建议 : " + suggestion.suggestionInfo);
            suggestionLayout.addView(view);
        }

        weatherScrollView.setVisibility(View.VISIBLE);
        mCurrentCityName = weather.basic.cityName;
        scheduleUpdateJob();
    }

    private void scheduleUpdateJob() {
        JobScheduler scheduler = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(WeatherActivity.this, UpdateJobService.class);
        JobInfo.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new JobInfo.Builder(1, componentName);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setRequiresDeviceIdle(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setRequiresBatteryNotLow(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setPeriodic(1000 * 60 * 60 * 5);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setPersisted(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduler.schedule(builder.build());
        }
    }
}
