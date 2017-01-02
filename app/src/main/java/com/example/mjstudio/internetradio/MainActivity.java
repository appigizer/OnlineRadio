package com.example.mjstudio.internetradio;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;


public class MainActivity extends AppCompatActivity implements AdapterCategory.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener{

    Realm realm;
    private ProgressDialog pDialog;
    AdapterCategory adapterforcategorylist;
    private ArrayList<CategoryPostEntity> beanPostArrayList;
    RealmResults<CategoryEntity> databaseresults;
    String streamname,streamurl;
    String globalUrl;
    RelativeLayout layoutforstreamimageandname;
    TextView textviewforstreamname;
    ImageView imageviewforstreamimage;
    ImageButton imageButtonforplaypause;
    private MediaPlayer mediaPlayer;
    private final String category_image_urls[] = {"https://cdn.devality.com/station/38654/influx_radio_2.jpg",
    "https://cdn.devality.com/station/38920/Pirate_Radio_BIG_Color.jpg",
    "https://cdn.devality.com/station/38899/90s90s_hits_600x600.png",
    "https://cdn.devality.com/station/38921/Pirate_Radio_Talk.JPG",
    "https://cdn.devality.com/station/38846/logo_nrp4iTunes.jpg",
    "https://cdn.devality.com/station/38645/2_loco_radio_submissions.jpg",
    "https://cdn.devality.com/station/38697/Koaticradio__2_.png",
    "https://cdn.devality.com/station/38941/18_Karat_Reggae.jpg",
    "https://cdn.devality.com/station/38550/fullblast-sq_logo.png",
    "https://cdn.devality.com/station/38846/logo_nrp4iTunes.jpg",
    "https://cdn.devality.com/station/38707/iHE25K6UA.jpg",
    "https://cdn.devality.com/station/38922/livemixfiltermusic.png",
    "https://cdn.devality.com/station/38511/amor1043banner.png",
    "https://cdn.devality.com/station/38845/delta_heavymetal-xmas_600x600.png",
    "https://cdn.devality.com/station/38893/LFM_Logo_Vector_Format.png",
    "https://cdn.devality.com/station/38840/Website-Logo.png",
    "https://cdn.devality.com/station/38651/LOGO.png",
    "https://cdn.devality.com/station/38639/Logo-quadrato.jpg",
    "https://cdn.devality.com/station/37793/3.png",
    "https://cdn.devality.com/station/37958/jukeboxjpg.png",
    "https://cdn.devality.com/station/38664/HeadFM_LOGO_300x300.png",
    "https://cdn.devality.com/station/38057/organradio_small.jpg",
    "https://cdn.devality.com/station/38481/Wave_FM_HD2_Logo__JPG_.jpg",
    "https://cdn.devality.com/station/23452/AD175.png",
    "https://cdn.devality.com/station/38677/SwirlSoundz_logo_final_symbol_web.png",
    "https://cdn.devality.com/station/38590/rbxxlarge.png",
    "https://cdn.devality.com/station/28100/j6Gnmx5A.jpg",
    "https://cdn.devality.com/station/38631/1400x1400_80s80s-NDW_colored.png",
    "https://cdn.devality.com/station/37787/ghhradio300.png",
    "https://cdn.devality.com/station/38257/powerplayjpop16.jpg",
    "https://cdn.devality.com/station/38661/CRLogo.png",
    "https://cdn.devality.com/station/38781/icon_512x512_2x.png",
    "https://cdn.devality.com/station/38415/danubius.jpg",
    "https://cdn.devality.com/station/38869/flashback-logo.jpg",
    "https://cdn.devality.com/station/38302/80s.jpg",
    "https://cdn.devality.com/station/38867/nova.jpg",
    "https://cdn.devality.com/station/38651/LOGO.png",
    "https://cdn.devality.com/station/38308/RSH_Gold_600x600.png",
    "https://cdn.devality.com/station/38948/A_LOGO_PERFIL.jpg",
    "https://cdn.devality.com/station/38802/SD-01.jpg",
    "https://cdn.devality.com/station/38726/logoRFR.jpg",
    "https://cdn.devality.com/station/38515/p3.png",
    "https://cdn.devality.com/station/38942/SatyaRadiosms.jpg",
    "https://cdn.devality.com/station/38383/download.png",
    "https://cdn.devality.com/station/28010/folkfwd-400.jpg",
    "https://cdn.devality.com/station/28140/ALoe_Logo.png",
    "https://cdn.devality.com/station/38844/Radio_Pic.png",
    "https://cdn.devality.com/station/35437/radiooo.jpg",
    "https://cdn.devality.com/station/37811/logo.jpg",
    "https://cdn.devality.com/station/38873/tango_logo.jpg",
    "https://cdn.devality.com/station/37922/A2O_logo__300x225_.jpg",
    "https://cdn.devality.com/station/28336/fifties_breezes_title_image.png",
    "https://cdn.devality.com/station/13970/Radio_Carousel_Cover_Art.jpg",
    "https://cdn.devality.com/station/38273/kpop_girls_club.png",
    "https://cdn.devality.com/station/37651/gaypopnewlogo.png",
    "https://cdn.devality.com/station/38946/junction-poland-radio-logo-1600x1600px.png",
    "https://cdn.devality.com/station/38755/b_header.jpg",
    "https://cdn.devality.com/station/38945/REVELAvazado.png",
    "https://cdn.devality.com/station/38946/junction-poland-radio-logo-1600x1600px.png",
    "https://cdn.devality.com/station/19616/taal.jpg",
    "https://cdn.devality.com/station/37787/ghhradio300.png",
    "https://cdn.devality.com/station/27918/RADIO_88_yellow_stamp.jpg",
    "https://cdn.devality.com/station/37937/icon.jpeg",
    "https://cdn.devality.com/station/37878/NewMandarinLogonotag300.jpg",
    "https://cdn.devality.com/station/12831/AirFM.png",
    "https://cdn.devality.com/station/38623/8018_-_The_Very_Best_Of_Ambient_Music_copy__300x300_.png",
    "https://cdn.devality.com/station/38668/Climax_Logo_-_128.png",
    "https://cdn.devality.com/station/38330/weapon.png",
    "https://cdn.devality.com/station/38949/Detroit-Skyline_WRJR_2015_1_for_Site.jpg",
    "https://cdn.devality.com/station/38502/LogoRiddim.jpg",
    "https://cdn.devality.com/station/38492/radio-logo.jpg",
    "https://cdn.devality.com/station/38253/STR_Logo_Square-300x300.jpg",
    "https://cdn.devality.com/station/38742/New_HOB.jpg"};
    int toogleforplaypausebutton = 0, checkmediaplayervalue,setmadiavalueonpause,index=0,showdialog,first=1;
    public RecyclerView recyclerView;


