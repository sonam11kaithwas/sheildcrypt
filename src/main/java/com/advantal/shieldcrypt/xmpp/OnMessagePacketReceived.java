package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.xmpp.stanzas.MessagePacket;

public interface OnMessagePacketReceived extends PacketReceived {
	void onMessagePacketReceived(Account account, MessagePacket packet);
}
