package com.example.player_sample_project.seeallpage.modelclassPoJo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class NewResponse {
    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("totalResults")
    @Expose
    private Integer totalResults;

    @SerializedName("articles")
    @Expose
    private List<NewResponseData> articles = null;

    public List<NewResponseData> getArticles() {
        return articles;
    }
}
