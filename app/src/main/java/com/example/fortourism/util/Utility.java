package com.example.fortourism.util;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.fortourism.db.County;
import com.example.fortourism.db.Province;
import com.example.fortourism.db.City;

public class Utility {
    public static boolean handleProvinceResponse(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setmProvinceName(provinceObject.getString("name"));
                    province.setmProvinceCode(provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCityResponse(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setmCityName(cityObject.getString("name"));
                    city.setmCityCode(cityObject.getInt("id"));
                    city.setmProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setmCountyName(countyObject.getString("name"));
                    county.setmWeatherId(countyObject.getString("weather_id"));
                    county.setmCityId(cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static <T> T handleHeAPIResponse(String response, Class<T> classOfT) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather6");
            String strContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(strContent, classOfT);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJson(Object o) {
        return new Gson().toJson(o);
    }

    public static <T> T toObject(String strContent, Class<T> classOfT) {
        return new Gson().fromJson(strContent, classOfT);
    }
}
