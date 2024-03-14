package com.advantal.shieldcrypt.sip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.advantal.shieldcrypt.R;

import java.util.ArrayList;

public class DialogMessageListAdapter extends RecyclerView.Adapter<DialogMessageListAdapter.UserViewHolder> {

    Context context;
    CallBack callBack;
    ArrayList<String> al_message = new ArrayList<>();


    public DialogMessageListAdapter(Context context, ArrayList<String> al_message) {
        this.al_message = al_message;
        this.context = context;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_message_list_row, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        try {
            holder.tv_message.setText(al_message.get(position));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callBack.doClickItem(al_message.get(position));
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return al_message.size();
    }

    public void setCallback(CallBack callback) {
        this.callBack = callback;
    }

    public interface CallBack {
        void doClickItem(String message);
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_message;

        public UserViewHolder(View itemView) {
            super(itemView);

            tv_message = (TextView) itemView.findViewById(R.id.tv_message);
        }
    }
}
