package com.advantal.shieldcrypt.ui.grp_create_contact;

import android.app.PendingIntent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.ui.CreatePrivateGroupChatDialog;
import com.advantal.shieldcrypt.ui.UiCallback;
import com.advantal.shieldcrypt.ui.XmppActivity;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class CreateNewGroupActivity extends XmppActivity implements ContactListAdpter.ContactSelected,
        View.OnClickListener,
        ContactAlphBetsAdpter.SelectAlphbets,
        CreatePrivateGroupChatDialog.CreateConferenceDialogListener {

    String Group ;

    private final UiCallback<Conversation> mAdhocConferenceCallback = new UiCallback<Conversation>() {
        @Override
        public void success(final Conversation conversation) {
            runOnUiThread(() -> {
                hideToast();
                switchToConversation(conversation);
            });
        }

        @Override
        public void error(final int errorCode, Conversation object) {
            runOnUiThread(() -> replaceToast(getString(errorCode)));
        }

        @Override
        public void userInputRequired(PendingIntent pi, Conversation object) {

        }
    };

    ArrayList<String> contactAlphList = new ArrayList<String>();
    List<Jid> jabberIds = new ArrayList<>();
    private ContactListAdpter adapter = null;
    private ContactAlphBetsAdpter myListAdapter = null;
    RecyclerView whatsap_contact_recycler;

    @Override
    protected void refreshUiReal() {

    }

    @Override
    public void onBackendConnected() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_group);
        whatsap_contact_recycler = findViewById(R.id.whatsap_contact_recycler);


        showContacts();
    }

    private void showContacts() {

        List<ContactDataModel> modelMutableList = new ArrayList<>();
        if (getIntent() != null && getIntent().hasExtra("contacts")) {
            modelMutableList = new Gson().fromJson(getIntent().getStringExtra("contacts"), new TypeToken<List<ContactDataModel>>() {
            }.getType());
        }
        adapter = new ContactListAdpter(this, modelMutableList, this);
        // Attach the adapter to the recyclerview to populate items
        whatsap_contact_recycler.setAdapter(adapter);
        // Set layout manager to position the items
        whatsap_contact_recycler.setLayoutManager(new LinearLayoutManager(this));
        // That's all!


//        myListAdapter = new ContactAlphBetsAdpter(this, contactAlphList, this);
//        binding.recycleAlphabets.setLayoutManager(new LinearLayoutManager(this));
//
//        binding.recycleAlphabets.setAdapter(myListAdapter);

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateDialogPositiveClick(Spinner spinner, String subject,String grpdesc) {

    }

    @Override
    public void getSelectedAlphabets(@NonNull String alpha) {

    }

    @Override
    public void getSelectMultipleCon(@NonNull ContactDataModel contactModel) {

    }
}