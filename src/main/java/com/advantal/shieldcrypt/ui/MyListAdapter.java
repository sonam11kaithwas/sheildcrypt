package com.advantal.shieldcrypt.ui; /**
 * Created by Sonam on 14-11-2022 12:50.
 */

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ViewHolder>{
    List<ContactDataModel> modelMutableList =new ArrayList<>();
    ContactSelected contactSelected;
    // RecyclerView recyclerView;
    public MyListAdapter( List<ContactDataModel> modelMutableList,ContactSelected contactSelected) {
        Log.e("MyListAdapter","Constructore()");
        this.modelMutableList = modelMutableList;
        this.contactSelected=contactSelected;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.e("onCreateViewHolder","-----");
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }
    interface ContactSelected {
        void getSelectMultipleCon(ContactDataModel contactDataModel);
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ContactDataModel myListData = modelMutableList.get(position);
        Log.e("onBindViewHolder",""+new Gson().toJson(myListData));
        holder.textView.setText(myListData.getContactName());
//        holder.imageView.setImageResource(listdata[position].getImgId());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            contactSelected.getSelectMultipleCon(myListData);
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.e("getItemCount",""+modelMutableList.size());
        return modelMutableList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public RelativeLayout relativeLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.textView = (TextView) itemView.findViewById(R.id.textView);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
        }
    }
}