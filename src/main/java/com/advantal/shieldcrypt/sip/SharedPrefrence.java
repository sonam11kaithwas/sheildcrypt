package com.advantal.shieldcrypt.sip;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class SharedPrefrence {

    public static final String BROADCASTNAME = "broadcast_name";
    public static String PRIVATE_MESSAGE_LIST = "private_message_lst";
    public static final String callNumber = "callNumber";
    public static final String callStatus = "callStaus";
    public static final String callType = "callType";
    public static final String callId = "callId";
    public static final String PASSCODE = "passcode";
    public static final String IS_SPLASH = "is_splash";
    public static final String Attemp = "attemp";
    public static final String SORT = "srotby";
    public static final String SIPSERVER = "sipServer";
    public static final String SIPPORT = "port";
    public static final String SIPUSERNAME = "userName";
    public static final String SIPPASS = "pass";
    public static final String SIPREALM = "realm";

    public static final String SIPUSERNAMEPARKONE = "SIPUSERNAMEPARKONE";
    public static final String SIPPASSWORDPARKONE = "SIPPASSWORDPARKONE";

    public static final String PROTECTED_USERS = "protected_users";
    public static final String CHAT_TEXT_USERS = "text_users";

    /* RESTORE TO DEFAULT SETTING chat*/
    public static String IS_USER_BLOCKED = "IS_USER_BLOCKED";

    /* RESTORE TO DEFAULT SETTING chat*/
    public static String evapEnabled = "mEvapEnableOrNot";
    public static String mMintute = "mMintute";
    public static String mSec = "mSec";

    public static final String PUSH_TOKEN = "push_token";

    public static final String PINSETONSERVER = "setpinonserver";

    public static final String BASEURL = "base_url";
    public static final String VOIP_IP = "voip_ip";
    public static final String XMPP_IP = "xmpp_ip";



    /* RESTORE TO DEFAULT SETTING chat*/

    // Privacy
    public static String SHOW_ONLINE_STATUS = "SHOW_ONLINE_STATUS";
    public static String SHOW_SEEN_STATUS = "SHOW_SEEN_STATUS";
    public static String COLLECT_ANALYTICS_DATA = "COLLECT_ANALYTICS_DATA";
    public static String SHOW_YOUR_PROFILE_PICTURE = "SHOW_YOUR_PROFILE_PICTURE";
    public static String DEACTIVATE_ACCOUNT = "DEACTIVATE_ACCOUNT";

    // Notifications
    public static String SHOW_CHAT_NOTIFICATION = "SHOW_CHAT_NOTIFICATION";
    public static String SHOW_CALL_NOTIFICATION= "SHOW_CALL_NOTIFICATION";
    public static String SHOW_NEW_CONTACTS_JOINS = "SHOW_NEW_CONTACTS_JOINS";
    public static String IN_APP_SOUND = "IN_APP_SOUND";
    public static String IN_APP_VIBRATE = "IN_APP_VIBRATE";
    public static String SHOW_SCREEN_LOCK = "SHOW_SCREEN_LOCK";
    public static String SHOW_CHAT_LOCK = "SHOW_CHAT_LOCK";
    public static String SET_CHAT_LOCK= "SET_CHAT_LOCK";
    public static String CHAT_LOCK_PIN= "CHAT_LOCK_PIN";


    // Calls & Messages
    public static String CALL_WAITING = "CALL_WAITING";
    public static String FILTER_UNKNOWN_SENDER = "FILTER_UNKNOWN_SENDER";

    // Multimedia
    public static String SAVE_TO_GALLERY = "SAVE_TO_GALLERY";
    public static String AUTOMATIC_DOWNLOAD = "AUTOMATIC_DOWNLOAD";
    public static String RESTRICT_DATA_USAGE = "RESTRICT_DATA_USAGE";

    public static String MUTE_UNMUTE = "MUTE_UNMUTE";

    public static String TRUE = "true";
    public static String FALSE = "false";

    /* RESTORE TO DEFAULT SETTING chat*/

    public static SharedPreferences myPrefs;
    public static SharedPreferences.Editor prefsEditor;
    public static SharedPrefrence myObj;
    public static String outLetDetailList = "outLetDetailList";
    public static String lat = "lat";
    public static String lng = "lng";
    public static String GET_USER_DETAILS = "getUserDetails";
    public static String OTP = "getOtp";
    public static String MOBILE = "getMobile";
    public static String ORG_ID = "org_id";
    public static String ORG_NAME = "org_name";
    public static String LICENCY_KEY = "licence_key";
    public static String ISfetchprofile = "fectchprofile";


    public static String DEVICETOKEN = "getDeviceToken";
    public static String CONTACT_LIST = "contactList";
    public static String SYNC_ENABLE = "syncEnable";
    public static String TRACKLOCATION = "trackLocation";

    public static String OFFLINE_MSG_SENDING = "offline_message_Sending";
    public static String OFFLINE_GROUP_NOTIFY = "offline_group_notify";

    /* ContactSync  Rajesh */
    public static String FIRST_TIME = "contact_string";
    public static String CONTACT_STRING = "contact_string";
    public static String LASTSYNCING = "last_syncing";
    public static String SYNCING_REQUEST = "syncing_request";
    public static String GROUP_SYNCING_REQUEST = "groupsyncing_request";
    public static String FETCH_USER_REQUEST = "fetchuser_request";
    public static String MAPPING_OBJ = "mapping_object";

    //public static String GROUP_LST = "groupLst";

    public static String FROM_VERIFICATION = "fromverification";
    public static String NAME = "name";
    public static String PASSPHARSE = "passpharse";
    public static final String USERID = "user_id";
    public static final String JID = "xmpp_jid";
    public static final String PASSWORD = "xmpp_password";
    public static final String ISLOGIN = "xmpp_logged_in";
    public static final String ISVOIP = "voip_logged_in";

    public static final String OUTGOING = "outgoing";
    public static final String INCOMING = "incoming";

    public static final String ISPUSHPROCESS = "ispuchprocess";
    public static final String pushcount = "pushcount";


    public static final String GROUPID = "group_id";
    public static final String ADMINID = "admin_id";
    public static final String GROUPNAME = "group_name";
    public static final String GROUPS_RESPONSE = "group_response";

    public static final String COUNTRY_NAME = "countryName";
    public static final String COUNTRY_CODE = "countryCode";

    public static final String PROFILE ="profile";

    public static final String CHAT_BG ="chat_bg";

    public static final String GALLERY_CHAT_BG_BASE64 = "gallery_chat_bg_base64";

    public static final String VCARD_LIST = "vCardList";

    public static final String OWNPRIVATEKEY = "ownprivatekey";
    public static final String OWNPUBLICKEY = "ownpublickey";


    public static final String UPDATE_CALL = "updatecall";
    public static final String UPDATE_CONTACT = "updatecontact";


    public static final String SIPACCOUNTID = "sipaccountid";
    public static final String SIPACCOUNTIDPARKONE = "sipaccountidparkone";
    public static final String TIMEWHENSTOP = "elspsetime";
    public static String securityCount="count";

    /**
     * Create private constructor
     */
    private SharedPrefrence() {

    }

    public static SharedPrefrence getInstance(Context ctx) {
        if (myObj == null) {
            myObj = new SharedPrefrence();
            myPrefs = ctx.getSharedPreferences("com.ithub", Context.MODE_PRIVATE);
            prefsEditor = myPrefs.edit();
        }
        return myObj;
    }

    public String getValue(String Tag) {
        if (Tag.equals("lat"))
            return myPrefs.getString(Tag, "22.7684");
        if (Tag.equals("lng"))
            return myPrefs.getString(Tag, "75.8957");
        if (Tag.equals("SESSION_KEY"))
            return myPrefs.getString(Tag, "1");
        if (Tag.equals("PROMPTOUTLET"))
            return myPrefs.getString(Tag, "false");
        if (Tag.equals("REMINDER"))
            return myPrefs.getString(Tag, "true");
        if (Tag.equals("dealercode"))
            return myPrefs.getString(Tag, "false");
        return myPrefs.getString(Tag, "");
    }

    public void setValue(String Tag, String value)
    {
        prefsEditor.putString(Tag, value);
        prefsEditor.commit();
    }

    public int getIntValue(String Tag) {
        return myPrefs.getInt(Tag, 0);
    }

    public void setIntValue(String Tag, int value) {
        prefsEditor.putInt(Tag, value);
        prefsEditor.commit();
    }

    /* Store Private Message to TEMP */

    public void setPrivateList(String Tag, ConcurrentHashMap<Long, String> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public ConcurrentHashMap<Long, String> getPrivateList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new ConcurrentHashMap<Long, String>();
        } else {
            Type type = new TypeToken<ConcurrentHashMap<Long, String>>() {
            }.getType();
            Gson gson = new Gson();
            ConcurrentHashMap<Long, String> List = gson.fromJson(obj, type);
            return List;
        }
    }



        /* Store Blocked Users */

    public void setBlockList(String Tag, ArrayList<String> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public ArrayList<String> getClockList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new ArrayList<String>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> List = gson.fromJson(obj, type);
            return List;
        }
    }


    public void setList(String Tag, ArrayList<String> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public ArrayList<String> getList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new ArrayList<String>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> List = gson.fromJson(obj, type);
            return List;
        }
    }


        /* Store Blocked Users */

    public void setMuteList(String Tag, ArrayList<String> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public ArrayList<String> getMuteList(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new ArrayList<String>();
        } else {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            Gson gson = new Gson();
            ArrayList<String> List = gson.fromJson(obj, type);
            return List;
        }
    }

    public int getintValue(String Tag)
    {
        return myPrefs.getInt(Tag, 0);
    }

    public void setintValue(String Tag, int value) {
        prefsEditor.putInt(Tag, value);
        prefsEditor.commit();
    }

    public void setintValue(String Tag, int value,Context ctx) {

        myObj = new SharedPrefrence();
        myPrefs = ctx.getSharedPreferences("com.ithub", Context.MODE_WORLD_READABLE);
        prefsEditor = myPrefs.edit();
        prefsEditor.putInt(Tag, value);
        prefsEditor.commit();
    }

    public static void clearSharePreference(Context ctx) {
        myObj = new SharedPrefrence();
        myPrefs = ctx.getSharedPreferences("com.ithub", Context.MODE_PRIVATE);
        prefsEditor = myPrefs.edit();
        prefsEditor.clear();
        prefsEditor.commit();
    }

    public HashMap<String, String> getHash(String Tag) {
        String obj = myPrefs.getString(Tag, "defValue");
        if (obj.equals("defValue")) {
            return new HashMap<String, String>();
        } else {
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Gson gson = new Gson();
            HashMap<String, String> List = gson.fromJson(obj, type);
            return List;
        }
    }

    public void setHash(String Tag, HashMap<String, String> lst) {
        Gson gson = new Gson();
        String json = gson.toJson(lst);
        prefsEditor.putString(Tag, json);
        prefsEditor.commit();
    }

    public long getlongValue(String Tag)
    {
        return myPrefs.getLong(Tag,0);
    }

    public void setlongValue(String Tag, long value) {
        prefsEditor.putLong(Tag, value);
        prefsEditor.commit();
    }
}