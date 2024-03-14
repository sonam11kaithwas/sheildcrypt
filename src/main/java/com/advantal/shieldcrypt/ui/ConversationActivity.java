package com.advantal.shieldcrypt.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.advantal.shieldcrypt.Config;
import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.services.XmppConnectionService;
import com.advantal.shieldcrypt.ui.util.SettingsUtils;
import com.advantal.shieldcrypt.utils.AccountUtils;

public class ConversationActivity extends XmppActivity {

    public static Intent getSignUpIntent(final Activity activity, final boolean toServerChooser) {
        final Intent intent;
        if (toServerChooser) {
            intent = new Intent(activity, PickServerActivity.class);
        } else {
            intent = new Intent(activity, WelcomeActivity.class);
        }
        return intent;
    }

    public static Intent getSignUpIntent(final Activity activity) {
        return getSignUpIntent(activity, false);
    }

    public static Intent getRedirectionIntent(final ConversationActivity activity) {
        Log.e("thisIsChecking", " getRedirectionIntent");
        final XmppConnectionService service = activity.xmppConnectionService;
        Account pendingAccount = AccountUtils.getPendingAccount(service);
        Intent intent;
        if (pendingAccount != null) {
            intent = new Intent(activity, EditAccountActivity.class);
            intent.putExtra("jid", pendingAccount.getJid().asBareJid().toString());
            if (!pendingAccount.isOptionSet(Account.OPTION_MAGIC_CREATE)) {
                intent.putExtra(EditAccountActivity.EXTRA_FORCE_REGISTER, pendingAccount.isOptionSet(Account.OPTION_REGISTER));
            }
        } else {
            Log.e("thisIsChecking", " accountSize-> " + service.getAccounts().size());
            if (service.getAccounts().size() == 0) {
                if (Config.X509_VERIFICATION) {
                    intent = new Intent(activity, ManageAccountActivity.class);
                } else if (Config.MAGIC_CREATE_DOMAIN != null) {
                    intent = getSignUpIntent(activity);
                } else {
                    intent = new Intent(activity, EditAccountActivity.class);
                }
            } else {

                intent = new Intent(activity, StartConversationActivity.class);
//		intent = new Intent(activity, ConversationsActivity.class);
            }
        }
        intent.putExtra("init", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        return intent;
    }

    @Override
    protected void refreshUiReal() {

    }

    @Override
    public void onBackendConnected() {
        final Intent intent = getRedirectionIntent(this);
        runOnUiThread(() -> {
            startActivity(intent);
        });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      /*startActivity(new Intent(this, ConversationsActivity.class));
      finish();*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        SettingsUtils.applyScreenshotPreventionSetting(this);
    }
}

