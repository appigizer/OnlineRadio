package com.example.mjstudio.internetradio;


import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

public class SharedRadioClass {
    public Context context;

    private static SharedRadioClass mysingleobject = null;
   public String url;
    public  String streamname;
    private SharedRadioClass() {

    }

    public static SharedRadioClass getMysingleobject() {
        if (mysingleobject == null)
            mysingleobject = new SharedRadioClass();
        Log.d("myvalue","position"+"rahulSingh");
        return mysingleobject;
    }
}