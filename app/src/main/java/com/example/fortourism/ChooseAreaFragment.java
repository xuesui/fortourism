package com.example.fortourism;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.example.fortourism.db.City;
import com.example.fortourism.db.Province;
import com.example.fortourism.db.County;
import com.example.fortourism.util.HttpUtil;
import com.example.fortourism.util.Utility;

public class ChooseAreaFragment extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog mProgressDialog;

    private TextView mTitleTextView;

    private Button mBackButton;

    private ListView mListView;

    private ArrayAdapter<String> mAdapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> mProvinceList;

    private List<City> mCityList;

    private List<County> mCountyList;

    private Province mSelectedProvince;

    private City mSelectedCity;

    private County mSelectedCounty;

    private int mCurrentLevel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTitleTextView = view.findViewById(R.id.tv_title);
        mBackButton = view.findViewById(R.id.btn_back);
        mListView = view.findViewById(R.id.lv_list);
        mAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        mListView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVEL_PROVINCE) {
                    mSelectedProvince = mProvinceList.get(position);
                    queryCitys();
                } else if (mCurrentLevel == LEVEL_CITY){
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                } else if (mCurrentLevel == LEVEL_COUNTY) {
                    String countyName = mCountyList.get(position).getmCountyName();
                    if (getActivity() instanceof MainActivity) {
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("county_name", countyName);
                        startActivity(intent);
                        getActivity().finish();
                    } else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity)getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.mSwipeRefreshLayout.setRefreshing(true);
                        activity.loadAllData(countyName);
                    }
                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVEL_COUNTY) {
                    queryCitys();
                } else if (mCurrentLevel == LEVEL_CITY) {
                    queryProvinces();
                }
            }
        });
        queryProvinces();
    }




    private void queryProvinces() {
        mTitleTextView.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);
        if (mProvinceList.size() > 0) {
            dataList.clear();
            for (Province province : mProvinceList) {
                dataList.add(province.getmProvinceName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }



    private void queryCitys() {
        mTitleTextView.setText(mSelectedProvince.getmProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport
                .where("mProvinceId = ?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        if (mCityList.size() > 0) {
            dataList.clear();
            for (City city : mCityList) {
                dataList.add(city.getmCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        } else {
            int provinceCode = mSelectedProvince.getmProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }

    }

    private void queryCounties() {
        mTitleTextView.setText(mSelectedCity.getmCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport
                .where("mCityId = ?", String.valueOf(mSelectedCity.getId())).find(County.class);
        if (mCountyList.size() > 0) {
            dataList.clear();
            for (County county : mCountyList) {
                dataList.add(county.getmCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        } else {
            int provinceCode = mSelectedProvince.getmProvinceCode();
            int cityCode = mSelectedCity.getmCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }

    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call0,@NonNull IOException e) {
                Log.d("asd",e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvinceResponse(responseText);
                } else if ("city".equals(type)) {
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountyResponse(responseText, mSelectedCity.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCitys();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }


    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    private void closeProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
