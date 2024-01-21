package wut.f1raceapp.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import wut.f1raceapp.dataModel.DriverData;
import wut.f1raceapp.dataModel.PositionData;
import wut.f1raceapp.dataModel.RaceControlData;
import wut.f1raceapp.dataModel.WeatherData;

public class F1DataProcessor {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static DriverData parseJson(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, DriverData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PositionData parseJsonPosition(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, PositionData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static RaceControlData parseJsonRaceControl(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, RaceControlData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WeatherData parseJsonWeather(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, WeatherData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
