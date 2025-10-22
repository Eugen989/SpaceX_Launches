package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Rocket {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("type")
    private String type;

    @SerializedName("company")
    private String company;

    @SerializedName("country")
    private String country;

    @SerializedName("description")
    private String description;

    @SerializedName("flickr_images")
    private java.util.List<String> flickrImages;

    @SerializedName("wikipedia")
    private String wikipedia;

    @SerializedName("active")
    private Boolean active;

    @SerializedName("stages")
    private Integer stages;

    @SerializedName("boosters")
    private Integer boosters;

    @SerializedName("cost_per_launch")
    private Integer costPerLaunch;

    @SerializedName("success_rate_pct")
    private Integer successRatePct;

    @SerializedName("first_flight")
    private String firstFlight;

    @SerializedName("height")
    private Height height;

    @SerializedName("diameter")
    private Diameter diameter;

    @SerializedName("mass")
    private Mass mass;

    public Rocket() {
    }

    public Rocket(String id, String name, String type, String company, String country,
                  String description, java.util.List<String> flickrImages, String wikipedia,
                  Boolean active, Integer stages, Integer boosters, Integer costPerLaunch,
                  Integer successRatePct, String firstFlight, Height height,
                  Diameter diameter, Mass mass) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.company = company;
        this.country = country;
        this.description = description;
        this.flickrImages = flickrImages;
        this.wikipedia = wikipedia;
        this.active = active;
        this.stages = stages;
        this.boosters = boosters;
        this.costPerLaunch = costPerLaunch;
        this.successRatePct = successRatePct;
        this.firstFlight = firstFlight;
        this.height = height;
        this.diameter = diameter;
        this.mass = mass;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public java.util.List<String> getFlickrImages() {
        return flickrImages;
    }

    public void setFlickrImages(java.util.List<String> flickrImages) {
        this.flickrImages = flickrImages;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getStages() {
        return stages;
    }

    public void setStages(Integer stages) {
        this.stages = stages;
    }

    public Integer getBoosters() {
        return boosters;
    }

    public void setBoosters(Integer boosters) {
        this.boosters = boosters;
    }

    public Integer getCostPerLaunch() {
        return costPerLaunch;
    }

    public void setCostPerLaunch(Integer costPerLaunch) {
        this.costPerLaunch = costPerLaunch;
    }

    public Integer getSuccessRatePct() {
        return successRatePct;
    }

    public void setSuccessRatePct(Integer successRatePct) {
        this.successRatePct = successRatePct;
    }

    public String getFirstFlight() {
        return firstFlight;
    }

    public void setFirstFlight(String firstFlight) {
        this.firstFlight = firstFlight;
    }

    public Height getHeight() {
        return height;
    }

    public void setHeight(Height height) {
        this.height = height;
    }

    public Diameter getDiameter() {
        return diameter;
    }

    public void setDiameter(Diameter diameter) {
        this.diameter = diameter;
    }

    public Mass getMass() {
        return mass;
    }

    public void setMass(Mass mass) {
        this.mass = mass;
    }
}