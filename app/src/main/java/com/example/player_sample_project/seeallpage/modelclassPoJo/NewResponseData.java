package com.example.player_sample_project.seeallpage.modelclassPoJo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NewResponseData {
    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("urlToImage")
    @Expose
    private String urlToImage;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }
}
