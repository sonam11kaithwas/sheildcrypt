package com.advantal.shieldcrypt.sip.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.adapter.DialogMessageListAdapter;

import java.util.ArrayList;

public class MessageListDialogFragment extends DialogFragment implements DialogMessageListAdapter.CallBack {

    CallBack callBack;
    RecyclerView rv_message;

    LinearLayout ll_cross;

    DialogMessageListAdapter dialogMessageListAdapter;

    View rootView;

    ArrayList<String> al_message = new ArrayList<>();


    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_message_picker, container, false);

        rv_message = (RecyclerView) rootView.findViewById(R.id.rv_message);

        ll_cross = rootView.findViewById(R.id.ll_cross);
        al_message.add("Sorry, I can't answer the phone now.");
        al_message.add("Can I call you back later?");
        al_message.add("Can't talk now. Can you call me later?");
        al_message.add("I'll be there in a moment.");
        al_message.add("Customise...");

        showMessageList();
        ll_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return rootView;
    }

    // show the list of contact
    public void showMessageList() {
        dialogMessageListAdapter = new DialogMessageListAdapter(getActivity(), al_message);
        dialogMessageListAdapter.setCallback(MessageListDialogFragment.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv_message.setLayoutManager(layoutManager);
        rv_message.setItemAnimator(new DefaultItemAnimator());
        rv_message.setAdapter(dialogMessageListAdapter);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack=callBack;
    }

    @Override
    public void doClickItem(String message) {
        callBack.doClickMessage(message);
        dismiss();
    }

    public interface CallBack{
        void doClickMessage(String message);

    }
}
