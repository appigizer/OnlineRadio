package com.example.mjstudio.internetradio;

import java.util.Map;

import io.realm.RealmObject;

/**
 * Created by mjstudio on 12/12/16.
 */

public class CategoryEntity extends RealmObject {
    private String id;
    private String title;

    public String getId ()
    {
        return id;
    }
    public void setId (String id) {
        this.id = id;
    }
    public String getTitle ()
    {
        return title;
    }
    public void setTitle (String title)
    {
        this.title = title;
    }

    public void updateWithData(Map<String, Object> map){
        CategoryEntity categoryEntity = this;
        categoryEntity.setId(String.valueOf(map.get("id")));
        categoryEntity.setTitle((String) map.get("title"));
    }
}
