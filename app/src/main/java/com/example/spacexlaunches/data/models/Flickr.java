package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Flickr {
    @SerializedName("small")
    private List<String> small;

    @SerializedName("original")
    private List<String> original;

    public List<String> getSmall() { return small; }
    public void setSmall(List<String> small) { this.small = small; }

    public List<String> getOriginal() { return original; }
    public void setOriginal(List<String> original) { this.original = original; }
}
