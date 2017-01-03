package com.appigizer.internetradio;

import com.google.gson.annotations.SerializedName;
public class CategoryPostEntity {

    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;


    public CategoryPostEntity(String id, String title) {
        this.id = id;
        this.title = title;

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
}