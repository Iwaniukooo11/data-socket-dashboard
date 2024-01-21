package wut.f1raceapp.dataModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RaceControlData extends RaceData {

    private String date;
    private Integer driverNumber; // Use Integer for nullable fields
    private String flag;
    private int meetingKey;
    private String scope;
    private int sessionKey;
    private Integer lapNumber; // Use Integer for nullable fields
    private String category;
    private String message;
    private String type;
    private String sector;

    @JsonCreator
    public RaceControlData(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") @JsonProperty("date") String date,
            @JsonProperty("driver_number") Integer driverNumber,
            @JsonProperty("flag") String flag,
            @JsonProperty("meeting_key") int meetingKey,
            @JsonProperty("scope") String scope,
            @JsonProperty("session_key") int sessionKey,
            @JsonProperty("lap_number") Integer lapNumber,
            @JsonProperty("category") String category,
            @JsonProperty("message") String message,
            @JsonProperty("type") String type,
            @JsonProperty("sector") String sector) {
        this.date = date;
        this.driverNumber = driverNumber;
        this.flag = flag;
        this.meetingKey = meetingKey;
        this.scope = scope;
        this.sessionKey = sessionKey;
        this.lapNumber = lapNumber;
        this.category = category;
        this.message = message;
        this.type = type;
        this.sector = sector;
    }

    public String getDate() {
        return date;
    }

    public Integer getDriverNumber() {
        return driverNumber;
    }

    public String getFlag() {
        return flag;
    }

    public int getMeetingKey() {
        return meetingKey;
    }

    public String getScope() {
        return scope;
    }

    public int getSessionKey() {
        return sessionKey;
    }

    public Integer getLapNumber() {
        return lapNumber;
    }

    public String getCategory() {
        return category;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getSector() {
        return sector;
    }
}