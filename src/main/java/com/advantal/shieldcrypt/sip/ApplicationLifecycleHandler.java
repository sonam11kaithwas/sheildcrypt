package com.advantal.shieldcrypt.sip;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks, ComponentCallbacks2 {

    private static final String TAG = ApplicationLifecycleHandler.class.getSimpleName();

    public static boolean isViewMedia = false;
    public static boolean isAppLockerOpen = false;
    public static boolean isInBackground = true;
    public static boolean isSharingApp = false;


    int type = 121;
    String isLogin;
    String isapplock ;
    SharedPrefrence share;

    Context ctx;

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityStarted(Activity activity) {
    }

    @Override
    public void onActivityResumed(Activity activity) {

        share = SharedPrefrence.getInstance(activity);
        ctx = activity;

        if (isInBackground) {

          // PermissionFramework.configurePermissions(activity);

           /* isLogin = share.getValue(SharedPrefrence.ISLOGIN);
            isapplock = share.getValue(SharedPrefrence.SHOW_SCREEN_LOCK);

            if (isapplock.equals(SharedPrefrence.TRUE) && isLogin.equals("true") && share.getValue(SharedPrefrence.IS_SPLASH).equals("false") && isSharingApp == false && isAppLockerOpen == false && isViewMedia == false) {

                Intent in = new Intent(activity, AppLockActivity.class);
                in.putExtra(AppLock.TYPE, AppLock.UNLOCK_PASSWORD);
                activity.startActivityForResult(in, type);
            } else {
                isViewMedia = false;
            }
            isInBackground = false;
            isSharingApp = false;*/
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
    }

    @Override
    public void onLowMemory() {
    }

    @Override
    public void onTrimMemory(int i) {
        if (i == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.e(TAG, "app went to background");

            freeMemory();
            deleteCache(ctx);
            isInBackground = true;

        }
    }


    public  void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public  boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    public void freeMemory(){

        try {
            System.runFinalization();
            Runtime.getRuntime().gc();
            System.gc();
        }catch (Exception e)
        {
        }

    }
}