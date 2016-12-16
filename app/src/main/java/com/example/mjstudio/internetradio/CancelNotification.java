package com.example.mjstudio.internetradio;




import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.widget.Toast;

public class CancelNotification extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

//        int notificationId = intent.getIntExtra("notificationId", 1);
//       // Log.d("notification","notivalue"+notificationId);
//        // Do what you want were.
//
//        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.cancel(notificationId);
//        context.stopService(new Intent(context,RadioService.class));
//        System.exit(0);
        String action = intent.getAction();
        if (AppConstant.YES_ACTION.equals(action)) {
            context.startService(new Intent(context,RadioService.class).putExtra("position",-1));
            Toast.makeText(context, "PLAY CALLED", Toast.LENGTH_SHORT).show();

        }
        else  if (AppConstant.STOP_ACTION.equals(action)) {
            context.startService(new Intent(context,RadioService.class).putExtra("position",-2));
            Toast.makeText(context, "PAUSE CALLED", Toast.LENGTH_SHORT).show();
        }
        else  if (AppConstant.CLEAR_ACTION.equals(action)) {
            context.startService(new Intent(context,RadioService.class).putExtra("position",-3));
            Toast.makeText(context, "CLEAR CALLED", Toast.LENGTH_SHORT).show();

        }
    }
}