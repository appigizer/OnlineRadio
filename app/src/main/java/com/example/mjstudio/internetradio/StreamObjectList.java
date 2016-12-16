package com.example.mjstudio.internetradio;

import io.realm.RealmObject;

/**
 * Created by mjstudio on 12/12/16.
 */

public class StreamObjectList extends RealmObject {
    private String id;
    private String streamurl;
    private String imageurl;
    private String streamname;
    private  String catid;

    public String getImageurl ()
    {
        return imageurl;
    }
    public String getStreamId ()
    {
        return id;
    }

    public void setStreamId (String id) {
        this.id = id;
    }
    public String getCatId ()
    {
        return catid;
    }

    public void setCatId (String catid) {
        this.catid = catid;
    }

    public void setImageurl (String imageurl)
    {
        this.imageurl = imageurl;
    }

    public String getStreamurl ()
    {
        return streamurl;
    }

    public void setStreamurl (String streamurl)
    {
        this.streamurl = streamurl;
    }
    public String getStreamname ()
    {
        return streamname;
    }

    public void setStreamname (String streamname)
    {
        this.streamname = streamname;
    }
}
