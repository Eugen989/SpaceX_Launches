package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Patch {
    @SerializedName("small")
    private String small;

    @SerializedName("large")
    private String large;

    public String getSmall() { return small; }
    public void setSmall(String small) { this.small = small; }

    public String getLarge() { return large; }
    public void setLarge(String large) { this.large = large; }
}
