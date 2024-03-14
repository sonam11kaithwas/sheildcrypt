package com.advantal.shieldcrypt.network;

//@AndroidEntryPoint
public class AlarmReceiver{

}
//        extends BroadcastReceiver {
//    public SharedPrefrence share;
//    Context context;
//@Inject
//     NetworkHelper networkHelper;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        this.context = context;
//        share = SharedPrefrence.getInstance(context);
//
////		checkxmppconnetion();
//    }
//
//    private void checkxmppconnetion() {
//
//        try {
//            if (networkHelper.isNetworkConnected()) {
//                if (RoosterConnectionService.Companion.isXmppServiceisRunning() == false) {
//                    Intent i1 = new Intent(context, RoosterConnectionService.class);
//                    context.stopService(i1);
//                    Intent intent = new Intent(context, RoosterConnectionService.class);
//                    context.startService(intent);
//                }
//
//                if (RoosterConnectionService.Companion.isXmppServiceisRunning() == false) {
//                    Intent i1 = new Intent(context, RoosterConnectionService.class);
//                    context.startService(i1);
//                } else if (RoosterConnectionService.Companion.getState() == XMPPConnectionListener.ConnectionState.DISCONNECTED) {
//                    try {
//                        XMPPConnectionListener mConnection = XMPPConnectionListener.Companion.getInstance();
//                        mConnection.initObject(context.getApplicationContext());
//                        mConnection.connect();
//                    } catch (Exception e) {
//
//                    }
//
//                }
//
//                String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
//                SipAccount account = SipService.mActiveSipAccounts.get(id);
//                if (account == null) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        Intent intent = new Intent(context, SipService.class);
//                        context.startService(intent);
//                    }
//
//                }
//
//                setAlarm();
//            }
//
//        } catch (Exception e) {
//
//        }
//
//    }
//
//    public void setAlarm() {
//        try {
//
//            Calendar calCurrent = Calendar.getInstance();
//            Intent mIntent = new Intent(context, AlarmReceiver.class);
//            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            final int HELLO_ID = 4568912;
//            AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            PendingIntent snoozependingintent = PendingIntent.getService(context, HELLO_ID, mIntent, PendingIntent.FLAG_ONE_SHOT);
//            mAlarmManager.set(AlarmManager.RTC_WAKEUP, calCurrent.getTimeInMillis() + (10 * 60 * 1000), snoozependingintent);
//        } catch (Exception e) {
//
//        }
//
//    }
//
//
//}