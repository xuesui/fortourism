package com.example.fortourism.db;

import org.litepal.crud.DataSupport;

public class Province extends DataSupport {
    private int id;
    private String mProvinceName;
    private int mProvinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getmProvinceName() {
        return mProvinceName;
    }

    public void setmProvinceName(String mProvinceName) {
        this.mProvinceName = mProvinceName;
    }

    public int getmProvinceCode() {
        return mProvinceCode;
    }

    public void setmProvinceCode(int mProvinceCode) {
        this.mProvinceCode = mProvinceCode;
    }
}
