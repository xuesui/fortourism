package com.example.fortourism.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("location")
    public String cityName;

    @SerializedName("cid")
    public String weatherId;

    @SerializedName("update")
    public Update update;
    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
