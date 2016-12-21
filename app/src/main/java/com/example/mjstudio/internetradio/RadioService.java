package com.example.mjstudio.internetradio;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;


import io.realm.Realm;
import io.realm.RealmResults;


public class RadioService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    String streamurl,position,streamname,categoryName;
    int out=0;
    Notification notification;
    int categoryidforstartstream;
    RealmResults<StreamEntity> databaseresults;
    Realm realm;

    private MediaPlayer player = new MediaPlayer();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public  void  startMusicPlayer()
    {
        //set the alertbox value for alertdialog if stream is not playing
        SharedRadioClass.getMysingleobject().alertbox = 0;

        //initialize the database
        realm = Realm.getDefaultInstance();

        //get all the data of the perticular id
        databaseresults = realm.where(StreamEntity.class).equalTo("catid",position).findAll();
        streamurl = databaseresults.get(categoryidforstartstream).getStreamurl();
        streamname = databaseresults.get(categoryidforstartstream).getStreamname();
        out=1;

        if (player.isPlaying()) {
            player.stop();
            player.reset();

        } else
            player.reset();
        try {

            player.setDataSource(streamurl);

        } catch (Exception e) {
            e.printStackTrace();
        }
        player.prepareAsync();

    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void pausePlayer(){
        //stop the playing stream when click on the pause button in the notification panel
        if(player.isPlaying())
            player.pause();
        SharedRadioClass.getMysingleobject().setmediaplayervalue = 0;
        SharedRadioClass.getMysingleobject().setvaluewhenonpause = 0;
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

        bigViews.setTextViewText(R.id.status_bar_album_name, categoryName);

        notification = new Notification.Builder(this).build();
        notification.contentView = views;
        notification.bigContentView = bigViews;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.drawable.radio;
        notification.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);


    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void playmediaplayer(){
        //play the playing stream when click on the play button in the notification panel
        player.start();
        SharedRadioClass.getMysingleobject().setvaluewhenonpause = 1;
        SharedRadioClass.getMysingleobject().setmediaplayervalue = 1;

        RemoteViews views = new RemoteViews(getPackageName(),
                R.layout.status_bar);
        RemoteViews bigViews = new RemoteViews(getPackageName(),
                R.layout.staus_bar_expanded);


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

        bigViews.setTextViewText(R.id.status_bar_album_name, categoryName);

        notification = new Notification.Builder(this).build();
        notification.contentView = views;
        notification.bigContentView = bigViews;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.drawable.radio;
        notification.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
            //get the category index send from the streamlistclass
            position = intent.getStringExtra("setvaluecategory");
            categoryName = intent.getStringExtra("categoryName");
            //get the streamlist item index send from the streamlistclass
            categoryidforstartstream = intent.getIntExtra("position", 0);
            if (categoryidforstartstream == -1 && out ==1)
                playmediaplayer();
            else if (categoryidforstartstream == -2 && out == 1)
                pausePlayer();
            else {
                initRadioPlayer();
                Toast.makeText(this, "please wait stream is preparing...", Toast.LENGTH_SHORT).show();
            }
        } else if (intent.getAction().equals(Constants.ACTION.PREV_ACTION)) {


        } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
            if(player.isPlaying()) {
                //notification panel action for pause the player
                pausePlayer();
            }
            else
            {
                //notification panel action for play the player
               playmediaplayer() ;
            }

        } else if (intent.getAction().equals(Constants.ACTION.NEXT_ACTION)) {

        } else if (intent.getAction().equals(Constants.ACTION.STOPFOREGROUND_ACTION)) {
           //cancel the the notififation from notfication panel  when click on the close button
            stopForeground(true);
            //stop the service
            stopSelf();
            //get out of the app
            System.exit(0);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }
    //phonestatelistner when the incoming and dialing phone calls on that time  pause the player
    PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (state == TelephonyManager.CALL_STATE_RINGING) {
                //Incoming call: Pause music
                player.pause();
            } else if(state == TelephonyManager.CALL_STATE_IDLE) {
                //Not in call: Play music
                player.start();
            } else if(state == TelephonyManager.CALL_STATE_OFFHOOK) {
                //A call is dialing, active or on hold
                player.pause();
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    };

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
        startMusicPlayer();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        //set the alertbox value for alertdialog if stream is playing
        SharedRadioClass.getMysingleobject().alertbox = 1;
       //start the player
        mediaPlayer.start();

        TelephonyManager mgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if(mgr != null) {
            mgr.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
        showNotification();
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

        bigViews.setTextViewText(R.id.status_bar_album_name, categoryName);

        notification = new Notification.Builder(this).build();
        notification.contentView = views;
        notification.bigContentView = bigViews;
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notification.icon = R.drawable.radio;
        notification.contentIntent = pendingIntent;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE, notification);
    }

}
