package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Diameter {
    @SerializedName("meters")
    private Double meters;

    @SerializedName("feet")
    private Double feet;

    public Diameter() {
    }

    public Diameter(Double meters, Double feet) {
        this.meters = meters;
        this.feet = feet;
    }

    public Double getMeters() {
        return meters;
    }

    public void setMeters(Double meters) {
        this.meters = meters;
    }

    public Double getFeet() {
        return feet;
    }

    public void setFeet(Double feet) {
        this.feet = feet;
    }
}