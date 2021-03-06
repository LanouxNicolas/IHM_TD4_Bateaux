package com.ihm.seawatch.fragments;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Objects;

public class Notifications extends Application {

    public static final String CHANNEL_1_ID = "Channel Low";
    public static final String CHANNEL_2_ID = "Channel Default";
    public static final String CHANNEL_3_ID = "Channel High";

    private NotificationChannel createNotificationChannel(String channelId, CharSequence name, int importance, String channelDescription) {
        // Créer le NotificationChannel, seulement pour API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(channelDescription);
            return channel;
        }
        return null;
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {  // Créer le NotificationChannel, seulement pour API 26+
            NotificationChannel channel1 = createNotificationChannel(CHANNEL_1_ID,"Channel 1",
                    NotificationManager.IMPORTANCE_LOW,"This channel has low priority");
            NotificationChannel channel2 = createNotificationChannel(CHANNEL_2_ID,"Channel 2",
                    NotificationManager.IMPORTANCE_DEFAULT,"This channel has default priority");
            NotificationChannel channel3 = createNotificationChannel(CHANNEL_3_ID,"Channel 2",
                    NotificationManager.IMPORTANCE_HIGH,"This channel has high priority");

            // Enregister le canal sur le système : attention de ne plus rien modifier après
            NotificationManager manager = getSystemService(NotificationManager.class);
            Objects.requireNonNull(manager).createNotificationChannel(channel1);
            Objects.requireNonNull(manager).createNotificationChannel(channel2);
            Objects.requireNonNull(manager).createNotificationChannel(channel3);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }
}