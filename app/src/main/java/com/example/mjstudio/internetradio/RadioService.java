package com.example.mjstudio.internetradio;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;


import io.realm.Realm;
import io.realm.RealmResults;

import static android.R.attr.action;
import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by mjstudio on 08/12/16.
 */

public class RadioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {


    String url,title,position,streamname;
    int out=0;
    int period = 0;
    Notification status;
    int section,pos;
    RealmResults<StreamObjectList> results;
    Realm realm;
    ArrayList<String> songs = new ArrayList<>();
    private MediaPlayer player = new MediaPlayer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public  void  startMusicPlayer()
    {



        realm = Realm.getDefaultInstance();
        results = realm.where(StreamObjectList.class).equalTo("catid",position).findAll();
        url = results.get(section).getStreamurl();
        streamname = results.get(section).getStreamname();



        out=1;
        if (player.isPlaying()) {
            player.stop();
            player.reset();
            Log.d("Check", "value===========" + "playing");
        } else
            player.reset();
        try {

            player.setDataSource(url);
            // player.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.prepareAsync();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void pausePlayer(){
        if(player.isPlaying())
            player.pause();



    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void go(){

        player.start();

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            position = intent.getStringExtra("category");

            section = intent.getIntExtra("position", 0);
            if (section == -1 && out ==1)
                go();
            else if (section == -2 && out == 1)
                pausePlayer();
            else if(section == -3)
                onDestroy();
            else {
                initRadioPlayer();
                Toast.makeText(this, "please wait stream is preparing...", Toast.LENGTH_SHORT).show();

            }


        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {


        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            if(player.isPlaying()) {
                pausePlayer();
                SharedRadioClass.getMysingleobject().mediaplayer = 0;
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.status_bar);
                RemoteViews bigViews = new RemoteViews(getPackageName(),
                        R.layout.staus_bar_expanded);

                Intent notificationIntent = new Intent(this, MainActivity.class);

                notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Intent previousIntent = new Intent(this, RadioService.class);
                previousIntent.setAction(Constants.ACTION.PREV_ACTION);
                PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                        previousIntent, 0);

                Intent playIntent = new Intent(this, RadioService.class);
                playIntent.setAction(Constants.ACTION.PLAY_ACTION);
                PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                        playIntent, 0);

                Intent nextIntent = new Intent(this, RadioService.class);
                nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
                PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                        nextIntent, 0);

                Intent closeIntent = new Intent(this, RadioService.class);
                closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                        closeIntent, 0);
                views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

                views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

                views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

                views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_play);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_play);
                views.setTextViewText(R.id.status_bar_track_name, "Internet Radio");
                bigViews.setTextViewText(R.id.status_bar_track_name, "Internet Radio");

                views.setTextViewText(R.id.status_bar_artist_name, streamname);
                bigViews.setTextViewText(R.id.status_bar_artist_name, streamname);

                bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

                status = new Notification.Builder(this).build();
                status.contentView = views;
                status.bigContentView = bigViews;
                status.flags = Notification.FLAG_ONGOING_EVENT;
                status.icon = R.drawable.radio;
                status.contentIntent = pendingIntent;
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);

            }
            else
            {
                player.start();
                SharedRadioClass.getMysingleobject().mediaplayer = 1;
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.status_bar);
                RemoteViews bigViews = new RemoteViews(getPackageName(),
                        R.layout.staus_bar_expanded);

