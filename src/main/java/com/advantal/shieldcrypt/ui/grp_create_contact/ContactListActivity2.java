package com.advantal.shieldcrypt.ui.grp_create_contact;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.databinding.ContListLayoutBinding;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.entities.Contact;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.entities.ListItem;
import com.advantal.shieldcrypt.entities.Presence;
import com.advantal.shieldcrypt.network_pkg.MainViewModel;
import com.advantal.shieldcrypt.network_pkg.NetworkHelper;
import com.advantal.shieldcrypt.network_pkg.RequestApis;
import com.advantal.shieldcrypt.network_pkg.Resource;
import com.advantal.shieldcrypt.network_pkg.ResponceModel;
import com.advantal.shieldcrypt.network_pkg.Status;
import com.advantal.shieldcrypt.services.XmppConnectionService;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.ui.ConversationFragment;
import com.advantal.shieldcrypt.ui.CreatePrivateGroupChatDialog;
import com.advantal.shieldcrypt.ui.StartConversationActivity;
import com.advantal.shieldcrypt.ui.XmppActivity;
import com.advantal.shieldcrypt.ui.model.AddNewGrpBean;
import com.advantal.shieldcrypt.ui.model.ResponseItem;
import com.advantal.shieldcrypt.ui.model.UserAdd;
import com.advantal.shieldcrypt.ui.model.UsersItem;
import com.advantal.shieldcrypt.ui.util.SoftKeyboardUtils;
import com.advantal.shieldcrypt.utils.AccountUtils;
import com.advantal.shieldcrypt.utils.XmppUri;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import database.my_database_pkg.db_table.MyAppDataBase;

@AndroidEntryPoint
public class ContactListActivity2 extends XmppActivity implements ContactListAdpter.ContactSelected, View.OnClickListener, ContactAlphBetsAdpter.SelectAlphbets, CreatePrivateGroupChatDialog.CreateConferenceDialogListener {
    @Inject
    NetworkHelper networkHelper;
    ContListLayoutBinding binding;
    ArrayList<String> contactAlphList = new ArrayList<String>();
    private ConversationFragment conversationFragment;
    /*
        private final UiCallback<Conversation> mAdhocConferenceCallback = new UiCallback<Conversation>() {
            @Override
            public void success(final Conversation conversation) {
                runOnUiThread(() -> {
                    hideToast();
                    openFragment(conversation);
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
    */
    private MainViewModel mainViewModel;
    private ContactListAdpter adapter = null;
    private ContactAlphBetsAdpter myListAdapter = null;

    @Override
    protected void refreshUiReal() {
        Log.e("", "");
    }

