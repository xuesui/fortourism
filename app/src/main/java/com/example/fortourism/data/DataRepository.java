package com.example.fortourism.data;
import com.example.fortourism.gson.AQI;
import com.example.fortourism.gson.Weather;
import com.example.fortourism.util.Utility;

public class DataRepository implements DataSource{
    private static DataRepository sDataRepository = null;

    private DataSource mRemoteDataSource;

    private DataSource mLocalDataSource;

    private DataRepository (DataSource remoteDataSource, DataSource localDataSource) {
        mRemoteDataSource = remoteDataSource;
        mLocalDataSource = localDataSource;
    }

    public static DataRepository getInstance(DataSource remoteDataSource, DataSource localDataSource) {
        if (sDataRepository == null) {
            sDataRepository = new DataRepository(remoteDataSource, localDataSource);
        }
        return sDataRepository;
    }

    @Override
    public void loadWeatherData(final String id, final LoadWeatherDataCallback callback) {
        mLocalDataSource.loadWeatherData(id, new LoadWeatherDataCallback() {
            @Override
            public void onWeatherDataLoaded(Weather weather) {
                callback.onWeatherDataLoaded(weather);
            }

            @Override
            public void onWeatherDataNotAvailable() {
                loadWeatherDataFromRemoteSource(id, callback);
            }
        });
    }

    @Override
    public void loadAQIData(final String id, final LoadAQIDataCallback callback) {
        loadAQIDataFromRemoteSource(id, callback);
    }

    @Override
    public void saveWeatherData(String city, String weatherString) {
        mLocalDataSource.saveWeatherData(city, weatherString);
    }

    @Override
    public void saveAQIData(String city, String aqiString) {
        mLocalDataSource.saveAQIData(city, aqiString);
    }

    public void loadWeatherDataFromRemoteSource(final String id, final LoadWeatherDataCallback callback) {
        mRemoteDataSource.loadWeatherData(id, new LoadWeatherDataCallback() {
            @Override
            public void onWeatherDataLoaded(Weather weather) {
                callback.onWeatherDataLoaded(weather);
                mLocalDataSource.saveWeatherData(id, Utility.toJson(weather));
            }

            @Override
            public void onWeatherDataNotAvailable() {
                callback.onWeatherDataNotAvailable();
            }
        });
    }

     public void loadAQIDataFromRemoteSource(final String id, final LoadAQIDataCallback callback) {
        mRemoteDataSource.loadAQIData(id, new LoadAQIDataCallback() {
            @Override
            public void onAQIDataLoaded(AQI aqi) {
                callback.onAQIDataLoaded(aqi);
            }

            @Override
            public void onAQIDataNotAvailable() {
                callback.onAQIDataNotAvailable();
            }
        });
    }
}
