package com.advantal.shieldcrypt.globalexceptionhandler;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = MyExceptionHandler.class.getSimpleName();
    public static final String EXTRA_MY_EXCEPTION_HANDLER = "EXTRA_MY_EXCEPTION_HANDLER";
    private final Activity context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    public MyExceptionHandler(Activity context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        if (ex instanceof MyAuthException) {
            // note we can't just open in Android an dialog etc. we have to use Intents here
            // http://stackoverflow.com/questions/13416879/show-a-dialog-in-thread-setdefaultuncaughtexceptionhandler

            Intent registerActivity = new Intent(context, AuthActivity.class);
            registerActivity.putExtra(EXTRA_MY_EXCEPTION_HANDLER, MyExceptionHandler.class.getName());
            registerActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            registerActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);

            context.startActivity(registerActivity);
            // make sure we die, otherwise the app will hang ...
            android.os.Process.killProcess(android.os.Process.myPid());
            // sometimes on older android version killProcess wasn't enough -- strategy pattern should be considered here
            System.exit(0);
        } else {
            Log.e(TAG, "readExceptions uncaughtException  ->> "+ ex.getMessage());
            rootHandler.uncaughtException(thread, ex);
        }
    }
}