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


//import static com.advantal.shieldcrypt.ui.ConversationFragment.REQUEST_DECRYPT_PGP;

import static com.advantal.shieldcrypt.ui.ConversationFragment.REQUEST_DECRYPT_PGP;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.R;
import com.advantal.shieldcrypt.databinding.ActivityConversationsBinding;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.entities.Contact;
import com.advantal.shieldcrypt.entities.Conversation;
import com.advantal.shieldcrypt.entities.Conversational;
import com.advantal.shieldcrypt.entities.ListItem;
import com.advantal.shieldcrypt.services.XmppConnectionService;
import com.advantal.shieldcrypt.tabs_pkg.adpter_pkg.ViewPagerAdapter;
import com.advantal.shieldcrypt.ui.adapter.ConversationAdapter;
import com.advantal.shieldcrypt.ui.adapter.ListItemAdapter;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationArchived;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationRead;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationSelected;
import com.advantal.shieldcrypt.ui.interfaces.OnConversationsListItemUpdated;
import com.advantal.shieldcrypt.ui.util.ActivityResult;
import com.advantal.shieldcrypt.ui.util.MenuDoubleTabUtil;
import com.advantal.shieldcrypt.ui.util.PendingItem;
import com.advantal.shieldcrypt.utils.SignupUtils;
import com.advantal.shieldcrypt.utils_pkg.MySharedPreferences;
import com.advantal.shieldcrypt.utils.XmppUri;
import com.advantal.shieldcrypt.xmpp.Jid;
import com.advantal.shieldcrypt.xmpp.OnUpdateBlocklist;
import com.google.android.material.tabs.TabLayoutMediator;

