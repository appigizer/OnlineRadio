package com.example.mjstudio.internetradio;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

import static java.security.AccessController.getContext;

/**
 * Created by mjstudio on 08/12/16.
 */

public class ArrayStationAdapter extends BaseAdapter {

    private LayoutInflater songInf;
    Realm realm;
    String catid,imageurl;
    int red,cat;

    RealmResults<StreamObjectList> streamList;



    public ArrayStationAdapter(Activity activity, String category_id) {
        realm = Realm.getDefaultInstance();
        this.catid = category_id;
        updateData();
    }

    public void updateData(){
        streamList =realm.where(StreamObjectList.class).equalTo("catid",catid).findAll();
        notifyDataSetChanged();

    }


    @Override
    public int getCount() {
        Log.d("Background Task", "data14=============>>" +streamList.size());
        return streamList.size();

    }

    @Override
    public Object getItem(int arg0) {

        return null;
    }

    @Override
    public long getItemId(int arg0) {

        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //map to song layout
        songInf = LayoutInflater.from(parent.getContext());
        final LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.channellist, parent, false);
        //get title and artist views
        TextView songView = (TextView) songLay.findViewById(R.id.text);
        ImageView imageView = (ImageView) songLay.findViewById(R.id.imageView);

        StreamObjectList streams = streamList.get(position);
//        Bitmap icon = BitmapFactory.decodeResource(parent.getContext().getResources(),
//                R.drawable.radio);
//        imageViewbutton.setImageBitmap(icon);
//
        red = SharedRadioClass.getMysingleobject().redurl;
        cat = SharedRadioClass.getMysingleobject().category;
        if(red == position && cat == Integer.parseInt(catid)) {
            ImageView imageView1 = (ImageView) songLay.findViewById(R.id.imageyesno);
            StreamObjectList image = streamList.get(red);
            imageurl = image.getRedurl();
            //imageView1.setImageResource(R.drawable.red);
                                try {
                URI uri = new URI(imageurl);
                Picasso.with(parent.getContext())
                        .load(String.valueOf(uri))
                        .resize(15, 15)
                        .centerCrop()
                        .into(imageView1);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            SharedRadioClass.getMysingleobject().category = Integer.parseInt(catid);
        }
//                    try {
//                URI uri = new URI("http://www.iconsdb.com/icons/preview/green/circle-xxl.png");
//                Picasso.with(parent.getContext())
//                        .load(String.valueOf(uri))
//                        .resize(10, 10)
//                        .centerCrop()
//                        .into(imageView1);
//            } catch (URISyntaxException e) {
//                e.printStackTrace();
//            }

        if (streams != null) {
            songView.setText(streams.getStreamname());
        }
        if (streams != null) {
            String url = streams.getImageurl();

            if (url == null) {
                URI uri1;
                try {
                    uri1 = new URI("https://cdn.devality.com/station/38392/logo.gif");
                    Picasso.with(parent.getContext()).load(String.valueOf(uri1)).resize(70, 70).centerCrop().into(imageView);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }


            } else {
                try {
                    URI uri = new URI(streams.getImageurl());
                    Picasso.with(parent.getContext()).load(String.valueOf(uri)).resize(70, 70).centerCrop().into(imageView);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            }
        }
        songLay.setTag(position);
        return songLay;
    }

}