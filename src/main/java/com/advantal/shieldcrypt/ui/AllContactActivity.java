package com.advantal.shieldcrypt.ui;

import static com.advantal.shieldcrypt.ui.util.SoftKeyboardUtils.hideSoftKeyboard;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.databinding.ActivityAllContactBinding;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.entities.Contact;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.service.SyncMyContacts2;
import com.advantal.shieldcrypt.services.XmppConnectionService;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.WhatsAppContactAdpter;
import com.advantal.shieldcrypt.ui.grp_create_contact.ContactListActivity2;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationRead;
import com.advantal.shieldcrypt.utils.XmppUri;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import database.my_database_pkg.db_table.MyAppDataBase;

@AndroidEntryPoint
public class AllContactActivity extends XmppActivity implements XmppConnectionService.OnConversationUpdate, OnConversationRead, XmppConnectionService.OnRosterUpdate, CreatePrivateGroupChatDialog.CreateConferenceDialogListener, View.OnClickListener {

    private final int REQUEST_CREATE_CONFERENCE = 0x39da;
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
    List<ContactDataModel> modelMutableList = new ArrayList<>();
    private WhatsAppContactAdpter adapter;
    private SyncMyContacts2 syncMyContacts2;
    private ActivityAllContactBinding binding;
    private ConversationFragment conversationFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contact);


        binding = ActivityAllContactBinding.inflate(getLayoutInflater());

        setSupportActionBar(binding.searchLayout.toolbar);
        getSupportActionBar().setTitle(getString(R.string.selected_contact));

        syncMyContacts2 = new ViewModelProvider(AllContactActivity.this).get(SyncMyContacts2.class);


        // Lookup the recyclerview in activity layout
        RecyclerView rvContacts = findViewById(R.id.rvContacts);

        modelMutableList = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getAllNotes(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber);

        modelMutableList.add(0, (new ContactDataModel(0, "A", "A", "", "", "", false)));
