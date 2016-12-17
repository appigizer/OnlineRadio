package com.example.mjstudio.internetradio;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mjstudio on 12/12/16.
 */

public class StreamList extends AppCompatActivity {
    Intent intent;
    public String category_id,categoryname,url,index,name,catname,data1,data2,streamname,streamurl;


    StreamObjectList streams;

    Realm realm;
    int toogle=0,red=0,period,mediaplayer;
    ImageView imageview;
    RealmResults<StreamObjectList> results,layout,streamsresult;
    private ProgressDialog pDialog;
    ArrayStationAdapter stationAdapter;
    RelativeLayout layout1;
    ListView listView;
    TextView textview;
    ImageButton imageButton;
    AlertDialog alertDialog;
    private Toolbar toolbar;
    static boolean active = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streamlist);
        intent = getIntent();

        SharedRadioClass.getMysingleobject().context = this;
        realm = Realm.getDefaultInstance();
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        alertDialog = new AlertDialog.Builder(this).create();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imageButton = (ImageButton)findViewById(R.id.imageButton);
        imageview  = (ImageView)findViewById(R.id.imageView1);
        textview = (TextView)findViewById(R.id.text1);
        results = realm.where(StreamObjectList.class).findAll();
        category_id = intent.getStringExtra("Categoryindex");
        Log.d("Background Task", "data321=============>>" + category_id);
        categoryname = intent.getStringExtra("Categoryname");
        data1 = intent.getStringExtra("data1");
        data2 = intent.getStringExtra("data2");
        Log.d("value","data321"+data1+","+data2);
        if(data1 != null && data2 != null)
        {
            textview.setText(data1);
            if (data2 != null) {
                String url = data2;

                if (url == null) {
                    URI uri1;
                    try {
                        uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                        Picasso.with(StreamList.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageview);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(data2);
                        Picasso.with(StreamList.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageview);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            layout1 =  (RelativeLayout)findViewById(R.id.layout1);
            layout1.setVisibility(View.VISIBLE);
        }
////        integer = Integer.parseInt(category_id);
        listView = (ListView) findViewById(R.id.stream_list);
        final TextView textView = (TextView)findViewById(R.id.textView);
        textView.setText(categoryname);
        streamsresult = realm.where(StreamObjectList.class).equalTo("catid", category_id).findAll();
        if(streamsresult.isEmpty())
        {
            catname = category_id;
            url = "http://api.dirble.com/v2/category/" + (category_id) + "/stations?token=3d4764dcfedc50c561564a45d1";
            PlayerTask placesTask = new PlayerTask();
            placesTask.execute(url);
        }

        stationAdapter = new ArrayStationAdapter(this,category_id);
        listView.setAdapter(stationAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                index=results.get(i).getStreamId().toString();
//                Log.d("category","index"+index);
                layout =  realm.where(StreamObjectList.class).equalTo("catid",category_id).findAll();
                index = layout.get(i).getImageurl();
                name = layout.get(i).getStreamname();
                red = i;

                SharedRadioClass.getMysingleobject().url=index;
                SharedRadioClass.getMysingleobject().streamname=name;

                textview.setText(name);
                textview.setOnClickListener(null);
                imageview.setOnClickListener(null);
                if (layout != null) {
                    String url = index;

                    if (url == null) {
                        URI uri1;
                        try {
                            uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                            Picasso.with(StreamList.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageview);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            URI uri = new URI(index);
                            Picasso.with(StreamList.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageview);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
                layout1 =  (RelativeLayout)findViewById(R.id.layout1);
                layout1.setVisibility(View.VISIBLE);
                imageButton.setImageResource(R.drawable.pause);

                // startService(new Intent(getApplicationContext(),RadioService.class).putExtra("position",i).putExtra("category",category_id));
                Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",i).putExtra("category",category_id);
                serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(serviceIntent);
                final Handler ha=new Handler();
                ha.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        period = SharedRadioClass.getMysingleobject().alertbox;
                        if(period != 1)
                        {
                            if(active == true) {
                                activeProgressDialog();
                                SharedRadioClass.getMysingleobject().redurl = red;
                                SharedRadioClass.getMysingleobject().category = Integer.parseInt(category_id);
                                stationAdapter.updateData();
                                SharedRadioClass.getMysingleobject().alertbox = 1;
                            }
                        }
                        SharedRadioClass.getMysingleobject().alertbox = 0;
                    }
                }, 15000);
            }
        });


    }



    //    public void onClick(View v) {
