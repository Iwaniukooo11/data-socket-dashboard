package wut.f1raceapp.dataModel;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DriverData {
        private int driverNumber;
        private int nGear;
        private String date;
        private int throttle;
        private int brake;
        private int drs;
        private int meetingKey;
        private int sessionKey;
        private String type;
        private int rpm;
        private int speed;

        @JsonCreator
        public DriverData(
                @JsonProperty("driver_number") int driverNumber,
                @JsonProperty("n_gear") int nGear,
                @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS") @JsonProperty("date") String date,
                @JsonProperty("throttle") int throttle,
                @JsonProperty("brake") int brake,
                @JsonProperty("drs") int drs,
                @JsonProperty("meeting_key") int meetingKey,
                @JsonProperty("session_key") int sessionKey,
                @JsonProperty("type") String type,
                @JsonProperty("rpm") int rpm,
                @JsonProperty("speed") int speed) {
                this.driverNumber = driverNumber;
                this.nGear = nGear;
                this.date = date;
                this.throttle = throttle;
                this.brake = brake;
                this.drs = drs;
                this.meetingKey = meetingKey;
                this.sessionKey = sessionKey;
                this.type = type;
                this.rpm = rpm;
                this.speed = speed;
        }

        public int getDriverNumber() {
                return driverNumber;
        }

        public int getnGear() {
                return nGear;
        }

        public String getDate() {
                return date;
        }

        public int getThrottle() {
                return throttle;
        }

        public int getBrake() {
                return brake;
        }

        public int getDrs() {
                return drs;
        }

        public int getMeetingKey() {
                return meetingKey;
        }

        public int getSessionKey() {
                return sessionKey;
        }

        public String getType() {
                return type;
        }

        public int getRpm() {
                return rpm;
        }

        public int getSpeed() {
                return speed;
        }
}