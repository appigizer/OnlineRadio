package com.example.mjstudio.internetradio;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URI;
import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mjstudio on 08/12/16.
 */

public class ArrayStationAdapter extends BaseAdapter {

    private LayoutInflater songInf;
    Realm realm;
    String category_id,imageurl;
    int redimageurlvalue, categorysetforredimage;

    RealmResults<StreamEntity> streamList;

    public ArrayStationAdapter(Activity activity, String category_id) {
        realm = Realm.getDefaultInstance();
        this.category_id = category_id;
        updateData();
    }

    public void updateData(){
        streamList =realm.where(StreamEntity.class).equalTo("catid", category_id).findAll();
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

        songInf = LayoutInflater.from(parent.getContext());
        final LinearLayout songLay = (LinearLayout) songInf.inflate(R.layout.channellist, parent, false);

        TextView songView = (TextView) songLay.findViewById(R.id.textViewforstreamsname);
        ImageView imageView = (ImageView) songLay.findViewById(R.id.imageViewfor_songimage);

        StreamEntity streams = streamList.get(position);

        redimageurlvalue = SharedRadioClass.getMysingleobject().redimageurl;
        categorysetforredimage = SharedRadioClass.getMysingleobject().category;
        if(redimageurlvalue == position && categorysetforredimage == Integer.parseInt(category_id)) {
           ImageView imageView1 = (ImageView) songLay.findViewById(R.id.imageViewforredimage);
            StreamEntity image = streamList.get(redimageurlvalue);
            imageurl = image.getRedurl();
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
            SharedRadioClass.getMysingleobject().category = Integer.parseInt(category_id);
        }

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