package com.example.spacexlaunches.data.models;

import com.google.gson.annotations.SerializedName;

public class Links {
    @SerializedName("patch")
    private Patch patch;

    @SerializedName("reddit")
    private Reddit reddit;

    @SerializedName("flickr")
    private Flickr flickr;

    @SerializedName("presskit")
    private String presskit;

    @SerializedName("webcast")
    private String webcast;

    @SerializedName("youtube_id")
    private String youtubeId;

    @SerializedName("article")
    private String article;

    @SerializedName("wikipedia")
    private String wikipedia;

    public Patch getPatch() { return patch; }
    public void setPatch(Patch patch) { this.patch = patch; }

    public Reddit getReddit() { return reddit; }
    public void setReddit(Reddit reddit) { this.reddit = reddit; }

    public Flickr getFlickr() { return flickr; }
    public void setFlickr(Flickr flickr) { this.flickr = flickr; }

    public String getPresskit() { return presskit; }
    public void setPresskit(String presskit) { this.presskit = presskit; }

    public String getWebcast() { return webcast; }
    public void setWebcast(String webcast) { this.webcast = webcast; }

    public String getYoutubeId() { return youtubeId; }
    public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }

    public String getArticle() { return article; }
    public void setArticle(String article) { this.article = article; }

    public String getWikipedia() { return wikipedia; }
    public void setWikipedia(String wikipedia) { this.wikipedia = wikipedia; }
}
