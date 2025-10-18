package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Failure {
    @SerializedName("time")
    private Integer time;

    @SerializedName("altitude")
    private Integer altitude;

    @SerializedName("reason")
    private String reason;

    public Integer getTime() { return time; }
    public void setTime(Integer time) { this.time = time; }

    public Integer getAltitude() { return altitude; }
    public void setAltitude(Integer altitude) { this.altitude = altitude; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
