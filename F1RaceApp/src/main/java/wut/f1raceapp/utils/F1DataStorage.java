package wut.f1raceapp.utils;

import wut.f1raceapp.RaceApp;
import wut.f1raceapp.dataModel.DriverData;
import wut.f1raceapp.dataModel.PositionData;
import wut.f1raceapp.dataModel.RaceControlData;
import wut.f1raceapp.dataModel.WeatherData;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class F1DataStorage {

    private List<DriverData> dataList = new ArrayList<>();
    private List<PositionData> positionList = new ArrayList<>();
    private List<WeatherData> weatherList = new ArrayList<>();
    private List<RaceControlData> raceControlList = new ArrayList<>();
    private int faultyDataCounter = 0;
    private Lock lock = new ReentrantLock();

    public void addData(DriverData data) {
        lock.lock();
        try {
            dataList.add(data);
        } finally {
            lock.unlock();
        }
    }

    public void addPosition(PositionData data) {
        lock.lock();
        try {
            positionList.add(data);
        } finally {
            lock.unlock();
        }
    }

    public void addWeather(WeatherData data) {
        lock.lock();
        try {
            weatherList.add(data);
        } finally {
            lock.unlock();
        }
    }

    public void addRaceControl(RaceControlData data) {
        lock.lock();
        try {
            raceControlList.add(data);
        } finally {
            lock.unlock();
        }
    }

    public List<DriverData> getDataList() {
        lock.lock();
        try {
            return new ArrayList<>(dataList);
        } finally {
            lock.unlock();
        }
    }

    public List<PositionData> getPositionList() {
        lock.lock();
        try {
            return new ArrayList<>(positionList);
        } finally {
            lock.unlock();
        }
    }

    public List<WeatherData> getWeatherList() {
        lock.lock();
        try {
            return new ArrayList<>(weatherList);
        } finally {
            lock.unlock();
        }
    }

    public List<RaceControlData> getRaceControlList() {
        lock.lock();
        try {
            return new ArrayList<>(raceControlList);
        } finally {
            lock.unlock();
        }
    }

    public DriverData peekLastData() {
        return dataList.get(dataList.size() - 1);
    }

    public PositionData peekLastPosition() {
        return positionList.get(positionList.size() - 1);
    }

    public WeatherData peekLastWeather() {
        return weatherList.get(weatherList.size() - 1);
    }

    public RaceControlData peekLastRaceControl() {
        return raceControlList.get(raceControlList.size() - 1);
    }

    public int getFaultyDataCounter() {
        return faultyDataCounter;
    }

    public void incrementFaultyDataCounter() {
        faultyDataCounter++;
    }
}

