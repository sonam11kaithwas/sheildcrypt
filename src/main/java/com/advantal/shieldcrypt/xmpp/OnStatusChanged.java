package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Account;

public interface OnStatusChanged {
	void onStatusChanged(Account account);
}