// showing default album image
                views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
                views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
                bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                        Constants.getDefaultAlbumArt(this));

                Intent notificationIntent = new Intent(this, MainActivity.class);
                notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                Intent previousIntent = new Intent(this, RadioService.class);
                previousIntent.setAction(Constants.ACTION.PREV_ACTION);
                PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                        previousIntent, 0);

                Intent playIntent = new Intent(this, RadioService.class);
                playIntent.setAction(Constants.ACTION.PLAY_ACTION);
                PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                        playIntent, 0);

                Intent nextIntent = new Intent(this, RadioService.class);
                nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
                PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                        nextIntent, 0);

                Intent closeIntent = new Intent(this, RadioService.class);
                closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
                PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                        closeIntent, 0);

                views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

                views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

                views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

                views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
                bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

                views.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_pause);
                bigViews.setImageViewResource(R.id.status_bar_play,
                        R.drawable.apollo_holo_dark_pause);

                views.setTextViewText(R.id.status_bar_track_name, "Internet Radio");
                bigViews.setTextViewText(R.id.status_bar_track_name, "Internet Radio");

                views.setTextViewText(R.id.status_bar_artist_name, streamname);
                bigViews.setTextViewText(R.id.status_bar_artist_name, streamname);

                bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

                status = new Notification.Builder(this).build();
                status.contentView = views;
                status.bigContentView = bigViews;
                status.flags = Notification.FLAG_ONGOING_EVENT;
                status.icon = R.drawable.radio;
                status.contentIntent = pendingIntent;
                startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
            }

        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {


        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
            Log.d("Check", "value321===========" + songs);
            stopForeground(true);
            stopSelf();
            System.exit(0);

        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    public void initRadioPlayer() {
        //set player properties

        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //  set listeners
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

//        songs = ShareRadioClass.getMysingleobject().streamUrlList;

        startMusicPlayer();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        SharedRadioClass.getMysingleobject().alertbox=1;
        Log.d("category","indexurl"+"rahul");
        mediaPlayer.start();


        showNotification();

//        Intent notIntent = new Intent(this, MainActivity.class);
//        notIntent.putExtra("data1", results.get(section).getStreamname());
//        notIntent.putExtra("data2", results.get(section).getImageurl());
//
//        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
//                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//        Notification.Builder builder = new Notification.Builder(this);
//        builder.setContentIntent(pendInt)
//                .setSmallIcon(R.drawable.radio)
//                .setTicker(results.get(section).getStreamname())
//                .setOngoing(true)
//                .setContentTitle("Playing")
//                .setContentText(results.get(section).getStreamname())
//                .addAction(R.drawable.play1, "play", pendInt)
//                .setAutoCancel(true);
//
//        Notification not = builder.build();
//        startForeground(1, not);

//        NotificationCompat.Builder mBuilder =
//                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.radio)
//                        .setContentTitle("Playing")
//
//                        .setContentText(results.get(section).getStreamname());
//
//        Intent openHomePageActivity = new Intent(this, MainActivity.class);
//        openHomePageActivity.putExtra("data1", results.get(section).getStreamname());
//        openHomePageActivity.putExtra("data2", results.get(section).getImageurl());
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//        stackBuilder.addNextIntent(openHomePageActivity);
//
//        PendingIntent resultPendingIntent =                     PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.addAction(R.drawable.end,"delete",resultPendingIntent);
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        mNotificationManager.notify(0, mBuilder.build());
//        Intent receiverIntent = new Intent(this, MainActivity.class);
//        receiverIntent.putExtra("data1", streamname);
//        receiverIntent.putExtra("data2", url);
//        PendingIntent pReceiverIntent = PendingIntent.getActivity(this, 0, receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder builder;
//        builder = new Notification.Builder(this).setSmallIcon(R.drawable.radio).setAutoCancel(false)
//                .setContentTitle("Playing.....").setContentText(streamname)
//                .setContentIntent(pReceiverIntent);
//
//
//        Intent yesReceive = new Intent();
//        yesReceive.setAction(AppConstant.YES_ACTION);
//        PendingIntent pendingIntentYes = PendingIntent.getBroadcast(this, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.play1, "play", pendingIntentYes);
//
//
//        Intent clearbutton = new Intent();
//        clearbutton.setAction(AppConstant.CLEAR_ACTION);
//        PendingIntent clearbuttonYes = PendingIntent.getBroadcast(this, 12345, clearbutton, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.end, "clear", clearbuttonYes);
//
//
////No intent
//        Intent noReceive = new Intent();
//        noReceive.setAction(AppConstant.STOP_ACTION);
//        PendingIntent pendingIntentNo = PendingIntent.getBroadcast(this, 12345, noReceive, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.addAction(R.drawable.pause, "pause", pendingIntentNo);
//
//
//        Notification notification = builder.build();
//
//        // notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        // notificationManager.notify(1, notification);
//        startForeground(1, notification);
    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
// Using RemoteViews to bind custom layouts into Notification
        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.staus_bar_expanded);

// showing default album image
        views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);
        views.setViewVisibility(R.id.status_bar_album_art, View.GONE);
        bigViews.setImageViewBitmap(R.id.status_bar_album_art,
                Constants.getDefaultAlbumArt(this));

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(Constants.ACTION.MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, RadioService.class);
        previousIntent.setAction(Constants.ACTION.PREV_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, RadioService.class);
        playIntent.setAction(Constants.ACTION.PLAY_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, RadioService.class);
        nextIntent.setAction(Constants.ACTION.NEXT_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Intent closeIntent = new Intent(this, RadioService.class);
        closeIntent.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        PendingIntent pcloseIntent = PendingIntent.getService(this, 0,
                closeIntent, 0);

        views.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_play, pplayIntent);

        views.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_next, pnextIntent);

        views.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_prev, ppreviousIntent);

        views.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);
        bigViews.setOnClickPendingIntent(R.id.status_bar_collapse, pcloseIntent);

        views.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);
        bigViews.setImageViewResource(R.id.status_bar_play,
                R.drawable.apollo_holo_dark_pause);

        views.setTextViewText(R.id.status_bar_track_name, "Internet Radio");
        bigViews.setTextViewText(R.id.status_bar_track_name, "Internet Radio");

        views.setTextViewText(R.id.status_bar_artist_name, streamname);
        bigViews.setTextViewText(R.id.status_bar_artist_name, streamname);

        bigViews.setTextViewText(R.id.status_bar_album_name, "Album Name");

        status = new Notification.Builder(this).build();
        status.contentView = views;
        status.bigContentView = bigViews;
        status.flags = Notification.FLAG_ONGOING_EVENT;
        status.icon = R.drawable.radio;
        status.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, status);
    }

    @Override
    public void onDestroy() {
        //notificationManager.cancel(1);
        stopForeground(false);
        stopSelf();

        System.exit(0);
    }
}
