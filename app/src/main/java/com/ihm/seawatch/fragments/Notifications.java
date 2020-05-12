package com.ihm.seawatch.fragments;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class Notifications extends Application {
    public static final String CHANNEL_ID ="channel1";
    private static NotificationManager notificationManager;

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }


    private void createNotificationChannel(String title, String message, int importance) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,title,importance);
            channel.setDescription(message);
            notificationManager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(channel);

        }
    }

    @Override
    public void onCreate(){
        super.onCreate();
        createNotificationChannel("title","message", NotificationManager.IMPORTANCE_DEFAULT);
    }


}
