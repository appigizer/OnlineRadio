package com.example.mjstudio.internetradio;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class SharedRadioClass {
    public Context context;

    private static SharedRadioClass mysingleobject = null;
   public String url;
    public  String streamname;
    public  int alertbox = -1;
    public  int redimageurl = -1;
    public int category = -1;
    public  int setmediaplayervalue =1;
    public  int setvaluewhenonpause = -1;
    public  String globalurl ="http://api.dirble.com/v2/";
    private SharedRadioClass() {

    }
    public static SharedRadioClass getMysingleobject() {
        if (mysingleobject == null)
            mysingleobject = new SharedRadioClass();
        Log.d("myvalue","position"+"rahulSingh");
        return mysingleobject;
    }
}