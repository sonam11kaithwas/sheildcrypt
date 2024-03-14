package com.advantal.shieldcrypt.crypto.axolotl;

public interface OnMessageCreatedCallback {
	void run(XmppAxolotlMessage message);
}
