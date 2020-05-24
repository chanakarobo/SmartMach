package com.example.smartmobilevehicle;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

public class NotificationApplication extends Application {

    public static final String CHANNEL_1_ID="channel1";


    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels(){

        

        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){

            NotificationChannel channel =new NotificationChannel(
                    CHANNEL_1_ID,
                    "chanel 1",
                    NotificationManager.IMPORTANCE_HIGH

            );
            channel.setDescription("This is notification channel");
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[] {0,1000});

            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


    }

}
