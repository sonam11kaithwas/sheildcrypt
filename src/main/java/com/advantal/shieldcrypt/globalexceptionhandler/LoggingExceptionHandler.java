package com.advantal.shieldcrypt.globalexceptionhandler;

import android.content.Context;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LoggingExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static String TAG = LoggingExceptionHandler.class.getSimpleName();
    private final static String ERROR_FILE = MyAuthException.class.getSimpleName() + ".error";

    private final Context context;
    private final Thread.UncaughtExceptionHandler rootHandler;

    public LoggingExceptionHandler(Context context) {
        this.context = context;
        // we should store the current exception handler -- to invoke it for all not handled exceptions ...
        rootHandler = Thread.getDefaultUncaughtExceptionHandler();
        // we replace the exception handler now with us -- we will properly dispatch the exceptions ...
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(final Thread thread, final Throwable ex) {
        try {
            Log.d(TAG, "called for " + ex.getClass());
            // assume we would write each error in one file ...
            File f = new File(context.getFilesDir(), ERROR_FILE);
            // log this exception ...
            /*Log.e(TAG, "Exception  1111111-> "  + f
            + "  ex-> " + ex.getMessage() +
                    "  -> "+ ex.getCause()
            + " -> " + ex.fillInStackTrace());*/


            /*FileUtils.writeStringToFile(f, ex.getClass().getSimpleName() + " " + System.currentTimeMillis() + "\n",
                    "ISO-8859-1");
            readExceptions(context);*/
        } catch (Exception e) {
            Log.e(TAG, "Exception Logger failed!", e);
        }

        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                // we cant start a dialog here, as the context is maybe just a background activity ...
                //Toast.makeText(context, ex.getMessage() + " Application will close!", Toast.LENGTH_LONG).show();
                String strExceptionMsg = "Message: "+ex.getMessage()+"\n";
                if (ex!=null && ex.getStackTrace()!=null && ex.getStackTrace().length>0){
                    for (int i=0;i<ex.getStackTrace().length;i++){
                        /*Log.e(TAG, "Exception  22222-> "
                                +  i+". Class Name: "+ex.getStackTrace()[i].getClassName()+"\n"
                                +  i+". File Name: "+ex.getStackTrace()[i].getFileName()+"\n"
                                +  i+". Method Name: "+ex.getStackTrace()[i].getMethodName()+"\n"
                                +  i+". Line Number: "+ex.getStackTrace()[i].getLineNumber()+"\n"
                                +  i+". "+ex.getStackTrace()[i].toString()+"\n");*/
                        strExceptionMsg = strExceptionMsg+""+ i+". "+ex.getStackTrace()[i].toString()+"\n";
                    }
                }
                Toast.makeText(context, strExceptionMsg, Toast.LENGTH_LONG).show();
                Log.e(TAG, "Exception Logger strExceptionMsg! -> "+ strExceptionMsg);
                Looper.loop();
            }
        }.start();

        try {
            Thread.sleep(4000); // Let the Toast display before app will get shutdown
        } catch (InterruptedException e) {
            // Ignored.
        }

        rootHandler.uncaughtException(thread, ex);
    }

    public static final List<String> readExceptions(Context context) {
        List<String> exceptions = new ArrayList<>();
        File f = new File(context.getFilesDir(), ERROR_FILE);
        if (f.exists()) {
            /*Log.e(TAG, "readExceptions failed!  ->>"+ f);
            try {
                exceptions = FileUtils.readLines(f, "ISO-8859-1");
                Log.e(TAG, "readExceptions 11111!  ->> "+ new Gson().toJson(exceptions));
            } catch (IOException e) {
                Log.e(TAG, "readExceptions failed!", e);
            }*/
        }
        return exceptions;
    }
}