    private SwipeRefreshLayout mSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(MainActivity.this);
        //initialize the realm database
        realm = Realm.getDefaultInstance();
        //initialize the toobar
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);


        recyclerView = (RecyclerView)findViewById(R.id.listViewforcategories);
        recyclerView.setHasFixedSize(true);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),3);
            recyclerView.setLayoutManager(layoutManager);
            adapterforcategorylist = new AdapterCategory(this);
            adapterforcategorylist.listenerCategory =  this;
            recyclerView.setAdapter(adapterforcategorylist);
            // Portrait Mode
        } else {
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),4);
            recyclerView.setLayoutManager(layoutManager);
            adapterforcategorylist = new AdapterCategory(this);
            adapterforcategorylist.listenerCategory =  this;
            recyclerView.setAdapter(adapterforcategorylist);
            // Landscape Mode
        }


        //get database results and check if data is not available in database then call the volley liabrary for getting the jsondata and put in database first time
        //next time get data from database
        databaseresults = realm.where(CategoryEntity.class).findAll();
        if(databaseresults.isEmpty()) {
           showdialog = 0;
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            makeJsonArrayRequest();
        }




    }
    public void makeJsonArrayRequest() {
        //show the dialog before getting the json response
        if(showdialog == 0)
        showpDialog();
        globalUrl = SettingsManager.getSharedInstance().globalurl;
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
                        mSwipeRefreshLayout.setRefreshing(false);
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

        //   ArrayList<Map<String, Object>> mapArrayList = (ArrayList<Map<String, Object>>) JSonHelper.toList(response);

//            for (Map<String, Object> category : mapArrayList) {
//                String id = String.valueOf(category.get("id"));
//                CategoryEntity categoryEntity = realm.where(CategoryEntity.class).equalTo("id", id).findFirst();
//                if(categoryEntity == null) {
//                    //chek if categoryEntity is null then create new realm object and store title and id in the database
//                    categoryEntity = realm.createObject(CategoryEntity.class);
//                    categoryEntity.updateWithData(category);
//                }
//            }

        Type listType = new TypeToken<ArrayList<CategoryPostEntity>>() {}.getType();
        beanPostArrayList = new GsonBuilder().create().fromJson(String.valueOf(response), listType);
        realm.beginTransaction();
        for (CategoryPostEntity post : beanPostArrayList) {
            String id = String.valueOf(post.getId());
            CategoryEntity categoryEntity = realm.where(CategoryEntity.class).equalTo("id", id).findFirst();
            if (categoryEntity == null) {
                categoryEntity = realm.createObject(CategoryEntity.class);
                categoryEntity.setCategoryimage(category_image_urls[index]);
                categoryEntity.updateWithGsonData((post));
                index++;

            }
        }
        realm.commitTransaction();
        adapterforcategorylist.updateData();
    }
    private void showpDialog() {
        //show the process dialog
        if(showdialog == 0) {
            if (!pDialog.isShowing())
                pDialog.show();
        }
    }
    private void hidepDialog() {
        //hide thye process dialog
        if(showdialog == 0) {
            if (pDialog.isShowing())
                pDialog.dismiss();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }
    @Override
    protected void onResume() {


        //get the streamname and streamurl from the SettingsManager which is set in streamlistclass
        streamname = SettingsManager.getSharedInstance().streamname;
        streamurl = SettingsManager.getSharedInstance().url;
        if(streamname == null) {
            imageButtonforplaypause = (ImageButton) findViewById(R.id.imageButtonforplayandpause);
            imageButtonforplaypause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (first == 0) {
                        ((ImageButton) v).setImageResource(R.drawable.play1);
                        first = 1;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position", -2);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    } else if (first == 1) {
                        ((ImageButton) v).setImageResource(R.drawable.pause);
                        first = 0;
                        Intent serviceIntent = new Intent(MainActivity.this, RadioService.class).putExtra("position",-1).putExtra("first",-5);
                        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                        startService(serviceIntent);
                    }
                }
            });
        }

        if(streamname != null){
            imageviewforstreamimage = (ImageView) findViewById(R.id.imageViewforstreamimage);
            textviewforstreamname = (TextView) findViewById(R.id.textViewforstreamname);
            textviewforstreamname.setText(streamname);
            String url = streamurl;
            if (url == null) {
                URI defaultUri;
                    try {
                        // set the default image if image not available in the database
                        defaultUri = new URI("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQCxumxjDnEtPCvN-_gVUbcELLnEj36_BJGJk5KsWTH5itj1saK");
                        Picasso.with(MainActivity.this).load(String.valueOf(defaultUri)).resize(60, 60).centerCrop().into(imageviewforstreamimage);
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

            //by default pause image show when the activity is shown
            imageButtonforplaypause = (ImageButton)findViewById(R.id.imageButtonforplayandpause);
            imageButtonforplaypause.setImageResource(R.drawable.pause);

            //get the setmediaplayervalue which is set in the RadioService class for handle play and pause button
            checkmediaplayervalue = SettingsManager.getSharedInstance().setmediaplayervalue;
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

            ViewGroup.LayoutParams params=mSwipeRefreshLayout.getLayoutParams();
//            params.height=1000;
//            mSwipeRefreshLayout.setLayoutParams(params);
//            layoutforstreamimageandname = (RelativeLayout) findViewById(R.id.layoutforhandlingplayandpause);
//            layoutforstreamimageandname.setVisibility(View.VISIBLE);

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
        super.onResume();
    }
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //set the play and pause button when the activity is open and play and pause change in the notificatio panel
        setmadiavalueonpause = SettingsManager.getSharedInstance().setvaluewhenonpause;
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
    @Override
    public void startService() {}
    @Override
    public void onRefresh() {
        showdialog = 1;
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        makeJsonArrayRequest();
    }

}
