package com.advantal.shieldcrypt.sip.videoconf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.sip.SharedPrefrence;
import com.advantal.shieldcrypt.sip.adapter.IthubContactForChatAdapter;
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
import java.util.List;

import database.my_database_pkg.db_table.MyAppDataBase;
import net.gotev.sipservice.SipServiceCommand;

public class ContactForVideoConfDialogFragment extends DialogFragment {

    public static SharedPrefrence share;
    View rootView;
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
    List<ContactDataModel> al_ithub_contact = new ArrayList<>();
    private LinearLayout ll_search;
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.ithub_contact_for_chat_activity, container, false);

        tv_no_contacts = rootView.findViewById(R.id.tv_no_contacts);
        tv_add_number = rootView.findViewById(R.id.tv_add_number);

        mySharedPreferences = new MySharedPreferences();
        share = SharedPrefrence.getInstance(requireActivity());

        ll_search = rootView.findViewById(R.id.ll_search);
        ll_search.setVisibility(View.GONE);
        topAppBar = (MaterialToolbar) rootView.findViewById(R.id.topAppBar);

        rv_contacts = rootView.findViewById(R.id.rv_contacts);

        et_search = rootView.findViewById(R.id.et_search);

        progressBar = rootView.findViewById(R.id.progressBar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
// Refresh items
                refreshItems();
            }
        });

        String mobileNumber = SharedPrefrence.getInstance(requireActivity()).getValue(AppConstants.loggedInUserNumber);
        al_ithub_contact = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).contactDao().getAllNotes(mobileNumber);
        callITHUBContactAdapter();

        tv_add_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNumberDialog(requireActivity());
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
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
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
                dismiss();
            }
        });

        return rootView;
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
        ithubContactForChatAdapter = new IthubContactForChatAdapter(getActivity(), al_ithub_contact, "call", new IthubContactForChatAdapter.CallBack() {
            @Override
            public void doClickItemAdd(String number) {
                if (number != null && !number.isEmpty()) {
                    if (!Utils.checkInternetConn(getActivity())) {
                        Toast.makeText(getActivity(), getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
                    } else {
                        //initiateVideoCall(number);
                        if (callBack != null) {
                            if (number != null && !number.isEmpty()) {
                                callBack.doClickMakeCall(number);
                            }
                        }
                        dismiss();
                    }
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
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
        Log.d("sachin", " num " + number + " -> " + id);
        SipServiceCommand.makeCall(getActivity(), id, number, true, true);
        getActivity().finish();
    }

    // make call when user press the call button
    public void initiateCall(String number) {
        String id = share.getValue(SharedPrefrence.SIPACCOUNTID);

//        createJsonToAddCallLogs(mobileRegistered, number);

        if (number.isEmpty()) {
            number = "*9000";
        }
        //  number = "000000038" + number;
        Log.d("sachin", " num " + number + " -> " + id);
        SipServiceCommand.makeCall(getActivity(), id, number);
        getActivity().finish();
    }

    public void addNumberDialog(final Activity activity) {
        try {

            final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireActivity(), R.style.Dialog);
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
                    if (Utils.checkInternetConn(getActivity())) {
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
                        //  initiateCall(addNumber);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.internet_check), Toast.LENGTH_SHORT).show();
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

    public interface CallBack {
        void doClickMakeCall(String number);
    }
}
