package com.advantal.shieldcrypt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.tabs_pkg.TabLayoutFragment;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;


import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipServiceCommand;




//Created by SV 13-06-22
public class BackgroundConnectionService extends Service {
    public static final String CHANNEL_ID = "exampleServiceChannel";
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;
    SharedPrefrence mySharedPreferences;
    SharedPrefrence share;
    //For sip pram
    private SipAccountData mSipAccount;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mySharedPreferences = SharedPrefrence.getInstance(this);
        share = SharedPrefrence.getInstance(this);

        createNotificationChannel();
        Intent notificationIntent = new Intent(this, TabLayoutFragment.class);
        /*PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);*/
        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {
            pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID).setContentTitle("Running Service")
                //.setContentText("inputExtra")
                .setSmallIcon(R.drawable.notification_icon_grey).setContentIntent(pendingIntent).build();
        startForeground(101, notification);

        //do heavy work on a background thread
        //stopSelf();
        //  Log.d("display", "This method is run every 5 seconds");
        if (Utils.checkInternetConn(this)) {
            onRegister();
            // startServiceToConnectXmpp();
        }

   /*   handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                Toast.makeText(ExampleService.this, "This method is run every 5 seconds",
                        Toast.LENGTH_SHORT).show();
                Log.d("display", "This method is run every 5 seconds");
            }
        }, delay);*/
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, "Example Service Channel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

    }

    // Request for the SIP registration
    public void onRegister() {
        mSipAccount = new SipAccountData();
        System.out.println("))))))) sipip " + mySharedPreferences.getValue(AppConstants.sipIpDynamic));
        System.out.println("))))))) sipport " + mySharedPreferences.getValue(AppConstants.sipPortDynamic));
        mSipAccount.setHost(mySharedPreferences.getValue(AppConstants.sipIpDynamic)).setPort(Integer.parseInt(mySharedPreferences.getValue(AppConstants.sipPortDynamic))).setTcpTransport(false).setUsername(mySharedPreferences.getValue(AppConstants.userName)).setPassword(mySharedPreferences.getValue(AppConstants.userPassword))
//                .setUsername("1000")
//                .setPassword("1000")
                .setRealm(mySharedPreferences.getValue(AppConstants.sipIpDynamic));

        share.setValue(SharedPrefrence.SIPSERVER, mySharedPreferences.getValue(AppConstants.sipIpDynamic));
        share.setintValue(SharedPrefrence.SIPPORT, Integer.parseInt(mySharedPreferences.getValue(AppConstants.sipPortDynamic)));
        share.setValue(SharedPrefrence.SIPUSERNAME, mySharedPreferences.getValue(AppConstants.userName));
        share.setValue(SharedPrefrence.SIPPASS, mySharedPreferences.getValue(AppConstants.userPassword));
//        share.setValue(SharedPrefrence.SIPUSERNAME, "1000");
//        share.setValue(SharedPrefrence.SIPPASS, "1000");
        share.setValue(SharedPrefrence.SIPREALM, mySharedPreferences.getValue(AppConstants.sipIpDynamic));
        SipServiceCommand.setAccount(this, mSipAccount);
        SipServiceCommand.setReRegisterAccount(this, mSipAccount);
        SipServiceCommand.getCodecPriorities(this);

     /*   if (XMPPConnectionListener.Companion.getMConnection()
                != null) {
            if (!XMPPConnectionListener.Companion.getMConnection().isConnected()) {
                try {
                    XMPPConnectionListener.Companion.getMConnection().connect();
                } catch (SmackException | IOException | XMPPException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }
}

/*

    Notification notification = new Notification(R.drawable.icon, getText(R.string.ticker_text), System.currentTimeMillis());
    Intent notificationIntent = new Intent(this, ExampleActivity.class);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

    notification.setLatestEventInfo(this, getText(R.string.notification_title),
            getText(R.string.notification_message), pendingIntent);

            startForeground(ONGOING_NOTIFICATION_ID, notification);*/