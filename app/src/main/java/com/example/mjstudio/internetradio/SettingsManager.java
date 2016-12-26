package com.example.mjstudio.internetradio;


import android.content.Context;
import android.util.Log;

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
    private SettingsManager() {

    }
    public static SettingsManager getSharedInstance() {
        if (mysingleobject == null)
            mysingleobject = new SettingsManager();
            return mysingleobject;
    }
}