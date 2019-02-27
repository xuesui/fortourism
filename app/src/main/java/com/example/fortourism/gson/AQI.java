package com.example.fortourism.gson;

import com.google.gson.annotations.SerializedName;

public class AQI {
    @SerializedName("air_now_city")
    public AQICity aqiNowInfo;

    public String status;

    public class AQICity {
        public String aqi;
        public String pm25;
        @SerializedName("qlty")
        public String airQuality;
    }
}
