package com.advantal.shieldcrypt.xmpp_pkg;

/**
 * Created by Sonam on 11-10-2022 12:31.
 */

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.advantal.shieldcrypt.R;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;

public class MainActivityFor extends AppCompatActivity implements ConnectionListener {
    private static final String TAG = MainActivityFor.class.getSimpleName();

    private AbstractXMPPConnection mConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ConnectToXmppServer().execute();
    }


    @Override
    public void authenticated(XMPPConnection arg0, boolean arg1) {
        Log.i(TAG, "Authenticated");
    }

    @Override
    public void connected(XMPPConnection arg0) {
        Log.i(TAG, "Connected");
        try {
            SASLAuthentication.unBlacklistSASLMechanism("PLAIN");
            SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
            try {
                mConnection.login("test", "ilink@2012");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (XMPPException | SmackException | IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void connectionClosed() {
        Log.i(TAG, "Connection closed");
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        Log.i(TAG, "Connection closed on error");
    }

//    @Override
//    public void reconnectingIn(int arg0) {
//        Log.i(TAG, "Reconnecting in");
//    }
//
//    @Override
//    public void reconnectionFailed(Exception arg0) {
//        Log.i(TAG, "Reconnection failed");
//    }
//
//    @Override
//    public void reconnectionSuccessful() {
//        Log.i(TAG, "Reconnection successful");
//    }

    private class ConnectToXmppServer extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "Connecting to xmpp server started...");
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration
                        .builder()
                        .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
//                        .setServiceName("192.168.0.166")
                        .setHost("192.168.0.166")
                        .setPort(5222)
//                        .setCompressionEnabled(false)
                        .build();
                mConnection = new XMPPTCPConnection(config);
//                mConnection.setPacketReplyTimeout(1000);
                mConnection.addConnectionListener(MainActivityFor.this);
                try {
                    mConnection.connect();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (XMPPException | SmackException | IOException e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Log.i(TAG, "Connecting to xmpp server finished...");
        }
    }
}