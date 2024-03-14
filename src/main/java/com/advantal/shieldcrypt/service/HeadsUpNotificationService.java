package com.advantal.shieldcrypt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity;
import com.advantal.shieldcrypt.service.HeadsUpNotificationActionReceiver;

import java.util.Objects;

public class HeadsUpNotificationService extends Service {
    private final String CHANNEL_ID = "VoipChannel";
    private final String CHANNEL_NAME = "Voip Channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle data = null;
        if (intent != null && intent.getExtras() != null) {
            data = intent.getBundleExtra("data");
        }
        try {
            Intent receiveCallAction = new Intent(getApplicationContext(), HeadsUpNotificationActionReceiver.class);
            receiveCallAction.putExtra("actionKey", "voip");
//            receiveCallAction.putExtra("data", data);
            receiveCallAction.setAction("RECEIVE_CALL");

            Intent cancelCallAction = new Intent(getApplicationContext(), AuthenticationActivity.class);
            cancelCallAction.putExtra("actionKey", "voip");
//            cancelCallAction.putExtra("data", data);
            cancelCallAction.setAction("CANCEL_CALL");

            /*PendingIntent receiveCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent cancelCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);*/

            PendingIntent receiveCallPendingIntent, cancelCallPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                receiveCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
                cancelCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            } else {
                receiveCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1200, receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
                cancelCallPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1201, cancelCallAction, PendingIntent.FLAG_UPDATE_CURRENT);
            }

            createChannel();
            NotificationCompat.Builder notificationBuilder = null;
//            if (data != null) {
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                        .setContentText(data.getString("remoteUserName"))
                    .setContentText("Call")
                    .setContentTitle("Incoming Voice Call")
                    .setSmallIcon(R.drawable.notification_icon_grey)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_CALL)
                    .addAction(R.drawable.audio_call_brown, "Receive Call", receiveCallPendingIntent)
                    .addAction(R.drawable.audio_call_brown, "Cancel call", cancelCallPendingIntent)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setFullScreenIntent(receiveCallPendingIntent, true);
//            }

            Notification incomingCallNotification = null;
            if (notificationBuilder != null) {
                incomingCallNotification = notificationBuilder.build();
            }
            startForeground(120, incomingCallNotification);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

//    Create noticiation channel if OS version is greater than or eqaul to Oreo


    public void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Call Notifications");
            /*channel.setSound(Uri.parse("android.resource://" + getContext().getPackageName() + "/" + R.raw.voip_ringtone),
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setLegacyStreamType(AudioManager.STREAM_RING)
                            .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION).build());*/
            Objects.requireNonNull(getSystemService(NotificationManager.class)).createNotificationChannel(channel);
        }
    }
}
