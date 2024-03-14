package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Contact;

public interface OnContactStatusChanged {
	void onContactStatusChanged(final Contact contact, final boolean online);
}
