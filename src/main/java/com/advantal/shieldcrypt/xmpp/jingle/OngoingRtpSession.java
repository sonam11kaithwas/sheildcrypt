package com.advantal.shieldcrypt.xmpp.jingle;

import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.xmpp.Jid;

public interface OngoingRtpSession {
    Account getAccount();
    Jid getWith();
    String getSessionId();
}
