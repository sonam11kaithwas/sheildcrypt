package com.advantal.shieldcrypt.conference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.utils_pkg.RoundedImageView2;
import com.google.gson.Gson;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;

import net.gotev.sipservice.Logger;

import java.util.ArrayList;
import java.util.List;

public class VideoAddedConUserAdapter extends RecyclerView.Adapter<VideoAddedConUserAdapter.UserViewHolder> {
    private Context context;
    private List<ConfCallBean> al_conf_call = new ArrayList<>();
    private CallBack callBack;

    public VideoAddedConUserAdapter(Context context,
                                    List<ConfCallBean> al_conf_call, CallBack callBack) {
        this.al_conf_call = al_conf_call;
        this.context = context;
        this.callBack = callBack;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_conf_call, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {

        if (al_conf_call!=null && al_conf_call.size()>0){
            Logger.debug("checkActiveData", "  -> " + new Gson().toJson(al_conf_call));
        }
        holder.tv_name.setText(al_conf_call.get(position).getName());

        holder.iv_end_call.setVisibility(View.GONE);
        /*holder.iv_end_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                callBack.doClickItem(al_conf_call.get(position).getCallId());
                confCallFragment.hangUp(al_conf_call.get(position).getCallId());
            }
        });*/

        /*if (position==0){
            holder.userImg.setBackgroundResource(R.drawable.ic_add_participant_video);
        } else {
            holder.userImg.setBackgroundResource(R.drawable.ic_contact_profile);
            holder.userImg.setBackgroundResource(R.drawable.ic_baseline_person_24);
        }*/

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if (position==0){
                    if (callBack!=null){
                        callBack.doClickItem(position);
                    }
                }*/
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
        private RoundedImageView2 userImg;

        public UserViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            iv_end_call = (ImageView) itemView.findViewById(R.id.iv_end_call);
            userImg = itemView.findViewById(R.id.userImg);
        }
    }

    public interface CallBack {
        void doClickItem(int position);
    }
}
