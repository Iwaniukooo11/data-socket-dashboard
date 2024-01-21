package wut.f1raceapp.dataModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PositionData extends RaceData {

    private int driverNumber;
    private String date;
    private int meetingKey;
    private int sessionKey;
    private int position;
    private String type;

    @JsonCreator
    public PositionData(
            @JsonProperty("driver_number") int driverNumber,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") @JsonProperty("date") String date,
            @JsonProperty("meeting_key") int meetingKey,
            @JsonProperty("session_key") int sessionKey,
            @JsonProperty("position") int position,
            @JsonProperty("type") String type) {
        this.driverNumber = driverNumber;
        this.date = date;
        this.meetingKey = meetingKey;
        this.sessionKey = sessionKey;
        this.position = position;
        this.type = type;
    }

    public int getDriverNumber() {
        return driverNumber;
    }

    public String getDate() {
        return date;
    }

    public int getMeetingKey() {
        return meetingKey;
    }

    public int getSessionKey() {
        return sessionKey;
    }

    public int getPosition() {
        return position;
    }

    public String getType() {
        return type;
    }

}