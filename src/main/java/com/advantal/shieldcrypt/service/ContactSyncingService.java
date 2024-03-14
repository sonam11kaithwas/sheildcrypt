package com.advantal.shieldcrypt.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class ContactSyncingService extends Service {
    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.v(LOG_TAG, "in stopService");

        return super.stopService(name);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public class MyBinder extends Binder {
        public ContactSyncingService getService() {
            return ContactSyncingService.this;
        }
    }
}
