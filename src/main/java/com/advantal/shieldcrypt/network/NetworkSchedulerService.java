package com.advantal.shieldcrypt.network;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.xmpp_pkg.XMPPConnectionListener;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.advantal.shieldcrypt.xmpp_pkg.RoosterConnectionService;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.pjsip_status_code;

/**
 * Service to handle callbacks from the JobScheduler. Requests scheduled with the JobScheduler
 * ultimately land on this service's "onStartJob" method.
 *
 * @author jiteshmohite
 */
public class NetworkSchedulerService extends Worker implements
        ConnectivityReceiver.ConnectivityReceiverListener {

//    public ChatDao chatDao;

    private static final String TAG = NetworkSchedulerService.class.getSimpleName();
    public static SharedPrefrence share;
    private final ConnectivityReceiver mConnectivityReceiver;
    boolean isAutologin = false;
    Context context;
    boolean isOnStartCalled = false;
    private SipAccountData mSipAccount;
    private boolean placecall = false;
    // broadcast receive when get sip registration response
    private final BroadcastEventReceiver sipEvents = new BroadcastEventReceiver() {

        @Override
        public void onRegistration(String accountID, int registrationStateCode) {

            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
//                Toast.makeText(DrawerActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                System.out.println("PUsh === registered");
                if (placecall) {
                    placecall = false;
                }

            } else {
                System.out.println("PUsh === unregistered");
//                Toast.makeText(DrawerActivity.this, "Unregistered", Toast.LENGTH_SHORT).show();
                if (placecall) {
                    placecall = false;
                }
//                Registersip(share.getValue(com.ithub.sip.SharedPrefrence.SIPACCOUNTID), "");
            }
        }
/*
        @Override
        public void onCallState(String accountID, int callID, int callStateCode, int callStatusCode,
                                long connectTimestamp, boolean isLocalHold, boolean isLocalMute, boolean isVieo) {
            if (callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CALLING ||
                    callStateCode == pjsip_inv_state.PJSIP_INV_STATE_INCOMING ||
                    callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING ||
                    callStateCode == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            }
        }*/
    };
    private SipService sipService;


    public NetworkSchedulerService(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;

        sipEvents.register(context);
        if (share == null)
            share = SharedPrefrence.getInstance(context);

        Log.i(TAG, "Service created");
        isAutologin = MySharedPreferences.getSharedprefInstance().getBooleanValue();
        mConnectivityReceiver = new ConnectivityReceiver(this);
    }

    /**
     * When the app's MainActivity is created, it starts this service. This is so that the
     * activity and this service can communicate back and forth. See "setUiCallback()"
     */

    @NonNull
    @Override
    public Result doWork() {
        System.out.println("NetworkSchedulerService connected dowork");
        context.registerReceiver(mConnectivityReceiver, new IntentFilter(AppConstants.CONNECTIVITY_ACTION));
        return Result.success();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if (isAutologin && isConnected) {
            System.out.println("NetworkSchedulerService connected true");

//            AppUtills.Companion.isXmppWorkScheduled(context);

//            onRegister();
        } else if (!isConnected && isAutologin) {
            System.out.println("NetworkSchedulerService connected false");
            RoosterConnectionService.Companion.setSConnectionState(XMPPConnectionListener.ConnectionState.DISCONNECTED);

//            chatDao.cancelAllDownloads(AppConstants.filedownloading, AppConstants.filedownloadfailed);
//            chatDao.cancelAllUploads(AppConstants.uploading, AppConstants.failed);
        }
    }

    public void stopXmppService() {
        try {
            if (RoosterConnectionService.mConnection != null) {
                RoosterConnectionService.mConnection.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void disconnectCall() {
//        try {
//            String accountID = mySharedPreferences.getValue(com.ithub.sip.SharedPrefrence.SIPACCOUNTID);
//            SipAccount account = SipService.mActiveSipAccounts.get(accountID);
//            if (account == null)
//                return;
//
//            Set<Integer> activeCallIDs = account.getCallIDs();
//
//            if (activeCallIDs.size() > 0) {
////                account.removeCalls();
//                Intent intent = new Intent("com.disconnectcall");
//                getApplicationContext().sendBroadcast(intent);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void Registersip(String mUsername1, String countryCodemobile) {

        SipAccountData mSipAccount = new SipAccountData();

        mSipAccount.
                setHost(MySharedPreferences.getSharedprefInstance().getLoginData().getXmpport())
//        setHost("5222")
//                .setPort(Integer.parseInt(mySharedPreferences.getValue(AppConstants.sipPortDynamic)))
                .setPort(Long.parseLong((MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())))
                .setTcpTransport(false)
                .setUsername(MySharedPreferences.getSharedprefInstance().getLoginData().getUsername())
                .setPassword(MySharedPreferences.getSharedprefInstance().getLoginData().getPassword())
                .setRealm(MySharedPreferences.getSharedprefInstance().getLoginData().getXmpport());
//                .setRealm("5222")
        ;
        SipServiceCommand.setAccount(context, mSipAccount);
    }

    // Request for the SIP registration
    public void onRegister() {
        System.out.println("NetworkSchedulerService connected on register called");
        mSipAccount = new SipAccountData();

        mSipAccount.
                //setHost(mySharedPreferences.getValue(AppConstants.sipIpDynamic))
                        setHost("5222")
//                .setPort(Integer.parseInt(mySharedPreferences.getValue(AppConstants.sipPortDynamic)))
                .setPort(Long.parseLong((MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())))
                .setTcpTransport(false)
                .setUsername(MySharedPreferences.getSharedprefInstance().getLoginData().getUsername())
                .setPassword(MySharedPreferences.getSharedprefInstance().getLoginData().getPassword())
                .setRealm(MySharedPreferences.getSharedprefInstance().getLoginData().getXmpport());
//                .setRealm("5222");

        share.setValue(SharedPrefrence.SIPSERVER, MySharedPreferences.getSharedprefInstance().getLoginData().getXmpport());
//        share.setValue(SharedPrefrence.SIPSERVER, "5222");
//        share.setintValue(SharedPrefrence.SIPPORT, Integer.parseInt(mySharedPreferences.getValue(AppConstants.sipPortDynamic)));
        share.setintValue(SharedPrefrence.SIPPORT,7);
        share.setValue(SharedPrefrence.SIPUSERNAME, MySharedPreferences.getSharedprefInstance().getLoginData().getUsername());
        share.setValue(SharedPrefrence.SIPPASS, MySharedPreferences.getSharedprefInstance().getLoginData().getPassword());
        share.setValue(SharedPrefrence.SIPREALM,MySharedPreferences.getSharedprefInstance().getLoginData().getXmpport());
//        share.setValue(SharedPrefrence.SIPREALM, "5222");

//        SipServiceCommand.setAccount(this, mSipAccount);
        SipServiceCommand.setReRegisterAccount(context, mSipAccount);
        SipServiceCommand.getCodecPriorities(context);

    }


}
