package com.advantal.shieldcrypt.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends
        RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    List<ContactDataModel> mContacts = new ArrayList<>();
    SelectedContact selectedContact;

    public ContactsAdapter(List<ContactDataModel> modelMutableList, SelectedContact selectedContact) {
        this.mContacts = modelMutableList;
        this.selectedContact = selectedContact;
    }
    // ... constructor and member variables

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_contact, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, final int position) {
        // Get the data model based on position
        ContactDataModel contact = mContacts.get(position);

        // Set item views based on your views and data model
        TextView textView = holder.nameTextView;
        textView.setText(contact.getContactName());
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedContact.setSelectedContact(mContacts.get(position));
            }
        });


    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mContacts.size();
    }

    interface SelectedContact {
        void setSelectedContact(ContactDataModel contact);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;
        public LinearLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
//            this.imageView = (ImageView) itemView.findViewById(R.id.imageView);
            this.nameTextView = (TextView) itemView.findViewById(R.id.contact_name);
            relativeLayout = (LinearLayout) itemView.findViewById(R.id.lay);
        }
    }
}