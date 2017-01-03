package com.appigizer.internetradio;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.net.URI;
import java.net.URISyntaxException;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by mjstudio on 08/12/16.
 */



public class ArrayStationAdapter extends RecyclerView.Adapter<ArrayStationAdapter.ViewHolder> {

    Context context;
    public interface OnItemClickListener {
        void startService();
    }
    public OnItemClickListener listener;
    Realm realm;
    String category_id,streamname,streamurl;
    RealmResults<StreamEntity> streamList;
    View v;
    private static ImageLoader imageLoader;
    private static  DisplayImageOptions options;

    public ArrayStationAdapter(Activity activity, String category_id) {
        realm = Realm.getDefaultInstance();
        this.category_id = category_id;
        context = activity;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.backgroundicon)
                .showImageForEmptyUri(R.drawable.backgroundicon)
                .showImageOnFail(com.appigizer.internetradio.R.drawable.backgroundicon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        updateData();
    }
    public void updateData(){
        streamList =realm.where(StreamEntity.class).equalTo("catid", category_id).findAllSorted("streamname");
        notifyDataSetChanged();
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        v = LayoutInflater.from(parent.getContext()).inflate(R.layout.channellist, parent, false);

        return new ViewHolder(v);

    }
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final StreamEntity streams = streamList.get(position);

        holder.bind(streams, listener);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                streamname = streams.getStreamname();
                streamurl = streams.getImageurl();
                SettingsManager.getSharedInstance().onclicklistner= true;
                SettingsManager.getSharedInstance().selectedStreamEntity = streams;
                SettingsManager.getSharedInstance().url = streamurl;
                SettingsManager.getSharedInstance().streamname = streamname;

                Intent serviceIntent = new Intent(context, RadioService.class);
                serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
                context.startService(serviceIntent);
                listener.startService();
            }
        });

    }
    @Override
    public int getItemCount() {
        return streamList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView streameName;
        private ImageView imageUrl;
        ImageView redImageUrl;



        public ViewHolder(View itemView) {

            super(itemView);

            redImageUrl = (ImageView)itemView.findViewById(R.id.imageViewforredimage);
            streameName = (TextView) itemView.findViewById(R.id.textViewforstreamsname);
            imageUrl = (ImageView) itemView.findViewById(R.id.imageViewfor_songimage);
        }
        public  void bind(final StreamEntity streamEntity, final OnItemClickListener listener) {

            streameName.setText(streamEntity.getStreamname());

            if (streamEntity!= null) {
                final String url = streamEntity.getImageurl();
                if (url == null) {
                    URI defaulturi;
                    try {
                        defaulturi = new URI("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQCxumxjDnEtPCvN-_gVUbcELLnEj36_BJGJk5KsWTH5itj1saK");

                        imageUrl.setImageResource(R.drawable.backgroundicon);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        URI defaultUrl =  new URI("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQCxumxjDnEtPCvN-_gVUbcELLnEj36_BJGJk5KsWTH5itj1saK");
                        imageLoader.displayImage(url,imageUrl,options);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

