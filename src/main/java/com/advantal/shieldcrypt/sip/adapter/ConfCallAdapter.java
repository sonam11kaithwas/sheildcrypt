package com.advantal.shieldcrypt.sip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.fragment.ConfCallFragment;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;

import net.gotev.sipservice.Logger;

import java.util.ArrayList;
import java.util.List;

public class ConfCallAdapter extends RecyclerView.Adapter<ConfCallAdapter.UserViewHolder> {

    Context context;
    List<ConfCallBean> al_conf_call = new ArrayList<>();
    ArrayList<ConfCallBean> tempArrayList = new ArrayList<ConfCallBean>();
    ConfCallBean confCallBean;
    ConfCallFragment confCallFragment;
    CallBack callBack;

    public ConfCallAdapter(ConfCallFragment confCallFragment, Context context, List<ConfCallBean> al_conf_call) {
        this.al_conf_call = al_conf_call;
        this.context = context;
        this.tempArrayList.addAll(al_conf_call);
        this.confCallFragment = confCallFragment;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conf_call, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {

        confCallBean = al_conf_call.get(position);
        if (al_conf_call!=null && al_conf_call.size()>0){
            Logger.debug("checkActiveData", "  -> " + new Gson().toJson(al_conf_call));
        }
        holder.tv_name.setText(confCallBean.getName());

        holder.iv_end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callBack.doClickItem(al_conf_call.get(position).getCallId());
                confCallFragment.hangUp(al_conf_call.get(position).getCallId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return al_conf_call.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        ImageView iv_end_call;

        public UserViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_end_call = (ImageView) itemView.findViewById(R.id.iv_end_call);
        }
    }

    public void setCallback(CallBack callback) {
        this.callBack = callback;
    }

    public interface CallBack {
        void doClickItem(int callId);
    }

}