/*
 * Copyright (c) 2018, Daniel Gultsch All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.advantal.shieldcrypt.ui;

//import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;
//import static androidx.recyclerview.widget.ItemTouchHelper.RIGHT;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.auth_pkg.AuthenticationActivity;
import com.advantal.shieldcrypt.databinding.FragmentConversationsOverviewBinding;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.network_pkg.MainViewModel;
import com.advantal.shieldcrypt.network_pkg.NetworkHelper;
import com.advantal.shieldcrypt.network_pkg.RequestApis;
import com.advantal.shieldcrypt.network_pkg.Resource;
import com.advantal.shieldcrypt.network_pkg.ResponceModel;
import com.advantal.shieldcrypt.network_pkg.Status;
import com.advantal.shieldcrypt.services.XmppConnectionService;
import com.advantal.shieldcrypt.ui.adapter.ConversationAdapter;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationRead;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationsListItemUpdated;
import com.advantal.shieldcrypt.ui.model.ResponseItem;
import com.advantal.shieldcrypt.ui.model.UsersItem;
import com.advantal.shieldcrypt.ui.util.MenuDoubleTabUtil;
import com.advantal.shieldcrypt.ui.util.PendingActionHelper;
import com.advantal.shieldcrypt.ui.util.PendingItem;
import com.advantal.shieldcrypt.ui.util.ScrollState;
import com.advantal.shieldcrypt.utils.AccountUtils;
import com.advantal.shieldcrypt.utils.EasyOnboardingInvite;
import com.advantal.shieldcrypt.utils_pkg.AppUtills;
import com.advantal.shieldcrypt.utils_pkg.FragmentCommunicator;
import com.advantal.shieldcrypt.utils_pkg.MyApp;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.google.common.collect.Collections2;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import database.my_database_pkg.db_table.MyAppDataBase;

@AndroidEntryPoint
public class ConversationsOverviewFragment extends XmppFragment implements XmppConnectionService.OnConversationUpdate, OnConversationRead, OnConversationsListItemUpdated, XmppConnectionService.OnRosterUpdate {
    private static final String STATE_SCROLL_POSITION = ConversationsOverviewFragment.class.getName() + ".scroll_state";
    private final PendingItem<Conversation> swipedConversation = new PendingItem<>();
    private final PendingItem<ScrollState> pendingScrollState = new PendingItem<>();
    private final PendingActionHelper pendingActionHelper = new PendingActionHelper();
    private final List<Conversation> conversations = new ArrayList<>();
    private final Handler mTimerHandler = new Handler();
    private final UiCallback<Conversation> mAdhocConferenceCallback = new UiCallback<Conversation>() {
        @Override
        public void success(final Conversation conversation) {
            runOnUiThread(() -> {

            });
        }

        @Override
        public void error(final int errorCode, Conversation object) {
        }

        @Override
        public void userInputRequired(PendingIntent pi, Conversation object) {

        }
    };
    @Inject
    NetworkHelper networkHelper;
    int c = 0;
    private Account account = null;
    private MainViewModel mainViewModel;
    private Timer mTimer1;
    private TimerTask mTt1;
    private FragmentConversationsOverviewBinding binding;

    private ConversationAdapter conversationsAdapter;
    private XmppActivity xmppActivity;
    private float mSwipeEscapeVelocity = 0f;
    /*
        private final ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, LEFT | RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //todo maybe we can manually changing the position of the conversation
                return false;
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return mSwipeEscapeVelocity;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    Paint paint = new Paint();
                    paint.setColor(StyledAttributes.getColor(xmppActivity, R.attr.conversations_overview_background));
                    paint.setStyle(Paint.Style.FILL);
                    c.drawRect(viewHolder.itemView.getLeft(), viewHolder.itemView.getTop(), viewHolder.itemView.getRight(), viewHolder.itemView.getBottom(), paint);
                }
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1f);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                pendingActionHelper.execute();
                int position = viewHolder.getLayoutPosition();
                try {
                    swipedConversation.push(conversations.get(position));
                } catch (IndexOutOfBoundsException e) {
                    return;
                }
                conversationsAdapter.remove(swipedConversation.peek(), position);
                xmppActivity.xmppConnectionService.markRead(swipedConversation.peek());

                if (position == 0 && conversationsAdapter.getItemCount() == 0) {
                    final Conversation c = swipedConversation.pop();
                    xmppActivity.xmppConnectionService.archiveConversation(c);
                    return;
                }
                final boolean formerlySelected = ConversationFragment.getConversation(getActivity()) == swipedConversation.peek();
                if (xmppActivity instanceof OnConversationArchived) {
                    ((OnConversationArchived) xmppActivity).onConversationArchived(swipedConversation.peek());
                }
                final Conversation c = swipedConversation.peek();
                final int title;
                if (c.getMode() == Conversational.MODE_MULTI) {
                    if (c.getMucOptions().isPrivateAndNonAnonymous()) {
                        title = R.string.title_undo_swipe_out_group_chat;
                    } else {
                        title = R.string.title_undo_swipe_out_channel;
                    }
                } else {
                    title = R.string.title_undo_swipe_out_conversation;
                }

                final Snackbar snackbar = Snackbar.make(binding.list, title, 5000).setAction(R.string.undo, v -> {
                    pendingActionHelper.undo();
                    Conversation conversation = swipedConversation.pop();
                    conversationsAdapter.insert(conversation, position);
                    if (formerlySelected) {
                        if (xmppActivity instanceof OnConversationSelected) {
                            ((OnConversationSelected) xmppActivity).onConversationSelected(c);
                        }
                    }
                    LinearLayoutManager layoutManager = (LinearLayoutManager) binding.list.getLayoutManager();
                    if (position > layoutManager.findLastVisibleItemPosition()) {
                        binding.list.smoothScrollToPosition(position);
                    }
                }).addCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar transientBottomBar, int event) {
                        switch (event) {
                            case DISMISS_EVENT_SWIPE:
                            case DISMISS_EVENT_TIMEOUT:
                                pendingActionHelper.execute();
                                break;
                        }
                    }
                });

                pendingActionHelper.push(() -> {
                    if (snackbar.isShownOrQueued()) {
                        snackbar.dismiss();
                    }
                    final Conversation conversation = swipedConversation.pop();
                    if (conversation != null) {
                        if (!conversation.isRead() && conversation.getMode() == Conversation.MODE_SINGLE) {
                            return;
                        }
                        xmppActivity.xmppConnectionService.archiveConversation(c);
                    }
                });

                ThemeHelper.fix(snackbar);
                snackbar.show();
            }
        };
    */
    private ItemTouchHelper touchHelper;

    public static ConversationsOverviewFragment newInstance() {
        Bundle args = new Bundle();
        ConversationsOverviewFragment fragment = new ConversationsOverviewFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            return;
        }
        pendingScrollState.push(savedInstanceState.getParcelable(STATE_SCROLL_POSITION));
    }

    @Override
    public void onDestroyView() {
        Log.d(Config.LOGTAG, "ConversationsOverviewFragment.onDestroyView()");
        super.onDestroyView();
        this.binding = null;
        this.conversationsAdapter = null;
        this.touchHelper = null;
    }

    @Override
    public void onDestroy() {
        Log.d(Config.LOGTAG, "ConversationsOverviewFragment.onDestroy()");
        super.onDestroy();

    }

    @Override
    public void onPause() {
        Log.d(Config.LOGTAG, "ConversationsOverviewFragment.onPause()");
        pendingActionHelper.execute();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        this.xmppActivity = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AuthenticationActivity) getActivity()).passVal(new FragmentCommunicator() {
            @Override
            public void openChatWindow(String uuid) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("VENUE_NAME", uuid);
                AuthenticationActivity.Companion.setFLAGNOTIFY(2);
                Navigation.findNavController(binding.getRoot()).navigate(R.id.action_tablayoutFragment_to_openConversationFragment, bundle);
            }

            @Override
            public void passData() {
                updateAdater();

                Log.e("", "");
            }

            @Override
            public void accountNotify(@NonNull Account accounts) {
                account = accounts;
                insertDataInCon();
            }

            @Override
            public void firstLoadGrpList() {
                if (mainViewModel != null)
                    mainViewModel.callGetApiWithToken2(RequestApis.GET_USER_GROUP);

            }

           /* @Override
            public void openChatWindowForGrp(Conversation conversation) {
                openChatWindowbyClick(binding.getRoot(), conversation);
            }*/
        });
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        mainViewModel.callGetApiWithToken2(RequestApis.GET_USER_GROUP);

        mainViewModel.getResponceCallBack().observeForever(new Observer<Resource<ResponceModel>>() {
            @Override
            public void onChanged(Resource<ResponceModel> responceModelResource) {

                if (responceModelResource.getStatus().equals(Status.SUCCESS)) {
                    AppUtills.Companion.closeProgressDialog();
                    if (responceModelResource.getData().getRequestCode() == RequestApis.GET_USER_GROUP) {
                        TypeToken<List<ResponseItem>> token = new TypeToken<List<ResponseItem>>() {
                        };
                        List<ResponseItem> createGrpList = new Gson().fromJson(responceModelResource.getData().getData(), token.getType());

                        MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().addAllGroupMember(createGrpList);


                        String groupName = "", groupDescription = "";
                        List<Jid> jabberIds = new ArrayList<>();

                        for (ResponseItem responseItem : createGrpList) {
                            groupName = responseItem.getGroupName();
                            groupDescription = responseItem.getGroupDescription();
                            for (UsersItem usersItem : responseItem.getUsers()) {
                                jabberIds.add(Jid.of((usersItem.getMobileNumber() + "@" + MySharedPreferences.getSharedprefInstance().getChatip())));
                            }

                            if (account == null) {
                                account = getSelectedAccount();
                            }
                            if (account != null)
                                if (xmppActivity.xmppConnectionService.createAdhocConference(account, groupName, jabberIds, mAdhocConferenceCallback, responseItem.getGroupJid())) {
                                }

                        }
                        filtterConversation();

                    }

                } else if (responceModelResource.getStatus().equals(Status.LOADING)) {
                    AppUtills.Companion.closeProgressDialog();
                } else if (responceModelResource.getStatus().equals(Status.ERROR)) {

                    AppUtills.Companion.closeProgressDialog();
//                    MyApp.Companion.getAppInstance().showToastMsg(responceModelResource.getMessage());
                }
            }
        });

        insertDataInCon();

    }

    private void updateAdater() {
        if (conversationsAdapter != null) {
            runOnUiThread(new Runnable() {
                public void run() {
                    filtterConversation();
                }
            });
        }
    }

    public Account getSelectedAccount() {

        if (xmppActivity.xmppConnectionService != null) {
            Jid jid;
            try {
                if (Config.DOMAIN_LOCK != null) {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber +

                            "@" + MySharedPreferences.getSharedprefInstance().getChatip(), Config.DOMAIN_LOCK, null);
                } else {
                    jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber + "@" + MySharedPreferences.getSharedprefInstance().getChatip());
                }
            } catch (final IllegalArgumentException e) {
                return null;
            }
            final XmppConnectionService service = xmppActivity.xmppConnectionService;
            if (service == null) {
                return null;
            }
            account = service.findAccountByJid(jid);
            return service.findAccountByJid(jid);
        } else {
            return null;
        }

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mSwipeEscapeVelocity = getResources().getDimension(R.dimen.swipe_escape_velocity);
        this.binding = DataBindingUtil.inflate(inflater, R.layout.fragment_conversations_overview, container, false);


        binding.pullRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainViewModel.callGetApiWithToken2(RequestApis.GET_USER_GROUP);
                binding.pullRefreshLayout.setRefreshing(false);
            }
        });


        mainViewModel.getResponceCallBack().observeForever(new Observer<Resource<ResponceModel>>() {
            @Override
            public void onChanged(Resource<ResponceModel> responceModelResource) {
                insertDataInCon();
            }
        });


        this.binding.fab.setOnClickListener((view) -> {
            Intent intent = new Intent(getActivity(), AllContactActivity.class);
            startActivity(intent);
        });
        if (conversations.size() == 0) {
            if (xmppActivity.xmppConnectionService != null) filtterConversation();
        }
        this.conversationsAdapter = new ConversationAdapter(this.xmppActivity, this.conversations);
        this.conversationsAdapter.setConversationClickListener((view, conversation) -> {
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("VENUE_NAME", conversation.getUuid());
//            AuthenticationActivity.Companion.setFLAGNOTIFY(2);
//            Navigation.findNavController(view).navigate(R.id.action_tablayoutFragment_to_openConversationFragment, bundle);
            openChatWindowbyClick(view, conversation);
        });
        this.binding.list.setAdapter(this.conversationsAdapter);
        this.binding.list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        this.touchHelper = new ItemTouchHelper(this.callback);
