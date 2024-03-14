package com.advantal.shieldcrypt.sip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.network_pkg.ApiService;
import com.advantal.shieldcrypt.sip.adapter.IthubContactForChatAdapter;
import com.advantal.shieldcrypt.sip.model.ConfCallBean;
import com.advantal.shieldcrypt.sip.utils.Utils;
import com.advantal.shieldcrypt.tabs_pkg.fab_contact_pkg.ContactDataModel;
import com.advantal.shieldcrypt.utils_pkg.AppConstants;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import database.my_database_pkg.db_table.MyAppDataBase;
import org.pjsip.pjsua2.CallInfo;
import net.gotev.sipservice.SipAccount;
import net.gotev.sipservice.SipCall;
import net.gotev.sipservice.SipServiceCommand;

public class IthubContactForChatActivity extends AppCompatActivity {

    TextView tv_no_contacts;
    TextView tv_add_number;

    MaterialToolbar topAppBar;

    RecyclerView rv_contacts;

    EditText et_search;

    ProgressBar progressBar;

    SwipeRefreshLayout mSwipeRefreshLayout;

    String userName = "";

    MySharedPreferences mySharedPreferences;

    IthubContactForChatAdapter ithubContactForChatAdapter;

    private boolean placecall = false;

    List<ContactDataModel> al_ithub_contact = new ArrayList<>();

    String intentTag = "", audioVideoWayStatus = "";

    public static SharedPrefrence share;

   // AppDataBase appDataBase;

