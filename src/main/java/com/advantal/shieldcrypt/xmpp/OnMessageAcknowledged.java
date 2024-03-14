package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Account;

public interface OnMessageAcknowledged {
    boolean onMessageAcknowledged(Account account, Jid to, String id);
}
