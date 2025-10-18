package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Reddit {
    @SerializedName("campaign")
    private String campaign;

    @SerializedName("launch")
    private String launch;

    @SerializedName("media")
    private String media;

    @SerializedName("recovery")
    private String recovery;

    public String getCampaign() { return campaign; }
    public void setCampaign(String campaign) { this.campaign = campaign; }

    public String getLaunch() { return launch; }
    public void setLaunch(String launch) { this.launch = launch; }

    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }

    public String getRecovery() { return recovery; }
    public void setRecovery(String recovery) { this.recovery = recovery; }
}
