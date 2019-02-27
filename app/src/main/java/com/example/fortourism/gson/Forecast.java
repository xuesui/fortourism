package com.example.fortourism.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {
    public String date;

    @SerializedName("tmp_max")
    public String maxTemper;

    @SerializedName("tmp_min")
    public String minTemper;

    @SerializedName("cond_txt_d")
    public String weatherInfoDay;

    @SerializedName("cond_txt_n")
    public String weatherInfoNight;
}
