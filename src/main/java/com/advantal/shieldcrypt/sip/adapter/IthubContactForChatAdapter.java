package com.advantal.shieldcrypt.sip.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.IthubContactForChatActivity;
import com.advantal.shieldcrypt.sip.model.IthubContactsModel;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class IthubContactForChatAdapter extends RecyclerView.Adapter<IthubContactForChatAdapter.UserViewHolder> {

    Context context;
    List<ContactDataModel> al_ithub_contact = new ArrayList<>();
    ArrayList<ContactDataModel> tempArrayList = new ArrayList<ContactDataModel>();
    ContactDataModel contactsBean;
    HashMap<String, Integer> alphaList = new HashMap<>();
    boolean isFirstAlphabet = true;
    IthubContactForChatActivity ithubContactForChatActivity;
    String intentTag = "";
    private CallBack callBack;

    public IthubContactForChatAdapter(IthubContactForChatActivity ithubContactForChatActivity, Context context,
                                      List<ContactDataModel> al_ithub_contact, String intentTag, CallBack callBack) {
        this.al_ithub_contact = al_ithub_contact;
        this.context = context;
        this.tempArrayList.addAll(al_ithub_contact);
        this.ithubContactForChatActivity = ithubContactForChatActivity;
        this.intentTag = intentTag;
        this.callBack = callBack;

        for (int i = 0; i < al_ithub_contact.size(); i++) {
            ContactDataModel contactbeanobject = al_ithub_contact.get(i);

            String name = contactbeanobject.getContactName();//getName();

            String firstChar = (name.charAt(0) + "").trim().toUpperCase();
            if (!alphaList.containsKey(firstChar)) {
                alphaList.put(firstChar, i);
            }
        }

    }

    public IthubContactForChatAdapter(Context context,
                                      List<ContactDataModel> al_ithub_contact, String intentTag, CallBack callBack) {
        this.al_ithub_contact = al_ithub_contact;
        this.context = context;
        this.tempArrayList.addAll(al_ithub_contact);
        this.intentTag = intentTag;
        this.callBack = callBack;

        for (int i = 0; i < al_ithub_contact.size(); i++) {
            ContactDataModel contactbeanobject = al_ithub_contact.get(i);

            String name = contactbeanobject.getContactName();//getName();

            String firstChar = (name.charAt(0) + "").trim().toUpperCase();
            if (!alphaList.containsKey(firstChar)) {
                alphaList.put(firstChar, i);
            }
        }

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_ithub_contact, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {

        contactsBean = al_ithub_contact.get(position);
        String name = contactsBean.getContactName();//.getName();

        String firstChar = (name.charAt(0) + "").trim().toUpperCase();

        final int pos = alphaList.get(firstChar);
        if (position == pos) {
            holder.tv_char.setVisibility(View.VISIBLE);
            holder.tv_char.setText(firstChar + "");
            if (isFirstAlphabet) {
                isFirstAlphabet = false;
            } else {
                holder.view_grey.setVisibility(View.VISIBLE);
            }
        } else {
            holder.tv_char.setVisibility(View.GONE);
            holder.view_grey.setVisibility(View.GONE);
        }

        holder.tv_extension.setVisibility(View.VISIBLE);
        holder.tv_name.setText(contactsBean.getContactName());//getName());
        holder.tv_extension.setText(contactsBean.getMobileNumber());//.getDisplayName());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactDataModel ithubContactsObjectBean = al_ithub_contact.get(position);
                if (intentTag.equalsIgnoreCase("message")) {
                   /* Intent chatIntent = new Intent(context, ChatWindowActivity.class);
                    chatIntent.putExtra(AppConstants.friendJidVariable, ithubContactsObjectBean.getUsername());
                    context.startActivity(chatIntent);
                    ithubContactForChatActivity.finish();*/
                } else if (intentTag.equalsIgnoreCase("call")) {
                    if (callBack!=null){
                        callBack.doClickItemAdd(ithubContactsObjectBean.getMobileNumber());
                    }
                  //  ithubContactForChatActivity.initiateCall(ithubContactsObjectBean.getMobileNumber());//getDisplayName());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return al_ithub_contact.size();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name;
        TextView tv_char;
        TextView tv_extension;
        View view_grey;

        public UserViewHolder(View itemView) {
            super(itemView);

            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_char = (TextView) itemView.findViewById(R.id.tv_char);
            tv_extension = (TextView) itemView.findViewById(R.id.tv_extension);
            view_grey = itemView.findViewById(R.id.view_grey);
        }
    }

    public void generateUpdatedAlphabetList() {
        alphaList = new HashMap<>();
        for (int i = 0; i < al_ithub_contact.size(); i++) {
            ContactDataModel contactbeanobject = al_ithub_contact.get(i);

            String name = contactbeanobject.getContactName();//.getName();

            String firstChar = (name.charAt(0) + "").trim().toUpperCase();
            if (!alphaList.containsKey(firstChar)) {
                alphaList.put(firstChar, i);
            }
        }
    }

    public void getFilter(String str) {

        al_ithub_contact.clear();

        String txt = str.toLowerCase(Locale.getDefault());
        if (str.length() == 0) {
            al_ithub_contact.addAll(tempArrayList);
        } else {
            for (ContactDataModel contact : tempArrayList) {
                if (contact.getContactName()/*getName()*/.toLowerCase().contains(txt)) {
                    al_ithub_contact.add(contact);
                }
            }
        }
        try {
            if (al_ithub_contact.size() > 0) {
                ithubContactForChatActivity.hideNoContacts();
            } else {
                ithubContactForChatActivity.showNoContacts();
            }
            isFirstAlphabet = true;
            generateUpdatedAlphabetList();
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // filter the contact list by number
    public void getFilterByNum(String strNum) {

        al_ithub_contact.clear();

        String txtNum = strNum.toLowerCase(Locale.getDefault());
        if (strNum.length() == 0) {
            al_ithub_contact.addAll(tempArrayList);
        } else {
            for (ContactDataModel contact : tempArrayList) {
//                List<String> contactNums = contact.get();
                boolean isExists = false;

//                for (int i = 0; i < contactNums.size(); i++) {
                if (!isExists) {

                    if (contact.getMobileNumber().toLowerCase().contains(txtNum)) {
                        al_ithub_contact.add(contact);
                    }
                }
//                }
            }
        }
        try {
            if (al_ithub_contact.size() > 0) {
                ithubContactForChatActivity.hideNoContacts();
            } else {
                ithubContactForChatActivity.showNoContacts();
            }
            isFirstAlphabet = true;
            generateUpdatedAlphabetList();
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface CallBack {
        void doClickItemAdd(String number);
    }

}