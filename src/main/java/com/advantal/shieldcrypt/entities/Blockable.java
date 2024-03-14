package com.advantal.shieldcrypt.entities;

import com.advantal.shieldcrypt.xmpp.Jid;

public interface Blockable {
	boolean isBlocked();
	boolean isDomainBlocked();
	Jid getBlockedJid();
	Jid getJid();
	Account getAccount();
}
