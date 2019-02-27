package com.example.fortourism.db;

import org.litepal.crud.DataSupport;

public class City extends DataSupport {
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmCityName() {
        return cityName;
    }

    public void setmCityName(String mCityName) {
        this.cityName = mCityName;
    }

    public int getmCityCode() {
        return cityCode;
    }

    public void setmCityCode(int mCityCode) {
        this.cityCode = mCityCode;
    }

    public int getmProvinceId() {
        return provinceId;
    }

    public void setmProvinceId(int mProvinceId) {
        this.provinceId = mProvinceId;
    }
}