import org.openintents.openpgp.util.OpenPgpApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ConversationsActivity extends XmppActivity implements OnConversationSelected, OnConversationArchived, OnConversationsListItemUpdated,
        OnConversationRead, XmppConnectionService.OnAccountUpdate, XmppConnectionService.OnConversationUpdate,
        XmppConnectionService.OnRosterUpdate, OnUpdateBlocklist, XmppConnectionService.OnShowErrorToast,
        XmppConnectionService.OnAffiliationChanged {
    public static final String ACTION_VIEW_CONVERSATION = "com.advantal.shieldcrypt.action.VIEW";
    public static final String EXTRA_CONVERSATION = "conversationUuid";
    public static final String EXTRA_DOWNLOAD_UUID = "com.advantal.shieldcrypt.download_uuid";
    //    private ListItemAdapter mChatAdapter;
    public static final String EXTRA_AS_QUOTE = "com.advantal.shieldcrypt.as_quote";
    public static final String EXTRA_NICK = "nick";
    public static final String EXTRA_IS_PRIVATE_MESSAGE = "pm";
    public static final String EXTRA_DO_NOT_APPEND = "do_not_append";
    public static final String EXTRA_POST_INIT_ACTION = "post_init_action";
    public static final String POST_ACTION_RECORD_VOICE = "record_voice";
    public static final String EXTRA_TYPE = "type";
    public static final int REQUEST_OPEN_MESSAGE = 0x9876;
    public static final int REQUEST_PLAY_PAUSE = 0x5432;
    private static final List<String> VIEW_AND_SHARE_ACTIONS = Arrays.asList(ACTION_VIEW_CONVERSATION, Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE);
    private final List<Conversation> conversations = new ArrayList<>();
    private final List<ListItem> contacts = new ArrayList<>();
    private final List<ListItem> conferences = new ArrayList<>();
    private final List<String> tabArray = new ArrayList<>();
    //secondary fragment (when holding the conversation, must be initialized before refreshing the overview fragment
//    private static final @IdRes
//    int[] FRAGMENT_ID_NOTIFICATIOORDER = {R.id.secondary_fragment, R.id.main_fragment};
    private final PendingItem<Intent> pendingViewIntent = new PendingItem<>();
    private final PendingItem<ActivityResult> postponedActivityResult = new PendingItem<>();
    private final AtomicBoolean mRedirectInProcess = new AtomicBoolean(false);
    ConversationFragment conversationFragment;
    private ConversationsOverviewFragment.ListPagerAdapter mListPagerAdapter;
    private ListItemAdapter mContactsAdapter;
    private ListItemAdapter mConferenceAdapter;
    private ConversationAdapter conversationsAdapter;
    private ActivityConversationsBinding binding;
    private boolean mActivityPaused = true;


    private static boolean isViewOrShareIntent(Intent i) {
        Log.d(Config.LOGTAG, "action: " + (i == null ? null : i.getAction()));
        return i != null && VIEW_AND_SHARE_ACTIONS.contains(i.getAction()) && i.hasExtra(EXTRA_CONVERSATION);
    }

    private static Intent createLauncherIntent(Context context) {
        final Intent intent = new Intent(context, ConversationsActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        return intent;
    }

    private static void executePendingTransactions(final FragmentManager fragmentManager) {
        try {
            fragmentManager.executePendingTransactions();
        } catch (final Exception e) {
            Log.e(Config.LOGTAG, "unable to execute pending fragment transactions");
        }
    }

    @Override
    protected void refreshUiReal() {

        if (conversationFragment != null) {
            conversationFragment.refreshUiReal();
        }
//        invalidateOptionsMenu();
////        for (@IdRes int id : FRAGMENT_ID_NOTIFICATION_ORDER) {
//        refreshFragment();
//        }
        List<Conversation> conversations = xmppConnectionService.getConversations();
        conversationsAdapter.setMyUpdatedConversation(conversations);
        conversationsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
    }

    @Override
    public void onBackendConnected() {
        if (performRedirectIfNecessary(true)) {
            Log.e("true", "true");
            return;
        } else {
            Log.e("false", "false");

        }

        if (getIntent().hasExtra("STARTCHAT")) {
            Intent intent = new Intent(this, StartConversationActivity.class);
            intent.putExtra("STARTCHAT", getIntent().getExtras().getString("STARTCHAT"));
            startActivity(intent);
            this.finish();

        } else {


            xmppConnectionService.getNotificationService().setIsInForeground(true);
            final Intent intent = pendingViewIntent.pop();
            if (intent != null) {
                processViewIntent(intent);
//            if (processViewIntent(intent)) {
//                if (binding.secondaryFragment != null) {
//                    notifyFragmentOfBackendConnected(R.id.main_fragment);
//                }
//                invalidateActionBarTitle();
//                return;
//            }
            }
//        for (@IdRes int id : FRAGMENT_ID_NOTIFICATION_ORDER) {
//            notifyFragmentOfBackendConnected(id);
//        }
//
//        final ActivityResult activityResult = postponedActivityResult.pop();
//        if (activityResult != null) {
//            handleActivityResult(activityResult);
//        }
//
//        invalidateActionBarTitle();
//        if (binding.secondaryFragment != null && ConversationFragment.getConversation(this) == null) {
//            Conversation conversation = ConversationsOverviewFragment.getSuggestion(this);
//            if (conversation != null) {
//                openConversation(conversation, null);
//            }
//        }
//        showDialogsIfMainIsOverview();
        }
    }

    private boolean performRedirectIfNecessary(boolean noAnimation) {
        return performRedirectIfNecessary(null, noAnimation);
    }

    private boolean performRedirectIfNecessary(final Conversation ignore, final boolean noAnimation) {
        if (xmppConnectionService == null) {
            return false;
        }
        boolean isConversationsListEmpty = xmppConnectionService.isConversationsListEmpty(ignore);
//        Log.e("isConversationsListEmpty:",""+isConversationsListEmpty);
        if (isConversationsListEmpty && mRedirectInProcess.compareAndSet(false, true)) {
            final Intent intent = SignupUtils.getRedirectionIntent(this);
//           if (intent.hasExtra("MyFrag")) {
//               boolean init = intent.getBooleanExtra("MyFrag", false);
//               initializeFragments();
////               return ;
//           }
            if (noAnimation) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            }
            runOnUiThread(() -> {
                startActivity(intent);
                if (noAnimation) {
                    overridePendingTransition(0, 0);
                }
            });
        }
        return mRedirectInProcess.get();//1  true
    }

    private void showDialogsIfMainIsOverview() {
        if (xmppConnectionService == null) {
            return;
        }
//        final Fragment fragment = getFragmentManager().findFragmentById(R.id.main_fragment);
//        if (fragment instanceof ConversationsOverviewFragment) {
//            if (ExceptionHelper.checkForCrash(this)) {
//                return;
//            }
//            openBatteryOptimizationDialogIfNeeded();
//        }
        openBatteryOptimizationDialogIfNeeded();
    }

    private String getBatteryOptimizationPreferenceKey() {
        @SuppressLint("HardwareIds") String device = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        return "show_battery_optimization" + (device == null ? "" : device);
    }

    private void setNeverAskForBatteryOptimizationsAgain() {
        getPreferences().edit().putBoolean(getBatteryOptimizationPreferenceKey(), false).apply();
    }

    private void openBatteryOptimizationDialogIfNeeded() {
        if (isOptimizingBattery() && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && getPreferences().getBoolean(getBatteryOptimizationPreferenceKey(), true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.battery_optimizations_enabled);
            builder.setMessage(getString(R.string.battery_optimizations_enabled_dialog, getString(R.string.app_name)));
            builder.setPositiveButton(R.string.next, (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                Uri uri = Uri.parse("package:" + getPackageName());
                intent.setData(uri);
                try {
                    startActivityForResult(intent, REQUEST_BATTERY_OP);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, R.string.device_does_not_support_battery_op, Toast.LENGTH_SHORT).show();
                }
            });
            builder.setOnDismissListener(dialog -> setNeverAskForBatteryOptimizationsAgain());
            final AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private void notifyFragmentOfBackendConnected(@IdRes int id) {
//        final Fragment fragment = getFragmentManager().findFragmentById(id);
//        if (fragment instanceof OnBackendConnected) {
//            ((OnBackendConnected) fragment).onBackendConnected();
//        }
    }

    private void refreshFragment() {
//        final Fragment fragment = getSupportFragmentManager().findFragmentById(id);
//        if (fragment instanceof XmppFragment) {
//            ((ConversationFragment) fragment).refresh();
//        }
        Log.e("PRASHANT", "");
    }

    private boolean processViewIntent(Intent intent) {
        final String uuid = intent.getStringExtra(EXTRA_CONVERSATION);
        final Conversation conversation = uuid != null ? xmppConnectionService.findConversationByUuid(uuid) : null;
        if (conversation == null) {
//            openFragment(conversation);
            this.finish();
            Log.d(Config.LOGTAG, "unable to view conversation with uuid:" + uuid);
            return false;
        }
        openConversation(conversation, intent.getExtras());
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        UriHandlerActivity.onRequestPermissionResult(this, requestCode, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switch (requestCode) {
                    case REQUEST_OPEN_MESSAGE:
                        refreshUiReal();
                        ConversationFragment.openPendingMessage(this);
                        break;
                    case REQUEST_PLAY_PAUSE:
                        ConversationFragment.startStopPending(this);
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ActivityResult activityResult = ActivityResult.of(requestCode, resultCode, data);
        if (xmppConnectionService != null) {
            handleActivityResult(activityResult);
        } else {
            this.postponedActivityResult.push(activityResult);
        }
    }

    private void handleActivityResult(ActivityResult activityResult) {
        if (activityResult.resultCode == Activity.RESULT_OK) {
            handlePositiveActivityResult(activityResult.requestCode, activityResult.data);
        } else {
            handleNegativeActivityResult(activityResult.requestCode);
        }
    }

    private void handleNegativeActivityResult(int requestCode) {
        Conversation conversation = ConversationFragment.getConversationReliable(this);
        switch (requestCode) {
            case REQUEST_DECRYPT_PGP:
                if (conversation == null) {
                    break;
                }
                conversation.getAccount().getPgpDecryptionService().giveUpCurrentDecryption();
                break;
            case REQUEST_BATTERY_OP:
                setNeverAskForBatteryOptimizationsAgain();
                break;
        }
    }

    private void handlePositiveActivityResult(int requestCode, final Intent data) {
        Conversation conversation = ConversationFragment.getConversationReliable(this);
        if (conversation == null) {
            Log.d(Config.LOGTAG, "conversation not found");
            return;
        }
        switch (requestCode) {
            case REQUEST_DECRYPT_PGP:
                conversation.getAccount().getPgpDecryptionService().continueDecryption(data);
                break;
            case REQUEST_CHOOSE_PGP_ID:
                long id = data.getLongExtra(OpenPgpApi.EXTRA_SIGN_KEY_ID, 0);
                if (id != 0) {
                    conversation.getAccount().setPgpSignId(id);
                    announcePgp(conversation.getAccount(), null, null, onOpenPGPKeyPublished);
                } else {
                    choosePgpSignId(conversation.getAccount());
                }
                break;
            case REQUEST_ANNOUNCE_PGP:
                announcePgp(conversation.getAccount(), conversation, data, onOpenPGPKeyPublished);
                break;
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ConversationMenuConfigurator.reloadFeatures(this);
//        OmemoSetting.load(this);
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_conversations);

        tabArray.add("Chats");
        tabArray.add("Calls");
        tabArray.add("Settings");
        creatab();

//binding.speedDial.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
//    @Override
//    public boolean onActionSelected(SpeedDialActionItem actionItem) {
//        return false;
//    }
//});


//        this.initializeFragments();
//        this.invalidateActionBarTitle();
//        final Intent intent;
//        if (savedInstanceState == null) {
//            intent = getIntent();
//        } else {
//            intent = savedInstanceState.getParcelable("intent");
//        }
//        if (isViewOrShareIntent(intent)) {
//            pendingViewIntent.push(intent);
//            setIntent(createLauncherIntent(this));
//        }
//
    }


    private void creatab() {

//        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this, 3);
//        binding.startConversationViewPager.setAdapter(viewPagerAdapter);


        new TabLayoutMediator(binding.tabLayout, binding.startConversationViewPager, (tab, position) -> {
            tab.setText(tabArray.get(position));
        }).attach();


        binding.tabLayout.getTabAt(0).setText("Chats").setIcon(R.drawable.ic_chat_white_24dp);
        binding.tabLayout.getTabAt(1).setText("Calls").setIcon(R.drawable.ic_tab_call);
        binding.tabLayout.getTabAt(2).setText("Settings").setIcon(R.drawable.setting_ic);

        binding.startConversationViewPager.setCurrentItem(0);
        mConferenceAdapter = new ListItemAdapter(this, conferences);
        mContactsAdapter = new ListItemAdapter(this, contacts);

        this.conversationsAdapter = new ConversationAdapter(this, this.conversations);
        this.conversationsAdapter.setConversationClickListener((view, conversation) -> {
            if (this instanceof OnConversationSelected) {
                ((OnConversationSelected) this).onConversationSelected(conversation);
            } else {
                Log.w(ConversationsOverviewFragment.class.getCanonicalName(), "Activity does not implement OnConversationSelected");
            }
        });
//openFragment(conversations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_conversations, menu);
        final MenuItem qrCodeScanMenuItem = menu.findItem(R.id.action_scan_qr_code);
//        if (qrCodeScanMenuItem != null) {
//            if (isCameraFeatureAvailable()) {
//                Fragment fragment = getFragmentManager().findFragmentById(R.id.main_fragment);
//                boolean visible = getResources().getBoolean(R.bool.show_qr_code_scan)
//                        && fragment instanceof ConversationsOverviewFragment;
//                qrCodeScanMenuItem.setVisible(visible);
//            } else {
//                qrCodeScanMenuItem.setVisible(false);
//            }
//        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onConversationSelected(Conversation conversation) {
        clearPendingViewIntent();
        if (ConversationFragment.getConversation(this) == conversation) {
            Log.d(Config.LOGTAG, "ignore onConversationSelected() because conversation is already open");
            return;
        }
//        openFragment(conversation);//, null);

    }

    private void openFragment(Conversation conversation) {
        binding.lay.setVisibility(View.GONE);
        conversationFragment = new ConversationFragment();
        conversationFragment.reInit(conversation, new Bundle());
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_fragment, conversationFragment, null).setReorderingAllowed(true).addToBackStack("name") // name can be null
                .commit();
    }

    public void clearPendingViewIntent() {
        if (pendingViewIntent.clear()) {
            Log.e(Config.LOGTAG, "cleared pending view intent");
        }
    }

    private void displayToast(final String msg) {
        runOnUiThread(() -> Toast.makeText(ConversationsActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onAffiliationChangedSuccessful(Jid jid) {

    }

    @Override
    public void onAffiliationChangeFailed(Jid jid, int resId) {
        displayToast(getString(resId, jid.asBareJid().toString()));
    }

    private void openConversation(Conversation conversation, Bundle extras) {
        final FragmentManager fragmentManager = getFragmentManager();
        executePendingTransactions(fragmentManager);
//        ConversationFragment conversationFragment = (ConversationFragment) fragmentManager.findFragmentById(R.id.main_fragment);
        final boolean mainNeedsRefresh;
        openFragment(conversation);
//        if (conversationFragment == null) {
        mainNeedsRefresh = false;
//            final Fragment mainFragment = fragmentManager.findFragmentById(R.id.main_fragment);
//            if (mainFragment instanceof ConversationFragment) {
//                conversationFragment = (ConversationFragment) mainFragment;
//            } else {
//                conversationFragment = new ConversationFragment();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.main_fragment, conversationFragment);
//                fragmentTransaction.addToBackStack(null);
//                try {
//                    fragmentTransaction.commit();
//                } catch (IllegalStateException e) {
//                    Log.w(Config.LOGTAG, "sate loss while opening conversation", e);
//                    //allowing state loss is probably fine since view intents et all are already stored and a click can probably be 'ignored'
//                    return;
//                }
//            }
//        } else {
//            mainNeedsRefresh = true;
//        }
//        conversationFragment.reInit(conversation, extras == null ? new Bundle() : extras);
//        if (mainNeedsRefresh) {
////            refreshFragment(R.id.main_fragment);
//        } else {
//            invalidateActionBarTitle();
//        }

    }

    public boolean onXmppUriClicked(Uri uri) {
        XmppUri xmppUri = new XmppUri(uri);
        if (xmppUri.isValidJid() && !xmppUri.hasFingerprints()) {
            final Conversation conversation = xmppConnectionService.findUniqueConversationByJid(xmppUri);
            if (conversation != null) {
                openConversation(conversation, null);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MenuDoubleTabUtil.shouldIgnoreTap()) {
            return false;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    try {
                        fm.popBackStack();
                    } catch (IllegalStateException e) {
                        Log.w(Config.LOGTAG, "Unable to pop back stack after pressing home button");
                    }
                    return true;
                }
                break;
            case R.id.action_scan_qr_code:
                UriHandlerActivity.scan(this);
                return true;
            case R.id.action_search_all_conversations:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            case R.id.action_search_this_conversation:
                final Conversation conversation = ConversationFragment.getConversation(this);
                if (conversation == null) {
                    return true;
                }
                final Intent intent = new Intent(this, SearchActivity.class);
                intent.putExtra(SearchActivity.EXTRA_CONVERSATION_UUID, conversation.getUuid());
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_UP && keyEvent.isCtrlPressed()) {
            final ConversationFragment conversationFragment = ConversationFragment.get(this);
            if (conversationFragment != null && conversationFragment.onArrowUpCtrlPressed()) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, keyEvent);
    }

    @Override
    public void onSaveInstanceState(final Bundle savedInstanceState) {
        final Intent pendingIntent = pendingViewIntent.peek();
        savedInstanceState.putParcelable("intent", pendingIntent != null ? pendingIntent : getIntent());
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        final int theme = findTheme();
        if (this.mTheme != theme) {
            this.mSkipBackgroundBinding = true;
            recreate();
        } else {
            this.mSkipBackgroundBinding = false;
        }
        mRedirectInProcess.set(false);


    }


    public void getSelectedAccount() {
        if (xmppConnectionService != null) {
            if (xmppConnectionService != null) {
                Jid jid = null;
                try {
                    if (Config.DOMAIN_LOCK != null) {
                        jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber +
                                "@" + MySharedPreferences.getSharedprefInstance().getXSip(), Config.DOMAIN_LOCK, null);
                    } else {
                        jid = Jid.ofEscaped(MySharedPreferences.getSharedprefInstance().getLoginData().mobileNumber +
                                "@" + MySharedPreferences.getSharedprefInstance().getLoginData().getXmpip());
                    }
                } catch (final IllegalArgumentException e) {
//                return null;
                }
                final XmppConnectionService service = xmppConnectionService;
                if (service != null) {
//                return null;

                    Account account = service.findAccountByJid(jid);
                    Conversation conversation = null;
                    conversations.clear();

                    for (int i = 0; i < account.getRoster().getContacts().size(); i++) {
//                        if (!account.getRoster().getContacts().get(i).getJid().equals(jid)) {
                        conversation = xmppConnectionService.findOrCreateConversation(account.getRoster().getContacts().get(i).getAccount(), account.getRoster().getContacts().get(i).getJid(), false, true);
                        if (conversation.getLatestMessage() != null && !conversation.getLatestMessage().getBody().equals(""))
                            conversations.add(conversation);
//                        }
                    }
//                    conversationsAdapter.notifyDataSetChanged();
                    if (conversationsAdapter != null) {
                        conversationsAdapter.setMyUpdatedConversation(conversations);
                    }
                }
            } else {
//            return null;
            }
        }


    }


    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        if (isViewOrShareIntent(intent)) {
            if (xmppConnectionService != null) {
                clearPendingViewIntent();
                processViewIntent(intent);
            } else {
                pendingViewIntent.push(intent);
            }
        }
        setIntent(createLauncherIntent(this));
    }

    @Override
    public void onPause() {
        this.mActivityPaused = true;
        super.onPause();
//        getSelectedAccount();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.mActivityPaused = false;
//        getSelectedAccount();
    }

    private void initializeFragments() {
        final FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        final Fragment mainFragment = fragmentManager.findFragmentById(R.id.main_fragment);
//        final Fragment secondaryFragment = fragmentManager.findFragmentById(R.id.secondary_fragment);
//        if (mainFragment != null) {
//            if (binding.secondaryFragment != null) {
//                if (mainFragment instanceof ConversationFragment) {
//                    getFragmentManager().popBackStack();
//                    transaction.remove(mainFragment);
//                    transaction.commit();
//                    fragmentManager.executePendingTransactions();
//                    transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.secondary_fragment, mainFragment);
//                    binding.startConversationViewPager.setCurrentItem(0);
////                    transaction.replace(R.id.main_fragment, new ConversationsOverviewFragment());
//                    transaction.commit();
//                    return;
//                }
//            } else {
//                if (secondaryFragment instanceof ConversationFragment) {
//                    transaction.remove(secondaryFragment);
//                    transaction.commit();
//                    getFragmentManager().executePendingTransactions();
//                    transaction = fragmentManager.beginTransaction();
//                    transaction.replace(R.id.main_fragment, secondaryFragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
//                    return;
//                }
//            }
        binding.startConversationViewPager.setCurrentItem(0);


//        } else {
//            binding.myTabVp.viewPager.setCurrentItem(0);
//
        binding.startConversationViewPager.setCurrentItem(0);

//            transaction.replace(R.id.main_fragment, new ConversationsOverviewFragment());
    }
//        if (binding.secondaryFragment != null && secondaryFragment == null) {
//            transaction.replace(R.id.secondary_fragment, new ConversationFragment());
//        }
//        transaction.commit();
//    }

    private void invalidateActionBarTitle() {
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
//        final FragmentManager fragmentManager = getFragmentManager();
////        final Fragment mainFragment = fragmentManager.findFragmentById(R.id.main_fragment);
//        if (mainFragment instanceof ConversationFragment) {
//            final Conversation conversation = ((ConversationFragment) mainFragment).getConversation();
//            if (conversation != null) {
//                actionBar.setTitle(conversation.getName());
//                actionBar.setDisplayHomeAsUpEnabled(true);
////                ActionBarUtil.setActionBarOnClickListener(
////                        binding.toolbar,
////                        (v) -> openConversationDetails(conversation)
////                );
//                return;
//            }
//        }
//        actionBar.setTitle(R.string.app_name);
//        actionBar.setDisplayHomeAsUpEnabled(false);
//        ActionBarUtil.resetActionBarOnClickListeners(binding.toolbar);

    }

    private void openConversationDetails(final Conversation conversation) {
        if (conversation.getMode() == Conversational.MODE_MULTI) {
            ConferenceDetailsActivity.open(this, conversation);
        } else {
            final Contact contact = conversation.getContact();
            if (contact.isSelf()) {
                switchToAccount(conversation.getAccount());
            } else {
                switchToContactDetails(contact);
            }
        }
    }

    @Override
    public void onConversationArchived(Conversation conversation) {
        if (performRedirectIfNecessary(conversation, false)) {
            return;
        }
//        final FragmentManager fragmentManager = getFragmentManager();
//        final Fragment mainFragment = fragmentManager.findFragmentById(R.id.main_fragment);
//        if (mainFragment instanceof ConversationFragment) {
//            try {
//                fragmentManager.popBackStack();
//            } catch (final IllegalStateException e) {
//                Log.w(Config.LOGTAG, "state loss while popping back state after archiving conversation", e);
//                //this usually means activity is no longer active; meaning on the next open we will run through this again
//            }
        return;
    }
//        final Fragment secondaryFragment = fragmentManager.findFragmentById(R.id.secondary_fragment);
//        if (secondaryFragment instanceof ConversationFragment) {
//            if (((ConversationFragment) secondaryFragment).getConversation() == conversation) {
//                Conversation suggestion = ConversationsOverviewFragment.getSuggestion(this, conversation);
//                if (suggestion != null) {
//                    openConversation(suggestion, null);
//                }
//            }
//        }
//    }

    @Override
    public void onConversationsListItemUpdated() {
//        Fragment fragment = getFragmentManager().findFragmentById(R.id.main_fragment);
//        if (fragment instanceof ConversationsOverviewFragment) {
//            ((ConversationsOverviewFragment) fragment).refresh();
//        }
        Log.e("", "");
    }

    @Override
    public void switchToConversation(Conversation conversation) {
        Log.d(Config.LOGTAG, "override");
        openConversation(conversation, null);
    }

    @Override
    public void onConversationRead(Conversation conversation, String upToUuid) {
        if (!mActivityPaused && pendingViewIntent.peek() == null) {
            xmppConnectionService.sendReadMarker(conversation, upToUuid);
        } else {
            Log.d(Config.LOGTAG, "ignoring read callback. mActivityPaused=" + mActivityPaused);
        }
    }

    @Override
    public void onAccountUpdate() {
        this.refreshUi();
    }

    @Override
    public void onConversationUpdate() {
//        if (performRedirectIfNecessary(false)) {
//            return;
//        }
        this.refreshUi();
    }

    @Override
    public void onRosterUpdate() {
        this.refreshUi();
    }

    @Override
    public void OnUpdateBlocklist(OnUpdateBlocklist.Status status) {
        this.refreshUi();
    }

    @Override
    public void onShowErrorToast(int resId) {
        runOnUiThread(() -> Toast.makeText(this, resId, Toast.LENGTH_SHORT).show());
    }
}
