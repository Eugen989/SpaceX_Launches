package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Mass {
    @SerializedName("kg")
    private Integer kg;

    @SerializedName("lb")
    private Integer lb;

    public Mass() {
    }

    public Mass(Integer kg, Integer lb) {
        this.kg = kg;
        this.lb = lb;
    }

    public Integer getKg() {
        return kg;
    }

    public void setKg(Integer kg) {
        this.kg = kg;
    }

    public Integer getLb() {
        return lb;
    }

    public void setLb(Integer lb) {
        this.lb = lb;
    }
}