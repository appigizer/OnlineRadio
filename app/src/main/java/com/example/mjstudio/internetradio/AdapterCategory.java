package com.example.mjstudio.internetradio;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by rp on 5/12/16.
 */

public class AdapterCategory extends BaseAdapter {
    private LayoutInflater songInf;



    Realm realm;
    RealmResults<Categories> categoryList;

    public AdapterCategory(Activity activity) {
        realm = Realm.getDefaultInstance();
        updateData();
    }

    public void updateData(){
        categoryList = realm.where(Categories.class).findAll();
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {

        return categoryList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        songInf=LayoutInflater.from(parent.getContext());
       RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.category, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_category);
        Categories categories = categoryList.get(position);

        if (categories != null){
            songView.setText(categories.getTitle());
        }
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}