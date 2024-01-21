package wut.f1raceapp.dataModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherData extends RaceData {
    private String date;
    private int rainfall;
    private double trackTemperature;
    private double airTemperature;
    private int meetingKey;
    private int sessionKey;
    private int humidity;
    private int windDirection;
    private int windSpeed;
    private double pressure;
    private String type;

    @JsonCreator
    public WeatherData(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") @JsonProperty("date") String date,
            @JsonProperty("rainfall") int rainfall,
            @JsonProperty("track_temperature") double trackTemperature,
            @JsonProperty("air_temperature") double airTemperature,
            @JsonProperty("meeting_key") int meetingKey,
            @JsonProperty("session_key") int sessionKey,
            @JsonProperty("humidity") int humidity,
            @JsonProperty("wind_direction") int windDirection,
            @JsonProperty("wind_speed") int windSpeed,
            @JsonProperty("pressure") double pressure,
            @JsonProperty("type") String type) {
        this.date = date;
        this.rainfall = rainfall;
        this.trackTemperature = trackTemperature;
        this.airTemperature = airTemperature;
        this.meetingKey = meetingKey;
        this.sessionKey = sessionKey;
        this.humidity = humidity;
        this.windDirection = windDirection;
        this.windSpeed = windSpeed;
        this.pressure = pressure;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public int getRainfall() {
        return rainfall;
    }

    public double getTrackTemperature() {
        return trackTemperature;
    }

    public double getAirTemperature() {
        return airTemperature;
    }

    public int getMeetingKey() {
        return meetingKey;
    }

    public int getSessionKey() {
        return sessionKey;
    }

    public int getHumidity() {
        return humidity;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public int getWindSpeed() {
        return windSpeed;
    }

    public double getPressure() {
        return pressure;
    }

    public String getType() {
        return type;
    }

}