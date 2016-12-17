package com.example.mjstudio.internetradio;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class SharedRadioClass {
    public Context context;

    private static SharedRadioClass mysingleobject = null;
   public String url;
    public  String streamname;
    public  int alertbox;
    public  int redurl=-1;
    public int category=-1;
    public  int mediaplayer=1;
    private SharedRadioClass() {

    }

    public static SharedRadioClass getMysingleobject() {
        if (mysingleobject == null)
            mysingleobject = new SharedRadioClass();
        Log.d("myvalue","position"+"rahulSingh");
        return mysingleobject;
    }
}