//        modelMutableList.add(1, (new ContactDataModel(1, "A", "A", "", "", "", false)));

        // Create adapter passing in the sample user data
        adapter = new WhatsAppContactAdpter(this, modelMutableList, new WhatsAppContactAdpter.ContactSelected() {
            @Override
            public void getSelectMultipleCon(@NonNull ContactDataModel contactDataModel) {
                selectedCOntactCreateConnection(contactDataModel);
            }

            @Override
            public void getQrCodeScanner() {

            }

            @Override
            public void getNewGrpraete() {
//                showCreatePrivateGroupChatDialog();
                getSelectedContactFromList();
            }
        });

        // Attach the adapter to the recyclerview to populate items
        rvContacts.setAdapter(adapter);
        // Set layout manager to position the items
        rvContacts.setLayoutManager(new LinearLayoutManager(this));
        // That's all!


        syncMyContacts2.getRefreshListCallBack().observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                modelMutableList.clear();
                modelMutableList = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getAllNotes(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber);

                modelMutableList.add(0, (new ContactDataModel(0, "A", "A", "", "", "", false)));

                adapter.addList(modelMutableList);
                adapter.notifyDataSetChanged();
            }
        });


        syncMyContacts2.getRefreshListCallBackError().observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.progressBar.setVisibility(View.GONE);
                MyApp.Companion.getAppInstance().showToastMsg(s);
            }
        });


        binding.searchLayout.searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().isEmpty()) {
                    filterCallList(s.toString());
                } else {
                    adapter.listReload();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void filterCallList(String query) {
        adapter.getFilter().filter(query);
    }

    private void getSelectedContactFromList() {
        Intent intent = new Intent(this, ContactListActivity2.class);
//        intent.putExtra("contacts", new Gson().toJson(contactOnOpenFire));
        intent.putExtra("contacts", new Gson().toJson(MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getAllNotes(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber)));
        startActivity(intent);
       this.finish();
    }

    private void selectedCOntactCreateConnection(ContactDataModel contactDataModel) {
        boolean openWindow = false;
        Jid jid = Jid.ofEscaped(contactDataModel.getMobileNumber() + "@" + MySharedPreferences.getSharedprefInstance().getChatip());
        Account account = getSelectedAccount();
        Contact contact = null;
        Conversation conversation = null;

        if (account != null && account.getRoster() != null && account.getRoster().getContacts() != null) {
            for (int i = 0; i < account.getRoster().getContacts().size(); i++) {
                if (account.getRoster().getContacts().get(i).getJid().equals(jid)) {
                    contact = account.getRoster().getContacts().get(i);
                    conversation = xmppConnectionService.findOrCreateConversation(contact.getAccount(), contact.getJid(), false, true);
                    openWindow = true;
                    break;
                } else {
                    openWindow = false;
                }
            }
        } else {
            openWindow = false;
        }

        if (openWindow) {
            openFragment(conversation);
        } else {
            final StartConversationActivity.Invite invite = null;
            craeteContact(null, contactDataModel, false);
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void openFragment(Conversation conversation) {
        conversationFragment = new ConversationFragment();
        conversationFragment.reInit(conversation, new Bundle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, conversationFragment, null).setReorderingAllowed(true).addToBackStack("name") // name can be null
                .commit();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (conversationFragment != null) conversationFragment.dispatchTouchEvent(event);
        return super.dispatchTouchEvent(event);
    }

    void craeteContact(StartConversationActivity.Invite invite, ContactDataModel con, boolean openWindow) {
        String ip = MySharedPreferences.getSharedprefInstance().getChatip();
        if (ip == null || ip.equals("")) {
            ip = MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip();
        }
        final Account account = xmppConnectionService.findAccountByJid(Jid.of((MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + ip)));//
        Contact contact = null;
        if (account != null && account.getRoster() != null)
            contact = account.getRoster().getContact(Jid.of((con.getMobileNumber() + "@" + ip)));
        else return;

        if (invite != null && invite.getName() != null) {
            contact.setServerName(invite.getName());
        }
        if (contact.isSelf()) {
            Log.e("self ", "contact");
        } else if (contact.showInRoster()) {
            Log.e("contact", "show roaster");
        } else {
            final String preAuth = invite == null ? null : invite.getParameter(XmppUri.PARAMETER_PRE_AUTH);
            xmppConnectionService.createContact(contact, true, preAuth);
            if (invite != null && invite.hasFingerprints()) {
                xmppConnectionService.verifyFingerprints(contact, invite.getFingerprints());
            }

        }
        if (!openWindow) selectedCOntactCreateConnection(con);
    }


    @Override
    protected void refreshUiReal() {
        Log.e("refreshUiReal", "refreshUiReal");
        if (conversationFragment != null) {
            conversationFragment.refreshUiReal();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        final int theme = findTheme();
        if (this.mTheme != theme) {
            recreate();
        }
    }

    @Override
    public void onBackendConnected() {
        /*offline users*/
//        getPreferences().edit().putBoolean("hide_offline", true).apply();

//        filterListForGrp();
    }


    public Account getSelectedAccount() {
        if (xmppConnectionService != null) {
            Jid jid;
            String ip = MySharedPreferences.getSharedprefInstance().getChatip();
            if (ip == null || ip.equals("")) {
                ip = MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip();
            }
            try {
                if (Config.DOMAIN_LOCK != null) {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + ip, Config.DOMAIN_LOCK, null);
                } else {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + ip);
                }
            } catch (final IllegalArgumentException e) {
                return null;
            }
            final XmppConnectionService service = this.xmppConnectionService;
            if (service == null) {
                return null;
            }
            return service.findAccountByJid(jid);
        } else {
            return null;
        }
    }

    @Override
    public void onConversationUpdate() {
//        if (performRedirectIfNecessary(false)) {
//            return;
//        }
        Log.e("", "onConversationUpdate()");
        this.refreshUi();
    }

    @Override
    public void onConversationRead(Conversation conversation, String upToUuid) {
//        if (!mActivityPaused && pendingViewIntent.peek() == null) {
        Log.e("", "onConversationRead()");
        xmppConnectionService.sendReadMarker(conversation, upToUuid);
//        } else {
        Log.d(Config.LOGTAG, "ignoring read callback. mActivityPaused=" + "mActivityPaused");
    }

    @Override
    public void onRosterUpdate() {
        Log.e("", "onRosterUpdate()");
        this.refreshUi();
    }

    @Override
    public void onCreateDialogPositiveClick(Spinner spinners, String name,String grpdec) {
        if (!xmppConnectionServiceBound) {
            return;
        }
        final Account account = getSelectedAccount();
        if (account == null) {
            return;
        }
        Intent intent = new Intent(getApplicationContext(), ChooseContactActivity.class);
        intent.putExtra(ChooseContactActivity.EXTRA_SHOW_ENTER_JID, false);
        intent.putExtra(ChooseContactActivity.EXTRA_SELECT_MULTIPLE, true);
        intent.putExtra(ChooseContactActivity.EXTRA_GROUP_CHAT_NAME, name.trim());
        intent.putExtra(ChooseContactActivity.EXTRA_ACCOUNT, account.getJid().asBareJid().toEscapedString());
        intent.putExtra(ChooseContactActivity.EXTRA_TITLE_RES_ID, R.string.choose_participants);
        startActivityForResult(intent, REQUEST_CREATE_CONFERENCE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode == RESULT_OK) {
            if (xmppConnectionServiceBound) {
                if (requestCode == REQUEST_CREATE_CONFERENCE) {
                    Account account = extractAccount(intent);
                    final String name = intent.getStringExtra(ChooseContactActivity.EXTRA_GROUP_CHAT_NAME);
                    final List<Jid> jids = ChooseContactActivity.extractJabberIds(intent);
                    if (account != null && jids.size() > 0) {

                    }
                }
            } else {
            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.three_dots_img: {
                binding.progressBar.setVisibility(View.VISIBLE);
                syncMyContacts2.callApp();
            }
            case R.id.search_view: {
                hideShowTitleAndSearchBar(3);
                break;
            }
            case R.id.search_back: {
                hideShowTitleAndSearchBar(2);
                break;
            }
            case R.id.ic_backarrow: {
                hideShowTitleAndSearchBar(1);
                break;
            }

        }
    }

    private void hideShowTitleAndSearchBar(int type) {
        switch (type) {
            case 1: {
                if (binding.searchLayout.searchLay.getVisibility() == View.VISIBLE) {
                    binding.searchLayout.searchLay.setVisibility(View.GONE);
                    binding.searchLayout.menuLayout.setVisibility(View.VISIBLE);
                } else {
                    if (binding.searchLayout.txtSend.getVisibility() == View.VISIBLE) {
                        adapter.clerseleItemPosi();
                        adapter.notifyDataSetChanged();
                    } else {
                        this.finish();
                    }
                }
            }
            case 2: {
                binding.searchLayout.searchLay.setVisibility(View.GONE);
                binding.searchLayout.menuLayout.setVisibility(View.VISIBLE);
                adapter.listReload();
                hideSoftKeyboard(this);
            }
            case 3: {
                binding.searchLayout.searchLay.setVisibility(View.VISIBLE);
                binding.searchLayout.menuLayout.setVisibility(View.GONE);
            }
            case 4: {

            }
        }


    }


}