    private void openFragment(Conversation conversation) {
        binding.contactFab.setVisibility(View.GONE);
        conversationFragment = new ConversationFragment();
        conversationFragment.reInit(conversation, new Bundle());
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, conversationFragment, null).setReorderingAllowed(true).addToBackStack("name") // name can be null
                .commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.cont_list_layout);

        this.binding = DataBindingUtil.setContentView(this, R.layout.cont_list_layout);

        contactAlphList.add("A");
        contactAlphList.add("B");
        contactAlphList.add("C");
        contactAlphList.add("D");
        contactAlphList.add("E");
        contactAlphList.add("F");
        contactAlphList.add("G");
        contactAlphList.add("H");
        contactAlphList.add("I");
        contactAlphList.add("J");
        contactAlphList.add("K");
        contactAlphList.add("L");
        contactAlphList.add("M");
        contactAlphList.add("N");
        contactAlphList.add("O");
        contactAlphList.add("P");
        contactAlphList.add("Q");
        contactAlphList.add("R");
        contactAlphList.add("S");
        contactAlphList.add("T");
        contactAlphList.add("U");
        contactAlphList.add("V");
        contactAlphList.add("W");
        contactAlphList.add("X");
        contactAlphList.add("Y");
        contactAlphList.add("Z");

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.getResponceCallBack().observeForever(new Observer<Resource<ResponceModel>>() {
            @Override
            public void onChanged(Resource<ResponceModel> responceModelResource) {

                if (responceModelResource.getStatus().equals(Status.SUCCESS)) {
                    AppUtills.Companion.closeProgressDialog();
                    if (responceModelResource.getData().getRequestCode() == RequestApis.ADD_USER_GROUP) {

                        ResponseItem item = new Gson().fromJson(responceModelResource.getData().getData(), ResponseItem.class);


                        MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().addGroupMember(item);
                        Account account = getSelectedAccount();

                        String groupName = "", groupDescription = "";
                        List<Jid> jabberIds = new ArrayList<>();
                        {
//                            for (ResponseItem responseItem : createGrpList)
                            {
                                groupName = item.getGroupName();
                                groupDescription = item.getGroupDescription();

                                for (UsersItem usersItem : item.getUsers()) {
                                    jabberIds.add(Jid.of((usersItem.getMobileNumber() + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())));
                                }
//
//                                if ((xmppConnectionService.createAdhocConference(account, groupName, jabberIds, mAdhocConferenceCallback,
//                                        item.getGroupJid()))) {
//                                }

                            }
                        }
                    }
                } else if (responceModelResource.getStatus().equals(Status.LOADING)) {
//                    AppUtills.Companion.setProgressDialog(ContactListActivity2.this);
                } else if (responceModelResource.getStatus().equals(Status.ERROR)) {

                    AppUtills.Companion.closeProgressDialog();
                    MyApp.Companion.getAppInstance().showToastMsg(responceModelResource.getMessage());
                }
            }
        });


        showContacts();
        binding.contactFab.setOnClickListener(this);
    }


    void craeteContact(StartConversationActivity.Invite invite, String con) {

        final Account account = xmppConnectionService.findAccountByJid(Jid.of((MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())));//8109383638@shieldcrypt.co.in

        final Contact contact = account.getRoster().getContact(Jid.of((con + "@" + "@broadcast." + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())));//9039404577@shieldcrypt.co.in


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
    }


    private void showContacts() {
        List<ContactDataModel> modelMutableList = new ArrayList<>();
        if (getIntent() != null && getIntent().hasExtra("contacts")) {
            modelMutableList = new Gson().fromJson(getIntent().getStringExtra("contacts"), new TypeToken<List<ContactDataModel>>() {
            }.getType());
        }
        adapter = new ContactListAdpter(this, modelMutableList, this);
        // Attach the adapter to the recyclerview to populate items
        binding.recycleContact.setAdapter(adapter);
        // Set layout manager to position the items
        binding.recycleContact.setLayoutManager(new LinearLayoutManager(this));
        // That's all!


        myListAdapter = new ContactAlphBetsAdpter(this, contactAlphList, this);
        binding.recycleAlphabets.setLayoutManager(new LinearLayoutManager(this));

        binding.recycleAlphabets.setAdapter(myListAdapter);


    }


    /*hide OFFLINE USERS*/
    protected void filterContacts() {
        List<ListItem> contacts = new ArrayList<>();
        String needle = "";
        final List<Account> accounts = xmppConnectionService.getAccounts();
        for (Account account : accounts) {
            if (account.getStatus() != Account.State.DISABLED) {
                for (Contact contact : account.getRoster().getContacts()) {
                    Presence.Status s = contact.getShownStatus();
                    if (contact.showInContactList() && contact.match(this, needle) && (!false || (needle != null && !needle.trim().isEmpty()) || s.compareTo(Presence.Status.OFFLINE) < 0)) {
                        contacts.add(contact);
                    }
                }
            }
        }
        Collections.sort(contacts);
    }


    @Override
    public void getSelectedAlphabets(@NonNull String alpha) {

    }

    private void showCreatePrivateGroupChatDialog() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_DIALOG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        CreatePrivateGroupChatDialog createConferenceFragment = CreatePrivateGroupChatDialog.newInstance(AccountUtils.getEnabledAccounts(xmppConnectionService));
        createConferenceFragment.show(ft, FRAGMENT_TAG_DIALOG);
    }

    private void selectedCOntactCreateConnection(String contactId) {
        boolean openWindow = false;
        Jid jid = Jid.ofEscaped(contactId + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());
        Account account = getSelectedAccount();
        Contact contact = null;
        Conversation conversation = null;

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

        if (openWindow) {
//            openWindow(conversation);
        } else {
            final StartConversationActivity.Invite invite = null;
            craeteContact(null, false, contactId);
        }

    }


    void craeteContact(StartConversationActivity.Invite invite, boolean openWindow, String contactId) {

        final Account account = xmppConnectionService.findAccountByJid(Jid.of((MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())));//8109383638@shieldcrypt.co.in

        final Contact contact = account.getRoster().getContact(Jid.of((contactId + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip())));


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
//            if (invite != null && invite.hasFingerprints()) {
//                xmppConnectionService.verifyFingerprints(contact, invite.getFingerprints());
//            }

        }
//        if (!openWindow) selectedCOntactCreateConnection(con);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.contact_fab:

                if (adapter != null && adapter.getSelectedItemPositionList().size() == 0) {
                    MyApp.Companion.getAppInstance().showToastMsg("At least 1 contact must be selected");
                } else if (getIntent() != null && getIntent().hasExtra("add_member_in_grp")) {
                    ArrayList<Integer> jList = adapter.getSelectedItemPositionList();
                    ArrayList<UserAdd> uList = new ArrayList<>();

                    for (int str : jList) {
                        uList.add(new UserAdd(str, false));
                    }
                    String grpdatastr = (new Gson().toJson(new AddNewGrpBean("", "", uList)));
                    MySharedPreferences.getSharedprefInstance().setChatGrpData(grpdatastr);

                    onBackPressed();
                } else {
                    createJid();
                }


                break;
        }
    }

    private void createJid() {
        showCreatePrivateGroupChatDialog();
    }

    @Override
    public void onBackendConnected() {
        filterContacts();
    }

    @Override
    public void getSelectMultipleCon(@NonNull ContactDataModel contactModel) {

    }

    public Account getSelectedAccount() {


        if (this instanceof XmppActivity) {
            Jid jid;
            try {
                if (Config.DOMAIN_LOCK != null) {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance().getXSip(), Config.DOMAIN_LOCK, null);
                } else {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());
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
    public void onCreateDialogPositiveClick(Spinner spinner, String subject, String grpDescr) {
        ArrayList<Integer> jList = adapter.getSelectedItemPositionList();
        ArrayList<UserAdd> uList = new ArrayList<>();
        int id = Integer.parseInt((MySharedPreferences.getSharedprefInstance().getLoginData().userid));
        uList.add(new UserAdd(id, true));

        for (int str : jList) {
            uList.add(new UserAdd(str, false));
        }
        String grpdatastr = (new Gson().toJson(new AddNewGrpBean(subject, grpDescr, uList)));


        MySharedPreferences.getSharedprefInstance().setChatGrpData(grpdatastr);
        SoftKeyboardUtils.hideSoftKeyboard(this);

        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }
}