package com.advantal.shieldcrypt.sip.videoconf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;

import net.gotev.sipservice.Logger;
import net.gotev.sipservice.SipServiceCommand;

import java.util.ArrayList;

public class AddedUserVideoConf extends RecyclerView.Adapter<AddedUserVideoConf.RecyclerViewHolder> {

    private ArrayList<ConfCallBean> courseDataArrayList;
    private Context mcontext;
    private int height;
    private String accountId = "";

    public AddedUserVideoConf(ArrayList<ConfCallBean> recyclerDataArrayList, Context mcontext, int height, String accountId) {
        this.courseDataArrayList = recyclerDataArrayList;
        this.mcontext = mcontext;
        this.height = height;
        this.accountId = accountId;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate Layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_video_conf_grid_item, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        // Set the data to textview and imageview.

        Log.e("heightwidth",  " heightNew-> " + height  + "  -> " +height/2);
        if (courseDataArrayList!=null && courseDataArrayList.size()>0){
            Logger.debug("checkActiveData", "  1111-> " + new Gson().toJson(courseDataArrayList)
            + "  accountId-> " + accountId);
        }

        ViewGroup.LayoutParams params = holder.llMainView.getLayoutParams();
        if (courseDataArrayList.size()<=4){
            params.height = ((height-150)/2);
        } else if (courseDataArrayList.size()>=4){
            params.height = ((height-150)/3);
        }
        holder.llMainView.setLayoutParams(params);


        if (accountId!=null && !accountId.isEmpty()){
            if (courseDataArrayList.size()>1){
                if (position==1){
                    SipServiceCommand.startVideoPreview(mcontext, accountId, courseDataArrayList.get(position).getCallId(),
                            holder.surfaceVideo.getHolder().getSurface());
                } else {
                    SipServiceCommand.setupIncomingVideoFeed(mcontext, accountId, courseDataArrayList.get(position).getCallId(),
                            holder.surfaceVideo.getHolder().getSurface());
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        // this method returns the size of recyclerview
        return courseDataArrayList.size();
    }

    // View Holder Class to handle Recycler View.
    public class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView courseTV;
        private ImageView courseIV;
        private LinearLayout llMainView;
        private SurfaceView surfaceVideo;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
           /* courseTV = itemView.findViewById(R.id.idTVCourse);
            courseIV = itemView.findViewById(R.id.idIVcourseIV);*/
            llMainView = itemView.findViewById(R.id.llMainView);
            surfaceVideo = itemView.findViewById(R.id.surfaceVideo);
        }
    }
}