//        this.touchHelper.attachToRecyclerView(this.binding.list);
        return binding.getRoot();
    }

    private void openChatWindowbyClick(View view, Conversation conversation) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("VENUE_NAME", conversation.getUuid());
        AuthenticationActivity.Companion.setFLAGNOTIFY(2);
        Navigation.findNavController(view).navigate(R.id.action_tablayoutFragment_to_openConversationFragment, bundle);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof XmppActivity) {
            this.xmppActivity = (XmppActivity) context;
        } else if (context instanceof AuthenticationActivity) {
            this.xmppActivity = (AuthenticationActivity) context;
        } else if (context instanceof StartConversationActivity) {
            this.xmppActivity = (StartConversationActivity) context;
        } else if (context instanceof ConversationsActivity) {
            this.xmppActivity = (ConversationsActivity) context;
        } else {
            throw new IllegalStateException("Trying to attach fragment to xmppActivity that is not an XmppActivity");
        }
        getSelectedAccount();
        insertDataInCon();
        Log.e("", "");
    }

    private void insertDataInCon() {

        List<ResponseItem> createGrpList = MyAppDataBase.Companion.getUserDataBaseAppinstance(MyApp.Companion.getAppInstance()).groupDao().getAllGrpList();


        String groupName = "", groupDescription = "";
        List<Jid> jabberIds = new ArrayList<>();

        for (ResponseItem responseItem : createGrpList) {
            groupName = responseItem.getGroupName();
            groupDescription = responseItem.getGroupDescription();
            for (UsersItem usersItem : responseItem.getUsers()) {
                jabberIds.add(Jid.of((usersItem.getMobileNumber() + "@" + MySharedPreferences.getSharedprefInstance().getChatip())));
            }

            if (account == null) {
                account = getSelectedAccount();
            }
            if (account != null && xmppActivity.xmppConnectionService != null)
                if (xmppActivity.xmppConnectionService.createAdhocConference(account, groupName, jabberIds, mAdhocConferenceCallback, responseItem.getGroupJid())) {

                }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                filtterConversation();
            }
        }, 200);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.fragment_conversations_overview, menu);
        AccountUtils.showHideMenuItems(menu);
        final MenuItem easyOnboardInvite = menu.findItem(R.id.action_easy_invite);
        easyOnboardInvite.setVisible(EasyOnboardingInvite.anyHasSupport(xmppActivity == null ? null : xmppActivity.xmppConnectionService));
    }

    @Override
    public void onBackendConnected() {
        refresh();
        account = getSelectedAccount();
//        mainViewModel.callGetApiWithToken2(RequestApis.GET_USER_GROUP);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        ScrollState scrollState = getScrollState();
        if (scrollState != null) {
            bundle.putParcelable(STATE_SCROLL_POSITION, scrollState);
        }
    }

    private ScrollState getScrollState() {
        if (this.binding == null) {
            return null;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.binding.list.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        final View view = this.binding.list.getChildAt(0);
        if (view != null) {
            return new ScrollState(position, view.getTop());
        } else {
            return new ScrollState(position, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(Config.LOGTAG, "ConversationsOverviewFragment.onStart()");
        if (xmppActivity.xmppConnectionService != null) {
            refresh();
        }
//        startTimer();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(Config.LOGTAG, "ConversationsOverviewFragment.onResume()");

        if (this.xmppActivity.xmppConnectionService != null) {
            filtterConversation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopTimer();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (MenuDoubleTabUtil.shouldIgnoreTap()) {
            return false;
        }
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(getActivity(), SearchActivity.class));
                return true;
            case R.id.action_easy_invite:
                selectAccountToStartEasyInvite();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectAccountToStartEasyInvite() {
        final List<Account> accounts = EasyOnboardingInvite.getSupportingAccounts(xmppActivity.xmppConnectionService);
        if (accounts.size() == 0) {
            //This can technically happen if opening the menu item races with accounts reconnecting or something
            Toast.makeText(getActivity(), R.string.no_active_accounts_support_this, Toast.LENGTH_LONG).show();
        } else if (accounts.size() == 1) {
            openEasyInviteScreen(accounts.get(0));
        } else {
            final AtomicReference<Account> selectedAccount = new AtomicReference<>(accounts.get(0));
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(xmppActivity);
            alertDialogBuilder.setTitle(R.string.choose_account);
            final String[] asStrings = Collections2.transform(accounts, a -> a.getJid().asBareJid().toEscapedString()).toArray(new String[0]);
            alertDialogBuilder.setSingleChoiceItems(asStrings, 0, (dialog, which) -> selectedAccount.set(accounts.get(which)));
            alertDialogBuilder.setNegativeButton(R.string.cancel, null);
            alertDialogBuilder.setPositiveButton(R.string.ok, (dialog, which) -> openEasyInviteScreen(selectedAccount.get()));
            alertDialogBuilder.create().show();
        }
    }

    private void openEasyInviteScreen(final Account account) {
        EasyOnboardingInviteActivity.launch(account, xmppActivity);
    }

    private void filtterConversation() {

        /*********************/
        /********all update data call back********/
        if (xmppActivity.xmppConnectionService != null) {
            conversations.clear();
            xmppActivity.xmppConnectionService.populateWithOrderedConversations(this.conversations);
        }
        List<Conversation> tempconversations = new ArrayList<>();

        for (Conversation conversation : conversations) {
            if (conversation.getJid().asBareJid().toString().contains("@broadcast")) {
                tempconversations.add(conversation);
            } else if ((conversation.getLatestMessage() != null && !conversation.getLatestMessage().getBody().equals("")) || conversation.getmessages().size() > 0) {
                tempconversations.add(conversation);
            }
        }

        conversations.clear();
        conversations.addAll(tempconversations);
        try {
            if (conversations != null && conversations.size() > 0) {
                if (conversationsAdapter != null) conversationsAdapter.notifyDataSetChanged();
                if (binding.msg != null) binding.msg.setVisibility(View.GONE);
                binding.list.setVisibility(View.VISIBLE);
            } else {
                if (binding != null && binding.msg != null) {
                    binding.msg.setVisibility(View.VISIBLE);
                }
                binding.list.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*********************/

    }

    @Override
    public void refresh() {
        if (this.binding == null || this.xmppActivity == null) {
            Log.d(Config.LOGTAG, "ConversationsOverviewFragment.refresh() skipped updated because view binding or xmppActivity was null");
            return;
        }
        Conversation removed = this.swipedConversation.peek();


        if (removed != null) {
            if (removed.isRead()) {
                this.conversations.remove(removed);
            } else {
                pendingActionHelper.execute();
            }
        }


        filtterConversation();

        ScrollState scrollState = pendingScrollState.pop();
        if (scrollState != null) {
            setScrollPosition(scrollState);
        }
    }

    private void setScrollPosition(ScrollState scrollPosition) {
        if (scrollPosition != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) binding.list.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(scrollPosition.position, scrollPosition.offset);
        }
    }

    private void startTimer() {
        mTimer1 = new Timer();

        mTt1 = new TimerTask() {
            public void run() {
                mTimerHandler.post(new Runnable() {
                    public void run() {
                        if (conversationsAdapter != null) {
                            conversationsAdapter.notifyDataSetChanged();
                            c++;
                            Log.e("Timer start----", "" + c + "");
                        }
                    }
                });
            }
        };

        mTimer1.schedule(mTt1, 10000, 10000);
    }

    private void stopTimer() {
//        if (mTimer1 != null) {
//            mTimer1.cancel();
//            mTimer1.purge();
//        }
    }

    public void updateConverAdpetrData(List<Conversation> conver) {
        if (conversationsAdapter != null) {
            this.conversations.clear();
            this.conversations.addAll(conver);
            conversationsAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onConversationUpdate() {
        Log.e("", "");
    }

    @Override
    public void onRosterUpdate() {
        Log.e("", "");

    }

    @Override
    public void onConversationRead(Conversation conversation, String upToUuid) {
        Log.e("", "");

    }

    @Override
    public void onConversationsListItemUpdated() {
        Log.e("", "");

    }


    public class ListPagerAdapter extends PagerAdapter {
        private final FragmentManager fragmentManager;
        private final StartConversationActivity.MyListFragment[] fragments;

        ListPagerAdapter(FragmentManager fm) {
            fragmentManager = fm;
            fragments = new StartConversationActivity.MyListFragment[3];
        }

        public void requestFocus(int pos) {
            if (fragments.length > pos) {
                fragments[pos].getListView().requestFocus();
            }
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.remove(fragments[position]);
            trans.commit();
            fragments[position] = null;
        }

        @NonNull
        @Override
        public androidx.fragment.app.Fragment instantiateItem(@NonNull ViewGroup container, int position) {
            final androidx.fragment.app.Fragment fragment = getItem(position);
            final FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.add(container.getId(), fragment, "fragment:" + position);
            try {
                trans.commit();
            } catch (IllegalStateException e) {
                //ignore
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return fragments.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object fragment) {
            return ((androidx.fragment.app.Fragment) fragment).getView() == view;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.call);
                case 1:
                    return getResources().getString(R.string.call);
                case 2:
                    return getResources().getString(R.string.setting);
                default:
                    return super.getPageTitle(position);
            }
        }

        androidx.fragment.app.Fragment getItem(int position) {
            if (fragments[position] == null) {
                final StartConversationActivity.MyListFragment listFragment = new StartConversationActivity.MyListFragment();
                if (position == 0) {
//                    listFragment.setListAdapter(mConferenceAdapter);
//                    listFragment.setContextMenu(R.menu.conference_context);
//                    listFragment.setOnListItemClickListener((arg0, arg1, p, arg3) -> openConversationForBookmark(p));
                } else if (position == 1) {
//                    listFragment.setListAdapter(mConferenceAdapter);
//                    listFragment.setContextMenu(R.menu.conference_context);
                } else {
//                    listFragment.setListAdapter(mContactsAdapter);
//                    listFragment.setContextMenu(R.menu.contact_context);
//                    listFragment.setOnListItemClickListener((arg0, arg1, p, arg3) -> openConversationForContact(p));

                }
                fragments[position] = listFragment;
            }
            return fragments[position];
        }
    }
}
