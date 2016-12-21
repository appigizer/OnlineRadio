package com.example.mjstudio.internetradio;


import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by rp on 5/12/16.
 */

public class AdapterCategory extends BaseAdapter {
    private LayoutInflater songInf;
    Realm realm;
    RealmResults<CategoryEntity> categoryList;

    public AdapterCategory(Activity activity) {
        realm = Realm.getDefaultInstance();
        updateData();
    }

    public void updateData(){
        categoryList = realm.where(CategoryEntity.class).findAll();
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

        songInf=LayoutInflater.from(parent.getContext());
        RelativeLayout songLay = (RelativeLayout) songInf.inflate(R.layout.category, parent, false);

        TextView songView = (TextView)songLay.findViewById(R.id.textViewfor_categoriesname);
        CategoryEntity categoryEntity = categoryList.get(position);

        if (categoryEntity != null){
            Log.d("URL", "URLid" + categoryEntity.getTitle());
            songView.setText(categoryEntity.getTitle());
        }
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}