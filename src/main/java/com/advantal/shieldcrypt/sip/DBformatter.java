package com.advantal.shieldcrypt.sip;

import android.content.Context;


import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by User on 7/31/2017.
 */

public class DBformatter

{

    public static int INCOMING_TYPE = 1;
    public static int MISSED_TYPE = 3;
    public static int OUTGOING_TYPE = 2;

    public static Callbean getcallLogforOuting(Context context, String callid, String name, int type) {


        Callbean mlog = new Callbean();
        mlog.setCallID(callid);
        mlog.setName(name);
        mlog.setNumber(name);
        mlog.setType(type);
        mlog.setDate(getCurentDateIFormate());
        mlog.setTime(setCallTime(System.currentTimeMillis(),mlog.getDate(), context));

        mlog.setTimestamp(String.valueOf(System.currentTimeMillis()));
        mlog.setDuration("0");
        return mlog;
    }
   public static Callbean getcalltimeupdae(int id, String callStarttime)
    {
        long time =0;
        long callStart = Long.valueOf(callStarttime);
        if(callStart > 0)
        {
            time = (System.currentTimeMillis() - callStart) / 1000;
        }

        Callbean mlog = new Callbean();
        mlog.setID(id);
        mlog.setDuration(String.valueOf(time));
        return mlog;
    }



    public static String getCurentDateIFormate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c.getTime());
        return formattedDate;
    }

    public static String setCallTime(long timestamp,String date, Context context) {

        String datem = "";
        try {
            datem = com.advantal.shieldcrypt.sip.DateFormatter.getRecentMessageTime(context, timestamp, date);

        }catch (Exception e)
        {

        }

        return datem;

    }
}
