package com.advantal.shieldcrypt.sip.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.adapter.ConfCallAdapter;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;

import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipCall;

import java.util.ArrayList;
import java.util.HashMap;


public class ConfCallFragment extends DialogFragment implements ConfCallAdapter.CallBack {

    CallBack callBack;

    RecyclerView rv_conf_call;
    ImageView iv_back;

    ArrayList<ConfCallBean> al_conf_call = new ArrayList<>();

    ConfCallAdapter confCallAdapter;

    BroadcastReceiver broadcastReceiver;
    BroadcastReceiver userUpdateBroadcast;


    public static ConfCallFragment newInstance() {
        ConfCallFragment fragment = new ConfCallFragment();
        return fragment;
    }

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
        rootView = inflater.inflate(R.layout.conf_call_fragment, container, false);

        rv_conf_call = rootView.findViewById(R.id.rv_conf_call);

        iv_back = rootView.findViewById(R.id.iv_back);

        Bundle extras = getArguments();
        String s = extras.getString("tag");
        al_conf_call = (ArrayList<ConfCallBean>) extras.getSerializable("arraylist");
        callConfCallAdapter();

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerConfCallReceiver();
        registerUserUpdateReceiver();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(userUpdateBroadcast);
    }

    // Receiver called when contact syncing complete
    public void registerConfCallReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HashMap<Integer, SipCall> activeCalls = new HashMap<>();
                activeCalls = SipAccount.activeCalls;
                ArrayList<ConfCallBean> al_call_id = new ArrayList<>();
                for (int callid : activeCalls.keySet()) {
                    ConfCallBean confCallBean = new ConfCallBean();
                    confCallBean.setCallId(callid);
                    confCallBean.setName("Contact " + callid);
                    al_call_id.add(confCallBean);
                }
                /*for (int callid : activeCalls.keySet()) {
                    CallerInfo contactInfo = null;
                    try {
                        SipCall sipCall = activeCalls.get(callid);
                        contactInfo = new CallerInfo(sipCall.getInfo());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ConfCallBean confCallBean = new ConfCallBean();
                    confCallBean.setCallId(callid);
                    if (contactInfo != null) {
                        confCallBean.setName(contactInfo.getDisplayName());
                    } else {
                        confCallBean.setName("User " + callid);
                    }
                    al_call_id.add(confCallBean);
                }*/

                al_conf_call = al_call_id;
                callConfCallAdapter();
            }
        };

        IntentFilter intent = new IntentFilter("conf_call");
        getActivity().registerReceiver(broadcastReceiver, intent);
    }

    // Receiver called when contact syncing complete
    public void registerUserUpdateReceiver() {
        userUpdateBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                HashMap<Integer, SipCall> activeCalls = new HashMap<>();
                activeCalls = SipAccount.activeCalls;
                ArrayList<ConfCallBean> al_call_id = new ArrayList<>();
                for (int callid : activeCalls.keySet()) {
                    ConfCallBean confCallBean = new ConfCallBean();
                    confCallBean.setCallId(callid);
                    confCallBean.setName("Contact " + callid);
                    al_call_id.add(confCallBean);
                }
                al_conf_call = al_call_id;
                callConfCallAdapter();
            }
        };

        IntentFilter intent = new IntentFilter("conf_user_update");
        getActivity().registerReceiver(userUpdateBroadcast, intent);
    }

    public void callConfCallAdapter() {
        confCallAdapter = new ConfCallAdapter(ConfCallFragment.this, getActivity(), al_conf_call);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_conf_call.setLayoutManager(layoutManager);
        rv_conf_call.setItemAnimator(new DefaultItemAnimator());
        rv_conf_call.setAdapter(confCallAdapter);
    }

    public void hangUp(int callId) {
        callBack.doClickHangUp(callId);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void doClickItem(int callId) {
        callBack.doClickHangUp(callId);
    }

    public interface CallBack {
        void doClickHangUp(int callId);

    }

}
