package com.appigizer.internetradio;


import android.content.Context;

public class SettingsManager {
    public Context context;
    private static SettingsManager mysingleobject = null;
    public String url;
    public StreamEntity selectedStreamEntity;
    public  String streamname;
    public  int alertbox = -1;
    public  int redimageurl = -1;
    public int category = -1;
    public  int setmediaplayervalue =1;
    public  int setvaluewhenonpause = -1;
    public  boolean onclicklistner = false;
    public  String globalurl ="http://api.dirble.com/v2/";
    public String DIRBLE_TOKEN = PUT_YOUR_OWN_DIRBLE_TOKEN_HERE; //e.g. "8d1f218f3e0fcb5c3efc7d01c7"
    private SettingsManager() {

    }
    public static SettingsManager getSharedInstance() {
        if (mysingleobject == null)
            mysingleobject = new SettingsManager();
            return mysingleobject;
    }
}