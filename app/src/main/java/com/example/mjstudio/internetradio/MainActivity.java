package com.example.mjstudio.internetradio;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity{
    ListView listviewforcategorylist;
    Realm realm;
    private ProgressDialog pDialog;
    AdapterCategory adapterforcategorylist;

    RealmResults<CategoryEntity> databaseresults;
    String categoryindex, categoryname, streamname,streamurl;
    String globalUrl;
    RelativeLayout layoutforstreamimageandname;
    TextView textviewforstreamname;
    ImageView imageviewforstreamimage;
    ImageView imageViewforrefreshbutton;
    ImageButton imageButtonforplaypause;
    int toogleforplaypausebutton = 0, checkmediaplayervalue,setmadiavalueonpause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the realm database
        realm = Realm.getDefaultInstance();

        //initialize the toobar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        //initialize listview and set the adapter
        listviewforcategorylist = (ListView)findViewById(R.id.listViewforcategories);
        adapterforcategorylist = new AdapterCategory(this);
        listviewforcategorylist.setAdapter(adapterforcategorylist);

        //get database results and check if data is not available in database then call the volley liabrary for getting the jsondata and put in database first time
        //next time get data from database
        databaseresults = realm.where(CategoryEntity.class).findAll();
        if(databaseresults.isEmpty()) {
            globalUrl = SharedRadioClass.getMysingleobject().globalurl;
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            makeJsonArrayRequest();
        }

        //By clicking on the category name go to the streamlist activity with the category index,name
        listviewforcategorylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryindex = databaseresults.get(i).getId().toString();
                categoryname = databaseresults.get(i).getTitle().toString();
                startActivity(new Intent(MainActivity.this,StreamList.class).putExtra("Categoryindex", categoryindex)
                        .putExtra("Categoryname", categoryname));
            }
        });

        //refresh image for calling the volley liabrary if any category is added
        imageViewforrefreshbutton = (ImageView)findViewById(R.id.imageViewfor_songimage) ;
        imageViewforrefreshbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait...");
                pDialog.setCancelable(false);
                makeJsonArrayRequest();
            }
        });

    }
    public void makeJsonArrayRequest() {
        //show the dialog before getting the json response
        showpDialog();

        String urlJsonArry = globalUrl+"categories?token=3d4764dcfedc50c561564a45d1";


        JsonArrayRequest req = new JsonArrayRequest(urlJsonArry,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            handleJsonResponse(response);
                        } catch (Exception e) {
                            Log.d("Exception", e.toString());
                        }
                    //hide the process dialog agter getting the json response
                        hidepDialog();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("error", "Error: " + error.getMessage());
                hidepDialog();
            }
        });

        // Adding request to request queue
        MyApplication.getInstance().addToRequestQueue(req);
    }

    private void handleJsonResponse(JSONArray response) {
        try {
            ArrayList<Map<String, Object>> mapArrayList = (ArrayList<Map<String, Object>>) JSonHelper.toList(response);
            realm.beginTransaction();
            for (Map<String, Object> category : mapArrayList) {
                String id = String.valueOf(category.get("id"));
                CategoryEntity categoryEntity = realm.where(CategoryEntity.class).equalTo("id", id).findFirst();
                if(categoryEntity == null) {
                    //chek if categoryEntity is null then create new realm object and store title and id in the database
                    categoryEntity = realm.createObject(CategoryEntity.class);
                    categoryEntity.updateWithData(category);
                }
            }
            realm.commitTransaction();
            adapterforcategorylist.updateData();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showpDialog() {
        //show the process dialog
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        //hide thye process dialog
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //get the streamname and streamurl from the SharedRadioClass which is set in streamlistclass
        streamname = SharedRadioClass.getMysingleobject().streamname;
        streamurl = SharedRadioClass.getMysingleobject().url;

        if(streamurl != null && streamname != null){
            imageviewforstreamimage = (ImageView) findViewById(R.id.imageViewforstreamimage);
            textviewforstreamname = (TextView) findViewById(R.id.textViewforstreamname);
            textviewforstreamname.setText(streamname);
            if (streamurl != null) {
                String url = streamurl;
                if (url == null) {
                    URI uri1;
                    try {
                        // set the default image if image not available in the database
                        uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                        Picasso.with(MainActivity.this).load(String.valueOf(uri1)).resize(60, 60).centerCrop().into(imageviewforstreamimage);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI uri = new URI(streamurl);
                        Picasso.with(MainActivity.this).load(String.valueOf(uri)).resize(60, 60).centerCrop().into(imageviewforstreamimage);

                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
            //by default pause image show when the activity is shown
            imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpause);
            imageButtonforplaypause.setImageResource(R.drawable.pause);

            //get the setmediaplayervalue which is set in the RadioService class for handle play and pause button
            checkmediaplayervalue = SharedRadioClass.getMysingleobject().setmediaplayervalue;
            if(checkmediaplayervalue == 1)
            {
                imageButtonforplaypause.setImageResource(R.drawable.pause);

            }
            else if(checkmediaplayervalue == 0)
            {
                imageButtonforplaypause.setImageResource(R.drawable.play1);
            }

            textviewforstreamname.setOnClickListener(null);
            imageviewforstreamimage.setOnClickListener(null);

            //after set the streamname and image show the layout if name and image available
            layoutforstreamimageandname = (RelativeLayout) findViewById(R.id.layoutforhandlingplayandpause);
            layoutforstreamimageandname.setVisibility(View.VISIBLE);

            //imagebutton for play and pause stream
            imageButtonforplaypause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(toogleforplaypausebutton == 0)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.play1);
                        toogleforplaypausebutton =1;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position",-2);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                    else if(toogleforplaypausebutton == 1)
                    {
                        ((ImageButton) v).setImageResource(R.drawable.pause);
                        toogleforplaypausebutton =0;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position",-1);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                }
            });

        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //set the play and pause button when the activity is open and play and pause change in the notificatio panel
        setmadiavalueonpause = SharedRadioClass.getMysingleobject().setvaluewhenonpause;
        if(hasFocus)
        {
            if(setmadiavalueonpause == 1)
            {
                imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpause);
                imageButtonforplaypause.setImageResource(R.drawable.pause);
            }
            else if(setmadiavalueonpause == 0)
            {
                imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpause);
                imageButtonforplaypause.setImageResource(R.drawable.play1);
            }
        }
    }
}
