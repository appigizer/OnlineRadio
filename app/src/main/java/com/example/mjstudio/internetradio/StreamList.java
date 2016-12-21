package com.example.mjstudio.internetradio;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mjstudio on 12/12/16.
 */

public class StreamList extends AppCompatActivity {
    Intent intent;
    public String category_id, categorynamefor, streamurlforstream, streamurlindex,streamurlname, streamname,streamurl;
    StreamEntity streamsEntity;
    Realm realm;
    int toogleforplaypausebutton = 0, redimagefornotplayingsong =0, checkforalertbox, checkforplayerisonoroff,setmadiavalueonpause;
    ImageView imageviewforimageandtitle;
    RealmResults<StreamEntity> databaseresults, databaselayoutresult, databasestreamsresult;
    private ProgressDialog pDialog;
    ArrayStationAdapter stationAdapter;
    RelativeLayout layout1forimageandtitle;
    ListView listViewforstreamtitleandimage;
    TextView textviewforstreamtitle;
    ImageButton imageButtonforplaypause;
    private Toolbar toolbar;
    String globalUrl;
    AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.streamlist);

        //initialize the database
        realm = Realm.getDefaultInstance();
        databaseresults = realm.where(StreamEntity.class).findAll();

        //get streamname and streamurl send by the MainActivityClass
        intent = getIntent();
        category_id = intent.getStringExtra("Categoryindex");
        categorynamefor = intent.getStringExtra("Categoryname");

        //set the category name in toolbar
        final TextView textView = (TextView)findViewById(R.id.textViewfor_toolbarcategoryname);
        textView.setText(categorynamefor);

        //initialize the SharedRadioClass
        SharedRadioClass.getMysingleobject().context = this;

        //set the toolbar
        toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the databaselayoutresult to the toolbar object
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //handle the play and pause button
        imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpauseimage);
        imageButtonforplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toogleforplaypausebutton == 0)
                {
                    ((ImageButton) v).setImageResource(R.drawable.play1);
                    toogleforplaypausebutton =1;
                    Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",-2);
                    serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(serviceIntent);
                }
                else if(toogleforplaypausebutton == 1)
                {
                    ((ImageButton) v).setImageResource(R.drawable.pause);
                    toogleforplaypausebutton =0;
                    Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",-1);
                    serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                    startService(serviceIntent);
                }
            }
        });

        //initialize listview and the set the adapter
        listViewforstreamtitleandimage = (ListView) findViewById(R.id.listViewforshowingstreamsname);
        stationAdapter = new ArrayStationAdapter(this,category_id);
        listViewforstreamtitleandimage.setAdapter(stationAdapter);

        imageviewforimageandtitle = (ImageView)findViewById(R.id.imageViewforstreamimage);
        textviewforstreamtitle = (TextView)findViewById(R.id.textViewforstreamname);

        //get the category id which is send by the MainActivity and call the volley liabrary for getting the stream json firsttime
        //next time fetch the data from database
        databasestreamsresult = realm.where(StreamEntity.class).equalTo("catid", category_id).findAll();
        if(databasestreamsresult.isEmpty())
        {
            globalUrl = SharedRadioClass.getMysingleobject().globalurl;
            streamurlforstream = globalUrl+"category/"+category_id + "/stations?token=3d4764dcfedc50c561564a45d1";
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            makeJsonArrayRequest(streamurlforstream);
        }
    }
    @Override
    protected void onResume() {
        listViewforstreamtitleandimage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //get the database result
                databaselayoutresult =  realm.where(StreamEntity.class).equalTo("catid",category_id).findAll();
                streamurlindex = databaselayoutresult.get(i).getImageurl();
                streamurlname = databaselayoutresult.get(i).getStreamname();
                redimagefornotplayingsong = i;

                //set the streamurl and name in Sharedclass url and streamname
                SharedRadioClass.getMysingleobject().url= streamurlindex;
                SharedRadioClass.getMysingleobject().streamname= streamurlname;

                textviewforstreamtitle.setText(streamurlname);

                //set the onclick listner false textview and imageview of the  layout1forimageandtitle
                textviewforstreamtitle.setOnClickListener(null);
                imageviewforimageandtitle.setOnClickListener(null);

                if (databaselayoutresult != null) {
                    String url = streamurlindex;

                    if (url == null) {
                        URI uri1;
                        try {
                            uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                            Picasso.with(StreamList.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageviewforimageandtitle);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            URI uri = new URI(streamurlindex);
                            Picasso.with(StreamList.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageviewforimageandtitle);

                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //show the layout when the stream is playing
                layout1forimageandtitle =  (RelativeLayout)findViewById(R.id.layoutforhandlingplayandpause);
                layout1forimageandtitle.setVisibility(View.VISIBLE);

                imageButtonforplaypause.setImageResource(R.drawable.pause);

                //call the service with the sending the listview item poition and category id and also set the action which is done in notification panel
                Intent serviceIntent = new Intent(StreamList.this, RadioService.class).putExtra("position",i).putExtra("setvaluecategory",category_id).putExtra("categoryName",categorynamefor);
                serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                startService(serviceIntent);

                //show the dialog box after 10 seconds if stream is not playing
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!((StreamList.this).isFinishing())) {
                            //get the value of alertbox and check
                            checkforalertbox = SharedRadioClass.getMysingleobject().alertbox;
                            if(checkforalertbox == 0) {
                                //call the alertdialog function
                                activeProgressDialog();
                                //set the red image on the rightside of the listview item for wich stream which is not playing
                                SharedRadioClass.getMysingleobject().redimageurl = redimagefornotplayingsong;
                                SharedRadioClass.getMysingleobject().category = Integer.parseInt(category_id);
                                stationAdapter.updateData();
                            }
                        }
                    }
                },10000);
            }
        });
        //get the streamname and imageurl showing in the layout with play and pause button
        streamname = SharedRadioClass.getMysingleobject().streamname;
        streamurl = SharedRadioClass.getMysingleobject().url;

        if(streamurl != null && streamname != null){
            imageviewforimageandtitle = (ImageView) findViewById(R.id.imageViewforstreamimage);
            textviewforstreamtitle = (TextView) findViewById(R.id.textViewforstreamname);

            textviewforstreamtitle.setText(streamname);
            if (streamurl != null) {
                String url = streamurl;

                if (url == null) {
                    URI uri1;
                    try {
                        uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                        Picasso.with(StreamList.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageviewforimageandtitle);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(streamurl);
                        Picasso.with(StreamList.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageviewforimageandtitle);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }

            textviewforstreamtitle.setOnClickListener(null);
            imageviewforimageandtitle.setOnClickListener(null);

            layout1forimageandtitle = (RelativeLayout) findViewById(R.id.layoutforhandlingplayandpause);
            layout1forimageandtitle.setVisibility(View.VISIBLE);

            //get the checkforplayerisonoroff value which is set in the service class for handle the play and pause button
            checkforplayerisonoroff = SharedRadioClass.getMysingleobject().setmediaplayervalue;
            if(checkforplayerisonoroff == 1)
            {
                imageButtonforplaypause.setImageResource(R.drawable.pause);

            }
            else if(checkforplayerisonoroff ==0)
            {
                imageButtonforplaypause.setImageResource(R.drawable.play1);
            }
        }
        super.onResume();
    }
    public void makeJsonArrayRequest(String streamurl) {
        //show the processdialog before fetching the json data fron server
        showpDialog();
        String urlJsonArry = streamurl;

        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try
                        {
                            handleJsonResponse(response);
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                        //hide the processdialog after fetching the json data
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }

    private void handleJsonResponse(JSONArray response) {
        try {
            ArrayList<Map<String, Object>> mapArrayList = (ArrayList<Map<String, Object>>) JSonHelper.toList(response);
            //set the category_id stream name,id,imageurl,redimageurln in the database
            realm.beginTransaction();
            for (Map<String, Object> stream : mapArrayList){
                streamsEntity = realm.createObject(StreamEntity.class);
                streamsEntity.setCatId(category_id.toString());
                streamsEntity.setRedurl("http://icons.iconarchive.com/icons/icons-land/play-stop-pause/256/Record-Normal-Red-icon.png");
                streamsEntity.setStreamname((String) stream.get("name"));
                streamsEntity.setStreamId(stream.get("id").toString());
                ArrayList<Map<String, Object>> mapArrayList1 = (ArrayList<Map<String, Object>>) stream.get("streams");
                for (Map<String,Object> str : mapArrayList1) {
                    streamsEntity.setStreamurl((String) str.get("stream"));
                }
                Map<String,Object> imageurl = (Map<String, Object>) stream.get("image");
                streamsEntity.setImageurl((String) imageurl.get("url"));
            }
            realm.commitTransaction();
            stationAdapter.updateData();
        }
        catch (Exception e) {
            Log.d("Exception", e.toString());
        }
    }
    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }
    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    protected void activeProgressDialog() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(StreamList.this, R.style.dialog);
        dialog.setIcon(R.drawable.radio);
        dialog.setTitle(streamurlname);
        dialog.setMessage("Stream not Available.....");
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        alertDialog = dialog.create();
        Log.d("value","dat321"+checkforalertbox);
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //set the play and pause button when the activity is open and play and pause change in the notificatio panel
        setmadiavalueonpause = SharedRadioClass.getMysingleobject().setvaluewhenonpause;
        if(hasFocus)
        {
            if(setmadiavalueonpause == 1)
            {
                imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpauseimage);
                imageButtonforplaypause.setImageResource(R.drawable.pause);
            }
            else if(setmadiavalueonpause == 0)
            {
                imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpauseimage);
                imageButtonforplaypause.setImageResource(R.drawable.play1);

            }
        }
    }
}