    private ProgressDialog progress;
    ApiService apiservice;
    ArrayList<ConfCallBean> al_call_id = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.ithub_contact_for_chat_activity);

     //   appDataBase = AppDataBase.getAppDatabase(IthubContactForChatActivity.this);

        tv_no_contacts = findViewById(R.id.tv_no_contacts);
        tv_add_number = findViewById(R.id.tv_add_number);

        mySharedPreferences = new MySharedPreferences();
        share = SharedPrefrence.getInstance(this);

        topAppBar = (MaterialToolbar)findViewById(R.id.topAppBar);

        rv_contacts = findViewById(R.id.rv_contacts);

        et_search = findViewById(R.id.et_search);

        progressBar = findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

       // Utils.setStatusBarColor(IthubContactForChatActivity.this, R.color.mode_blue_black);
      //  userName = MySharedPreferences.getSharedprefInstance().getLoginData().getUserName;

        /*setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);*/

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
// Refresh items
                refreshItems();
            }
        });

        if (getIntent().hasExtra(AppConstants.ithubContactIntentTag)) {
            intentTag = getIntent().getStringExtra(AppConstants.ithubContactIntentTag);
        }
        if (getIntent().hasExtra("VideoAudioWayScreen")) {
            audioVideoWayStatus = getIntent().getStringExtra("VideoAudioWayScreen");
        }

        //al_ithub_contact = appDataBase.ithubContactDao().getAllIthubContacts();
       // String mobileNumberNew = mySharedPreferences.getLoginData().mobileNumber;
        String mobileNumber = SharedPrefrence.getInstance(IthubContactForChatActivity.this).getValue(AppConstants.loggedInUserNumber);
        al_ithub_contact = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao()
                .getAllNotes(mobileNumber);
        callITHUBContactAdapter();

        /*if (al_ithub_contact.size() == 0) {
            createJsonToGetIthubContacts();
        }*/

        tv_add_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumberDialog(IthubContactForChatActivity.this);
            }
        });

        // called when search field text has to be changed
        et_search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                try {
                    if (cs.toString() != null) {
                        if (cs.toString().length() > 0) {
                            et_search.setHint("");
                        } else {
                            et_search.setHint(getString(R.string.search_dot_dot));
                        }
                        String txt = cs.toString().trim().toLowerCase();

                        boolean isSearchNumeric = android.text.TextUtils.isDigitsOnly(cs);
                        if (isSearchNumeric) {
                            ithubContactForChatAdapter.getFilterByNum(txt);
                        } else {
                            ithubContactForChatAdapter.getFilter(txt);
                        }

                    } else {
                        et_search.setHint(getString(R.string.search_dot_dot));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getActiveCall();
    }


    void refreshItems() {
        createJsonToGetIthubContacts();

// Load complete
        onItemsLoadComplete();
    }

    void onItemsLoadComplete() {
// Stop refresh animation
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void callITHUBContactAdapter() {
        if (al_ithub_contact != null && al_ithub_contact.size() > 0) {
            hideNoContacts();
        } else {
            showNoContacts();
        }
        sortIthubContacts();
        ithubContactForChatAdapter = new IthubContactForChatAdapter(IthubContactForChatActivity.this,
                IthubContactForChatActivity.this, al_ithub_contact, intentTag,
                new IthubContactForChatAdapter.CallBack() {
                    @Override
                    public void doClickItemAdd(String number) {
                        if (number!=null && !number.isEmpty()){
                            if (!Utils.checkInternetConn(IthubContactForChatActivity.this)) {
                                Toast.makeText(IthubContactForChatActivity.this, getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                            } else {
                                boolean activeNumberStatus = false;
                                if (al_call_id!=null && al_call_id.size()>0){
                                    if (number!=null && !number.isEmpty()){
                                        for (int i=0;i<al_call_id.size();i++){
                                            if (number.equals(al_call_id.get(i).getNumber())){
                                                activeNumberStatus = true;
                                                break;
                                            }
                                        }
                                        if (!activeNumberStatus){
                                            if (audioVideoWayStatus.equals("audio")){
                                                initiateCall(number);
                                            } else if (audioVideoWayStatus.equals("video")){
                                                initiateVideoCall(number);
                                            }
                                        } else {
                                            Toast.makeText(IthubContactForChatActivity.this, "Already added in call.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rv_contacts.setLayoutManager(layoutManager);
        rv_contacts.setItemAnimator(new DefaultItemAnimator());
        rv_contacts.setAdapter(ithubContactForChatAdapter);
    }

    public void showNoContacts() {
        tv_no_contacts.setVisibility(View.VISIBLE);
    }

    public void hideNoContacts() {
        tv_no_contacts.setVisibility(View.GONE);
    }

    // Sort all the contacts alphabatically
    public void sortIthubContacts() {
        try {
            if (al_ithub_contact.size() != 0)
                Collections.sort(al_ithub_contact, new Comparator<ContactDataModel>() {

                    @Override
                    public int compare(ContactDataModel lhs, ContactDataModel rhs) {
                        return lhs.getContactName()/*getName()*/.compareToIgnoreCase(rhs.getContactName()/*getName()*/);
                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createJsonToGetIthubContacts() {
        JSONObject jsonObject = new JSONObject();
        JsonObject gsonObject = new JsonObject();
        JsonParser jsonParser = new JsonParser();
        try {
            jsonObject.put("username", userName);
            gsonObject = (JsonObject) jsonParser.parse(jsonObject.toString());
            //callGetIthubContactsApi(gsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Call get Ithub Contacts api
//    private void callGetIthubContactsApi(JsonObject body) {
//        apiservice = ApiClient.getClient().create(ApiInterface.class);
//        Call<IthubContactsResponseBean> call = apiservice.getIthubContactsApi(Utils.getBasicAuthenticationString(), body);
//        progress = new ProgressDialog(IthubContactForChatActivity.this);
//        progress.setMessage("Loading ...");
//        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progress.setIndeterminate(true);
//        progress.setProgress(0);
//        progress.setCancelable(false);
////        progress.show();
//        progressBar.setVisibility(View.VISIBLE);
//        call.enqueue(new Callback<IthubContactsResponseBean>() {
//            @Override
//            public void onResponse(Call<IthubContactsResponseBean> call, Response<IthubContactsResponseBean> response) {
//
//                try {
//                    String responseMessage = response.body().getMessage();
//                    if (response.body().getStatus().equalsIgnoreCase(AppConstants.successResponse)) {
//                        List<IthubContactsObjectBean> al_temp_ithub_list = new ArrayList<>();
//                        al_temp_ithub_list = response.body().getIthubContactsObject();
//                        appDataBase.ithubContactDao().deleteAllIthubContacts();
//                        IthubContactsModel myIthubContactsModel = new IthubContactsModel();
//                        // Add my contact in db
//                        myIthubContactsModel = Utils.getIthubContactModel(mySharedPreferences.getValue(AppConstants.userName), mySharedPreferences.getValue(AppConstants.mobileNumber),
//                                mySharedPreferences.getValue(AppConstants.firstName), mySharedPreferences.getValue(AppConstants.lastName), mySharedPreferences.getValue(AppConstants.emailId),
//                                mySharedPreferences.getValue(AppConstants.firstName) + " " + mySharedPreferences.getValue(AppConstants.lastName)
//                                , "0", mySharedPreferences.getValue(AppConstants.displayName));
//                        appDataBase.ithubContactDao().insertIthubContact(myIthubContactsModel);
//                        for (int i = 0; i < al_temp_ithub_list.size(); i++) {
//                            IthubContactsModel ithubContactsModel = new IthubContactsModel();
//                            IthubContactsObjectBean ithubContactsObjectBean = al_temp_ithub_list.get(i);
//
////                            String contactName = Utils.getContactNameOrUserFirstNameLastName(ithubContactsObjectBean.getMobileNumber(),
////                                    ithubContactsObjectBean.getFirstName(), ithubContactsObjectBean.getLastName(), IthubContactForChatActivity.this);
//
//                            String contactName = ithubContactsObjectBean.getFirstName() + " " + ithubContactsObjectBean.getLastName();
//
//                            ithubContactsModel = Utils.getIthubContactModel(ithubContactsObjectBean.getUsername(), ithubContactsObjectBean.getMobileNumber(),
//                                    ithubContactsObjectBean.getFirstName(), ithubContactsObjectBean.getLastName(), ithubContactsObjectBean.getEmailId(),
//                                    contactName, ithubContactsObjectBean.getIsBlocked(), ithubContactsObjectBean.getDisplayName());
//
//                            appDataBase.ithubContactDao().insertIthubContact(ithubContactsModel);
////                            al_ithub_contact.get(i).setName(Utils.getContactNameOrUserFirstNameLastName(ithubContactsObjectBean.getMobileNumber(),
////                                    ithubContactsObjectBean.getFirstName(), ithubContactsObjectBean.getLastName(), getActivity()));
//                        }
//                        al_ithub_contact = appDataBase.ithubContactDao().getAllIthubContacts();
////                        ithubContactAdapter.notifyDataSetChanged();
//                        callITHUBContactAdapter();
//                        progressBar.setVisibility(View.GONE);
//                    } else if (response.body().getStatus().equalsIgnoreCase(AppConstants.failResponse)) {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                } catch (Exception e) {
//                    progressBar.setVisibility(View.GONE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<IthubContactsResponseBean> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//            }
//        });
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    // make video call when user press the call button
    public void initiateVideoCall(String number) {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);
//        createJsonToAddCallLogs(mobileRegistered, number);
        if (number.isEmpty()) {
            number = "*9000";
        }
        //  number = "000000038" + number;
        Log.d("sachin", " num " + number + " -> "+ id);
        SipServiceCommand.makeCall(this, id, number, true, false);
        finish();
    }

    // make call when user press the call button
    public void initiateCall(String number) {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);

//        createJsonToAddCallLogs(mobileRegistered, number);

        if (number.isEmpty()) {
            number = "*9000";
        }
      //  number = "000000038" + number;
        Log.d("sachin", " num " + number + " -> "+ id);
        SipServiceCommand.makeCall(this, id, number);
        finish();
    }

    public void addNumberDialog(final Activity activity) {
        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, R.style.Dialog);
// ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_add_number, null);
            dialogBuilder.setView(dialogView);

            final EditText et_number = (EditText) dialogView.findViewById(R.id.et_number);
            Button btn_add = dialogView.findViewById(R.id.btn_add);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            final AlertDialog show = dialogBuilder.show();
            final Window window = show.getWindow();

            btn_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String addNumber = et_number.getText().toString().trim();
                    if (Utils.checkInternetConn(IthubContactForChatActivity.this)) {
                        try {
                            View dialogView = show.getCurrentFocus();
                            if (dialogView != null) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialogBuilder.setCancelable(true);
                        show.dismiss();
                        initiateCall(addNumber);
                    } else {
                        Toast.makeText(IthubContactForChatActivity.this, getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                    }

                }
            });

            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        View dialogView = show.getCurrentFocus();
                        if (dialogView != null) {
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialogBuilder.setCancelable(true);
                    show.dismiss();

                }
            });

        } catch (Exception e) {

        }
    }

    public void getActiveCall(){
        HashMap<Integer, SipCall> activeCalls = new HashMap<>();
        activeCalls = SipAccount.activeCalls;
        for (int callid : activeCalls.keySet()) {
            String contactName = getContactName(activeCalls, callid);
            Log.e("nameWithUri", " contactName-> " + contactName);
            ConfCallBean confCallBean = new ConfCallBean();
            confCallBean.setCallId(callid);
            confCallBean.setName(contactName);
            confCallBean.setNumber(contactName);
            al_call_id.add(confCallBean);
        }
    }

    public String getContactName(HashMap<Integer, SipCall> activeCalls, int callid) {
        String contactName = "";
        SipCall call = activeCalls.get(callid);
        try {
            CallInfo callInfo = call.getInfo();
            if (callInfo != null) {
                String nameWithUri = callInfo.getRemoteUri();
                String numberWithoutIp = Utils.getidwithoutip(nameWithUri);
                try {
                    if (numberWithoutIp!=null && !numberWithoutIp.isEmpty()){
                        contactName = Utils.getNumberFromSipPath(numberWithoutIp);
                    } else {
                        contactName = numberWithoutIp;
                    }
                } catch (Exception e) {
                    contactName = numberWithoutIp;
                    e.printStackTrace();
                }
            } else {
                contactName = callid + "";
            }
        } catch (Exception e) {
            contactName = callid + "";
            e.printStackTrace();
        }
        return contactName;
    }
}
