package com.example.fortourism.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;

    public Basic basic;

    public Update update;

    public Now now;

    @SerializedName("lifestyle")
    public List<Suggestion> suggestionList;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;
}
