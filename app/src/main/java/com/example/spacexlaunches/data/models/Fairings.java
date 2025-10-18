package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Fairings {
    @SerializedName("reused")
    private Boolean reused;

    @SerializedName("recovery_attempt")
    private Boolean recoveryAttempt;

    @SerializedName("recovered")
    private Boolean recovered;

    @SerializedName("ships")
    private List<String> ships;

    public Boolean getReused() { return reused; }
    public void setReused(Boolean reused) { this.reused = reused; }

    public Boolean getRecoveryAttempt() { return recoveryAttempt; }
    public void setRecoveryAttempt(Boolean recoveryAttempt) { this.recoveryAttempt = recoveryAttempt; }

    public Boolean getRecovered() { return recovered; }
    public void setRecovered(Boolean recovered) { this.recovered = recovered; }

    public List<String> getShips() { return ships; }
    public void setShips(List<String> ships) { this.ships = ships; }
}
