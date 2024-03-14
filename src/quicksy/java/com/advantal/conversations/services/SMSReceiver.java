package com.advantal.shieldcrypt.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.common.base.Strings;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.utils.Compatibility;

public class SMSReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        intent.setClass(context, XmppConnectionService.class);
        Compatibility.startService(context, intent);
    }
}