//        if(toogle == 0)
//        {
//            ((ImageButton) v).setImageResource(R.drawable.play1);
//            toogle=1;
//            startService(new Intent(StreamList.this ,RadioService.class).putExtra("position",-2));
//        }
//        else if(toogle == 1)
//        {
//            ((ImageButton) v).setImageResource(R.drawable.pause);
//            toogle=0;
//            startService(new Intent(StreamList.this ,RadioService.class).putExtra("position",-1));
//        }
//
//    }
    public class PlayerTask extends AsyncTask<String, Integer, String> {

        String data = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(StreamList.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }
        @Override
        protected String doInBackground(String... url) {
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task", "data=============>>" + data);

            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {

            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

            Log.d("Background ", "result=============>>" + result);

            try {
                JSONArray myListsAll = new JSONArray(result);

                Map<String, Object> map = new HashMap<>();
                map.put("jsonArray", myListsAll);
                JSONObject myobject = new JSONObject(map);
                Map<String, Object> maps = JSonHelper.toMap(myobject);
                ArrayList<Map<String, Object>> mapArrayList = (ArrayList<Map<String, Object>>) maps.get("jsonArray");

                realm.beginTransaction();
                for (Map<String, Object> stream : mapArrayList){
                    streams = realm.createObject(StreamObjectList.class);
                    streams.setCatId(category_id.toString());
                    streams.setRedurl("http://icons.iconarchive.com/icons/icons-land/play-stop-pause/256/Record-Normal-Red-icon.png");
                    streams.setStreamname((String) stream.get("name"));
                    streams.setStreamId((String) stream.get("id").toString());
                    ArrayList<Map<String, Object>> mapArrayList1 = (ArrayList<Map<String, Object>>) stream.get("streams");
                    for (Map<String,Object> str : mapArrayList1) {
                        streams.setStreamurl((String) str.get("stream"));
                        // Log.d("URL","URL"+str.get("stream"));
                    }
                    Map<String,Object> imageurl = (Map<String, Object>) stream.get("image");
                    streams.setImageurl((String) imageurl.get("url"));
                    Log.d("URL","URL"+imageurl.get("url"));
                   // http://icons.iconarchive.com/icons/icons-land/play-stop-pause/256/Record-Normal-Red-icon.png
                    //http://www.iconsdb.com/icons/preview/green/circle-xxl.png
           }

                realm.commitTransaction();
                stationAdapter.updateData();
            }
            catch (Exception e) {
                Log.d("Exception", e.toString());
            }
        }

        private String downloadUrl(String strUrl) throws IOException {
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(strUrl);

                // Creating an http connection to communicate with url
                urlConnection = (HttpURLConnection) url.openConnection();

                // Connecting to url
                urlConnection.connect();

                // Reading data from url
                iStream = urlConnection.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

                StringBuffer sb = new StringBuffer();

                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                data = sb.toString();

                br.close();

            } catch (Exception e) {
                //Log.d("Exception while downloading url", e.toString());
            } finally {
                iStream.close();
                urlConnection.disconnect();
            }
            return data;
        }
    }
    @Override
    public void onBackPressed() {

        super.onBackPressed();


    }
    @Override
    protected void onResume() {
        streamname = SharedRadioClass.getMysingleobject().streamname;
        streamurl = SharedRadioClass.getMysingleobject().url;
        Log.d("value","data321"+streamname+","+streamurl);
        if(streamurl != null && streamname != null){

            Log.d("value","data321"+streamname+","+streamurl);

            imageview = (ImageView) findViewById(R.id.imageView1);
            textview = (TextView) findViewById(R.id.text1);
            textview.setText(streamname);
            if (streamurl != null) {
                String url = streamurl;

                if (url == null) {
                    URI uri1;
                    try {
                        uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                        Picasso.with(StreamList.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageview);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(streamurl);
                        Picasso.with(StreamList.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageview);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            textview.setOnClickListener(null);
            imageview.setOnClickListener(null);
            layout1 = (RelativeLayout) findViewById(R.id.layout1);
            layout1.setVisibility(View.VISIBLE);
            imageButton = (ImageButton)findViewById(R.id.imageButton);
            imageButton.setImageResource(R.drawable.pause);
            mediaplayer = SharedRadioClass.getMysingleobject().mediaplayer;
            if(mediaplayer == 1)
            {
                imageButton.setImageResource(R.drawable.pause);

            }
            else if(mediaplayer ==0)
            {
                imageButton.setImageResource(R.drawable.play1);
            }
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toogle == 0)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.play1);
                        toogle=1;
                        //startService(new Intent(StreamList.this ,RadioService.class).putExtra("position",-2));
                        Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",-2);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                    else if(toogle == 1)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.pause);
                        toogle=0;
//            startService(new Intent(StreamList.this ,RadioService.class).putExtra("position",-1));
                        Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",-1);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                }
            });



        }
        super.onResume();

    }
    protected void activeProgressDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.dialog));

        dialog.setIcon(R.drawable.radio);
        dialog.setTitle(name);
        dialog.setMessage("Stream not Available.....");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.show();

    }
    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

}
