package com.example.spacexlaunches.data.models;

import com.example.spacexlaunches.data.models.Failure;
import com.example.spacexlaunches.data.models.Fairings;
import com.example.spacexlaunches.data.models.Links;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Launch {
    @SerializedName("fairings")
    private Fairings fairings;

    @SerializedName("links")
    private Links links;

    @SerializedName("static_fire_date_utc")
    private String staticFireDateUtc;

    @SerializedName("static_fire_date_unix")
    private Integer staticFireDateUnix;

    @SerializedName("net")
    private Boolean net;

    @SerializedName("window")
    private Integer window;

    @SerializedName("rocket")
    private String rocket;

    @SerializedName("success")
    private Boolean success;

    @SerializedName("failures")
    private List<Failure> failures;

    @SerializedName("details")
    private String details;

    @SerializedName("crew")
    private List<String> crew;

    @SerializedName("ships")
    private List<String> ships;

    @SerializedName("capsules")
    private List<String> capsules;

    @SerializedName("payloads")
    private List<String> payloads;

    @SerializedName("launchpad")
    private String launchpad;

    @SerializedName("flight_number")
    private Integer flightNumber;

    @SerializedName("name")
    private String name;

    @SerializedName("date_utc")
    private String dateUtc;

    @SerializedName("date_unix")
    private Long dateUnix;

    @SerializedName("date_local")
    private String dateLocal;

    @SerializedName("date_precision")
    private String datePrecision;

    @SerializedName("upcoming")
    private Boolean upcoming;

    @SerializedName("auto_update")
    private Boolean autoUpdate;

    @SerializedName("tbd")
    private Boolean tbd;

    @SerializedName("launch_library_id")
    private String launchLibraryId;

    @SerializedName("id")
    private String id;

    public Fairings getFairings() { return fairings; }
    public void setFairings(Fairings fairings) { this.fairings = fairings; }

    public Links getLinks() { return links; }
    public void setLinks(Links links) { this.links = links; }

    public String getStaticFireDateUtc() { return staticFireDateUtc; }
    public void setStaticFireDateUtc(String staticFireDateUtc) { this.staticFireDateUtc = staticFireDateUtc; }

    public Integer getStaticFireDateUnix() { return staticFireDateUnix; }
    public void setStaticFireDateUnix(Integer staticFireDateUnix) { this.staticFireDateUnix = staticFireDateUnix; }

    public Boolean getNet() { return net; }
    public void setNet(Boolean net) { this.net = net; }

    public Integer getWindow() { return window; }
    public void setWindow(Integer window) { this.window = window; }

    public String getRocket() { return rocket; }
    public void setRocket(String rocket) { this.rocket = rocket; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }

    public List<Failure> getFailures() { return failures; }
    public void setFailures(List<Failure> failures) { this.failures = failures; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public List<String> getCrew() { return crew; }
    public void setCrew(List<String> crew) { this.crew = crew; }

    public List<String> getShips() { return ships; }
    public void setShips(List<String> ships) { this.ships = ships; }

    public List<String> getCapsules() { return capsules; }
    public void setCapsules(List<String> capsules) { this.capsules = capsules; }

    public List<String> getPayloads() { return payloads; }
    public void setPayloads(List<String> payloads) { this.payloads = payloads; }

    public String getLaunchpad() { return launchpad; }
    public void setLaunchpad(String launchpad) { this.launchpad = launchpad; }

    public Integer getFlightNumber() { return flightNumber; }
    public void setFlightNumber(Integer flightNumber) { this.flightNumber = flightNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDateUtc() { return dateUtc; }
    public void setDateUtc(String dateUtc) { this.dateUtc = dateUtc; }

    public Long getDateUnix() { return dateUnix; }
    public void setDateUnix(Long dateUnix) { this.dateUnix = dateUnix; }

    public String getDateLocal() { return dateLocal; }
    public void setDateLocal(String dateLocal) { this.dateLocal = dateLocal; }

    public String getDatePrecision() { return datePrecision; }
    public void setDatePrecision(String datePrecision) { this.datePrecision = datePrecision; }

    public Boolean getUpcoming() { return upcoming; }
    public void setUpcoming(Boolean upcoming) { this.upcoming = upcoming; }

    public Boolean getAutoUpdate() { return autoUpdate; }
    public void setAutoUpdate(Boolean autoUpdate) { this.autoUpdate = autoUpdate; }

    public Boolean getTbd() { return tbd; }
    public void setTbd(Boolean tbd) { this.tbd = tbd; }

    public String getLaunchLibraryId() { return launchLibraryId; }
    public void setLaunchLibraryId(String launchLibraryId) { this.launchLibraryId = launchLibraryId; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
}
