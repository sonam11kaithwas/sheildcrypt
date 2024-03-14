package com.advantal.shieldcrypt.sip.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;

public class Utils {

    public static void setStatusBarColor(Activity activity, int color) {
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, color));
    }

    public static String getContactName(String name) {
        String contactName = "";
        try {
            contactName = name.split(",")[0];
        } catch (Exception e) {
            e.printStackTrace();
            contactName = "";
        }
        return contactName;
    }

    public static String getidwithoutip(String ID) {

        if (ID.contains("@")) {
            return ID.split("@")[0];
        } else {
            return ID;
        }
    }

    public static String getNumberFromSipPath(String ID) {

        if (ID.contains(":")) {
            return ID.split(":")[1];
        } else {
            return ID;
        }
    }

    // Get contact name from the device
    public static String getContactName(final String phoneNumber, Context context) {
        try {
            String contactName = "";

            boolean isNameExist = false;
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactName = cursor.getString(0);
                    isNameExist = true;
                }
                cursor.close();
            }
            if (!isNameExist) {
                contactName = phoneNumber;
            }
            return contactName;
        } catch (Exception e) {
            e.printStackTrace();
            return phoneNumber;
        }
    }

    public static void displaySnackbar(Context context, View view, String message, int color) {
        try {
            Snackbar snack = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            View sbview = snack.getView();
            sbview.setBackgroundColor(ContextCompat.getColor(context, color));
//        TextView textView = (TextView) sbview.findViewById(android.support.design.R.id.snackbar_text);
//        textView.setTextColor(context.getResources().getColor(R.color.red));
            snack.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean checkInternetConn(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobile.isConnected() || wifi.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Method to get the basic auth to pass on the api
    public static String getBasicAuthenticationString() {
        String basicAuth = "";
        String baseData = AppConstants.basicAuthUsername + ":" + AppConstants.basicAuthPassword;
        basicAuth = "Basic " + Base64.encodeToString(baseData.getBytes(), Base64.NO_WRAP);
        return basicAuth;
    }

    public static void hideKeyBoard(Activity activity) {
        // Then just use the following:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    // Permission alert dialog when we forcefully enable the permission
    public static void permissionAlertDialog(final Activity activity, String dialogHeading, String message) {
        try {
            hideKeyBoard(activity);
            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.Dialog);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.permission_alert_dialog, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            TextView tv_heading = (TextView) dialogView.findViewById(R.id.tv_heading);
            TextView tv_detail = (TextView) dialogView.findViewById(R.id.tv_detail);
            TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_ok);

            tv_heading.setText(dialogHeading);
            tv_detail.setText(message);

            final AlertDialog show = dialogBuilder.show();
            tv_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                    intent.setData(uri);
                    activity.startActivityForResult(intent, AppConstants.RUNTIME_PERMISSION_REQUEST_CODE);
//                    activity.finish();
                    dialogBuilder.setCancelable(true);
                    show.dismiss();
                }
            });
        } catch (Exception e) {

        }
    }
}
