package com.example.mjstudio.internetradio;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
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

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity{
    ListView categorylist;
    Realm realm;

    private ProgressDialog pDialog;
    AdapterCategory songAdt;
    int onlyone = 0;
    RealmResults<Categories> results;
    String url,index,name,data1,data2,streamname,streamurl;
    RelativeLayout layout1;
    Intent intent;
    TextView textview;
    ImageView imageview;
    ImageView imageView;
    ImageButton imageButton;
    int toogle=0,mediaplayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        categorylist = (ListView)findViewById(R.id.list);
        // PlayGifView pGif = (PlayGifView) findViewById(R.id.imageView);
        // pGif.setImageResource(R.drawable.reload1);
//        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayShowTitleEnabled(true);




        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            // getSupportActionBar().setHomeButtonEnabled(true);
            // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        results = realm.where(Categories.class).findAll();


//        Bundle extras = getIntent().getExtras();
//        if(extras != null ){
//
//            data1 = extras.getString("data1");
//            data2 = extras.getString("data2");
//                    // Log.d("value","data321"+data1+","+data2);
//
//                    imageview = (ImageView) findViewById(R.id.imageView1);
//                    textview = (TextView) findViewById(R.id.text1);
//                    textview.setText(data1);
//                    if (data2 != null) {
//                        String url = data2;
//
//                        if (url == null) {
//                            URI uri1;
//                            try {
//                                uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
//                                Picasso.with(MainActivity.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageview);
//                            } catch (URISyntaxException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            try {
//                                URI uri = new URI(data2);
//                                Picasso.with(MainActivity.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageview);
//
//                            } catch (URISyntaxException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    textview.setOnClickListener(null);
//                    imageview.setOnClickListener(null);
//                    layout1 = (RelativeLayout) findViewById(R.id.layout1);
//                    layout1.setVisibility(View.VISIBLE);
//
//        }


        if(results.isEmpty()) {
            url = "http://api.dirble.com/v2/categories?token=3d4764dcfedc50c561564a45d1";
            PlayerTask placesTask = new PlayerTask();
            placesTask.execute(url);
        }
        songAdt = new AdapterCategory(this);
        categorylist.setAdapter(songAdt);
        categorylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                index=results.get(i).getId().toString();
                name = results.get(i).getTitle().toString();
                startActivity(new Intent(MainActivity.this,StreamList.class).putExtra("Categoryindex",index).putExtra("Categoryname",name).putExtra("data1",data1).putExtra("data2",data2));
            }
        });
        imageView = (ImageView)findViewById(R.id.imageView) ;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = "http://api.dirble.com/v2/categories?token=3d4764dcfedc50c561564a45d1";
                PlayerTask placesTask = new PlayerTask();
                placesTask.execute(url);
            }
        });

    }

    public class PlayerTask extends AsyncTask<String, Integer, String> {
        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
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
            try {
                if(onlyone == 0) {
                    JSONArray myListsAll = new JSONArray(result);
                    Log.d("URL", "URLid" + result);
                    Map<String, Object> map = new HashMap<>();
                    map.put("jsonArray", myListsAll);
                    JSONObject myobject = new JSONObject(map);
                    Map<String, Object> maps = JSonHelper.toMap(myobject);
                    ArrayList<Map<String, Object>> mapArrayList = (ArrayList<Map<String, Object>>) maps.get("jsonArray");
                    realm.beginTransaction();

                    for (Map<String, Object> category : mapArrayList) {
                        Categories categories = realm.createObject(Categories.class);
                        categories.setTitle((String) category.get("title"));
                        categories.setId(String.valueOf(category.get("id")));
                    }
                    realm.commitTransaction();
                    songAdt.updateData();
                    onlyone = 1;
                }
            } catch (Exception e) {
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
                        Picasso.with(MainActivity.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageview);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(streamurl);
                        Picasso.with(MainActivity.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageview);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            imageButton = (ImageButton)findViewById(R.id.imageButton1);
            imageButton.setImageResource(R.drawable.pause);
            mediaplayer = SharedRadioClass.getMysingleobject().mediaplayer;
            if(mediaplayer == 1)
            {
                imageButton.setImageResource(R.drawable.pause);

            }
            else if(mediaplayer == 0)
            {
                imageButton.setImageResource(R.drawable.play1);
            }
            textview.setOnClickListener(null);
            imageview.setOnClickListener(null);
            layout1 = (RelativeLayout) findViewById(R.id.layout1);
            layout1.setVisibility(View.VISIBLE);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toogle == 0)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.play1);
                        toogle=1;
                        SharedRadioClass.getMysingleobject().mediaplayer=1;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position",-2);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                    else if(toogle == 1)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.pause);
                        toogle=0;
                        SharedRadioClass.getMysingleobject().mediaplayer=0;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position",-1);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                }
            });

        }
        super.onResume();

    }

}
