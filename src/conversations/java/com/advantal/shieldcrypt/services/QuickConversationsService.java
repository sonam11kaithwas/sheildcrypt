package com.advantal.shieldcrypt.services;

import android.content.Intent;
import android.util.Log;

import com.advantal.shieldcrypt.Config;

public class QuickConversationsService extends AbstractQuickConversationsService {

    QuickConversationsService(XmppConnectionService xmppConnectionService) {
        super(xmppConnectionService);
    }

    @Override
    public void considerSync() {

    }

    @Override
    public void signalAccountStateChange() {

    }

    @Override
    public boolean isSynchronizing() {
        return false;
    }

    @Override
    public void considerSyncBackground(boolean force) {

    }

    @Override
    public void handleSmsReceived(Intent intent) {
        Log.d(Config.LOGTAG,"ignoring received SMS");
    }
}