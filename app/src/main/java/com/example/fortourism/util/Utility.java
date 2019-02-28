package com.example.fortourism.util;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.example.fortourism.gson.AQI;
import com.example.fortourism.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.fortourism.db.County;
import com.example.fortourism.db.Province;
import com.example.fortourism.db.City;

public class Utility {
    private static String response;

    public static boolean handleProvinceResponse(String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            try{
                JSONArray allProvinces=new JSONArray(response);
                for(int i=0;i<allProvinces.length();i++){
                    JSONObject provinceObject=allProvinces.getJSONObject(i);
                    Province province=new Province();
                    province.setProvinceName(provinceObject.getString("name"));
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } return false;
    }

    public static boolean handleCityResponse(String response,int provinceId)
    {   if(!TextUtils.isEmpty(response))
    {
        try{
            JSONArray allCities=new JSONArray(response);
            for(int i=0;i<allCities.length();i++)
            {JSONObject cityObject=allCities.getJSONObject(i);
                City city=new City();
                city.setmCityName(cityObject.getString("name"));
                city.setmCityCode(cityObject.getInt("id"));
                city.setmProvinceId(provinceId);
                city.save();
            }return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId)
    {  if(!TextUtils.isEmpty(response))
    {
        try{
            JSONArray allcounties=new JSONArray(response);
            for(int i=0;i<allcounties.length();i++)
            {
                JSONObject countyObject=allcounties.getJSONObject(i);
                County county=new County();
                county.setCountyName(countyObject.getString("name"));
                county.setWeatherId(countyObject.getString("weather_id"));
                county.setCityId(cityId);
                county.save();
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

        return false;
    }

    @Nullable
    public static Weather handleWeatherResponse(String response) {
        try {

            JSONObject jsonObject = new JSONObject(response);
            Gson g=new Gson();
            Weather weather=  g.fromJson(jsonObject.toString(),Weather.class);

            return weather;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;

    }


    public static AQI handleAQIResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Gson g=new Gson();
            AQI aqi=  g.fromJson(jsonObject.toString(),AQI.class);

            return aqi;

        } catch (Exception e) {
            e.printStackTrace();


        }
        return null;

    }

}
