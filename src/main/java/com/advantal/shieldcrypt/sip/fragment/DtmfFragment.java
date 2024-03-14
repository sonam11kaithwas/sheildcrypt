package com.advantal.shieldcrypt.sip.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.DialogFragment;


import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;

import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipCall;
import net.gotev.sipservice.SipService;
import net.gotev.sipservice.SipServiceCommand;

import java.util.Set;


public class DtmfFragment extends DialogFragment implements View.OnClickListener {

    public static DtmfFragment newInstance() {
        DtmfFragment fragment = new DtmfFragment();
        return fragment;
    }

    EditText et_mobile;
    LinearLayout ll_hide;
    LinearLayout ll_cross;

    ImageView iv_cross;

    LinearLayout ll_1;
    LinearLayout ll_2;
    LinearLayout ll_3;
    LinearLayout ll_4;
    LinearLayout ll_5;
    LinearLayout ll_6;
    LinearLayout ll_7;
    LinearLayout ll_8;
    LinearLayout ll_9;
    LinearLayout ll_star;
    LinearLayout ll_0;
    LinearLayout ll_hash;

    MediaPlayer mp;

    SharedPrefrence mySharedPreferences;

    String mobileRegistered = "";
    String countryCode = "";

    private String s, totalNumber = "";
    private int counter = 0;

    public static SharedPrefrence share;

    private boolean placecall = false;

    private View parentLayout;

