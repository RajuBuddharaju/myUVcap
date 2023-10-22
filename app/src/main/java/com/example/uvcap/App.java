package com.example.uvcap;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    public static int mytime = 0;
    public static int spfTimer = 0;
    public static final String CHANNEL_1_ID = "Dosage Notifications";
    public static final String CHANNEL_2_ID = "UV Notifcations";
    public static final String CHANNEL_3_ID = "SPF Notifcations";

    public static boolean indexNotifSent = false;
    public static boolean eightyNotified = false;
    public static boolean hunderedNotified = false;

    public static boolean spfSet = false;


    public static float UVIndex;
    public static double UVDosage;
    // The recommend UV Dosage for skin type selected
    public static double recUVDosage = 50;
    public static double SPF = 1;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Dosage Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("These are notifications related to UV Dosage");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "UV Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("These are notifications related to UV Index");

            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "SPF Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("These are notifications related to SPF");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);
        }
    }
}