    View rootView;

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dtmf_fragment, container, false);

        //View  Object for snackbar
        parentLayout = rootView.findViewById(android.R.id.content);

        share = SharedPrefrence.getInstance(getActivity());
        mySharedPreferences = SharedPrefrence.getInstance(getActivity());

        mp = MediaPlayer.create(getActivity(), R.raw.beep);

        mobileRegistered = mySharedPreferences.getValue(AppConstants.primaryMobile);
        countryCode = mySharedPreferences.getValue(AppConstants.primaryCountryCode);
        mobileRegistered = countryCode + mobileRegistered;

        iv_cross = (ImageView) rootView.findViewById(R.id.iv_cross);

        //View  Object for snackbar
        parentLayout = getActivity().findViewById(android.R.id.content);

        et_mobile = (EditText) rootView.findViewById(R.id.et_mobile);
        ll_hide = (LinearLayout) rootView.findViewById(R.id.ll_hide);
        ll_cross = (LinearLayout) rootView.findViewById(R.id.ll_cross);

        ll_1 = (LinearLayout) rootView.findViewById(R.id.ll_1);
        ll_2 = (LinearLayout) rootView.findViewById(R.id.ll_2);
        ll_3 = (LinearLayout) rootView.findViewById(R.id.ll_3);
        ll_4 = (LinearLayout) rootView.findViewById(R.id.ll_4);
        ll_5 = (LinearLayout) rootView.findViewById(R.id.ll_5);
        ll_6 = (LinearLayout) rootView.findViewById(R.id.ll_6);
        ll_7 = (LinearLayout) rootView.findViewById(R.id.ll_7);
        ll_8 = (LinearLayout) rootView.findViewById(R.id.ll_8);
        ll_9 = (LinearLayout) rootView.findViewById(R.id.ll_9);
        ll_star = (LinearLayout) rootView.findViewById(R.id.ll_star);
        ll_0 = (LinearLayout) rootView.findViewById(R.id.ll_0);
        ll_hash = (LinearLayout) rootView.findViewById(R.id.ll_hash);

        et_mobile.setShowSoftInputOnFocus(false);

        // initialize listner for the keypad buttons
        ll_1.setOnClickListener(this);
        ll_2.setOnClickListener(this);
        ll_3.setOnClickListener(this);
        ll_4.setOnClickListener(this);
        ll_5.setOnClickListener(this);
        ll_6.setOnClickListener(this);
        ll_7.setOnClickListener(this);
        ll_8.setOnClickListener(this);
        ll_9.setOnClickListener(this);
        ll_star.setOnClickListener(this);
        ll_0.setOnClickListener(this);
        ll_hash.setOnClickListener(this);

        ll_hide.setOnClickListener(this);
        iv_cross.setOnClickListener(this);

        // When long click on the cross icon it will clear the dialed number
        iv_cross.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                totalNumber = "";
                et_mobile.setText("");
                counter = 0;
                return true;
            }
        });

        // On long press on the 0 it will enter the + on the dialpad
        ll_0.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                s = "+";
                increment(counter);
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_1:
                mp.start();
                s = "1";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_2:
                mp.start();
                s = "2";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_3:
                mp.start();
                s = "3";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_4:
                mp.start();
                s = "4";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_5:
                mp.start();
                s = "5";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_6:
                mp.start();
                s = "6";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_7:
                mp.start();
                s = "7";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_8:
                mp.start();
                s = "8";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_9:
                mp.start();
                s = "9";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_star:
                mp.start();
                s = "*";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_0:
                mp.start();
                s = "0";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_hash:
                mp.start();
                s = "#";
                sendDtmf(s);
                increment(counter);
                break;
            case R.id.ll_hide:
                dismiss();
                break;
            case R.id.iv_cross:
                int cur = et_mobile.getSelectionEnd();
                decrement(cur);
                break;
        }

    }

    // internet check and check validation
    public void initiateCall() {
        Utils.hideKeyBoard(getActivity());
        if (!Utils.checkInternetConn(getActivity())) {
            Utils.displaySnackbar(getActivity(), parentLayout, getString(R.string.internet_check), R.color.blue);
        } else {
            checkValidation();
        }
    }

    // check validation for the call
    public void checkValidation() {
        if (et_mobile.getText().toString().trim().equalsIgnoreCase("")) {
            Utils.displaySnackbar(getActivity(), parentLayout, getString(R.string.please_enter_mobile), R.color.red);
        } else {
            onCall();
        }
    }

    // make call when user press the call button
    public void onCall() {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
        String number = et_mobile.getText().toString().trim();

//        createJsonToAddCallLogs(mobileRegistered, number);

        if (number.isEmpty()) {
            number = "*9000";
        }
        SipServiceCommand.makeCall(getActivity(), id, number);
    }

    // Increment the entered number count when user press on the dialpad
    public void increment(int c) {
        try {
            if (counter < 20) {
                counter++;

                int cur = 0;

                if (et_mobile.getSelectionEnd() < totalNumber.length()) {
                    cur = et_mobile.getSelectionEnd();
                    totalNumber = insertAt(totalNumber, cur, s);
                    cur++;
                } else {
                    totalNumber += s;
                    cur = totalNumber.length();
                }

                et_mobile.setText(totalNumber);
                et_mobile.setSelection(cur);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // THis method is use when user edit the entered number
    public String insertAt(final String target, final int position, final String insert) {
        final int targetLen = target.length();
        if (position < 0 || position > targetLen) {
            throw new IllegalArgumentException("position=" + position);
        }
        if (insert.isEmpty()) {
            return target;
        }
        if (position == 0) {
            return insert.concat(target);
        } else if (position == targetLen) {
            return target.concat(insert);
        }
        final int insertLen = insert.length();
        final char[] buffer = new char[targetLen + insertLen];
        target.getChars(0, position, buffer, 0);
        insert.getChars(0, insertLen, buffer, position);
        target.getChars(position, targetLen, buffer, position + insertLen);
        return new String(buffer);
    }

    // Decrement the entered number count when user press on the backspace
    public void decrement(int index) {
        try {
            int a = counter;
            if (counter > 0) {
                if (totalNumber != null && totalNumber.length() > 0) {
//                totalNumber = totalNumber.substring(0, totalNumber.length() - 1);
                    totalNumber = new StringBuilder(totalNumber).deleteCharAt(index - 1).toString();
                    et_mobile.setText(totalNumber);

                    try {
                        // et_number.setSelection(totalNumber.length());
                        et_mobile.setSelection(index - 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    counter--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDtmf(String code) {
        String mSIPACCOUNTID = share.getValue(SharedPrefrence.SIPACCOUNTID);

        SipAccount account = SipService.mActiveSipAccounts.get(mSIPACCOUNTID);
        if (account == null)
            return;
        Set<Integer> activeCallIDs = account.getCallIDs();
        if (activeCallIDs == null || activeCallIDs.isEmpty())
            return;
        for (int callID : activeCallIDs) {
            try {
                SipCall sipCall = account.getCall(callID);

                if (sipCall == null) {
                    return;
                } else {
                    SipServiceCommand.sendDTMF(getActivity(), mSIPACCOUNTID, sipCall.getId(), code);
                }

            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }
}
