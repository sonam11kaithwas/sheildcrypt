package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.xmpp.stanzas.PresencePacket;

public interface OnPresencePacketReceived extends PacketReceived {
	void onPresencePacketReceived(Account account, PresencePacket packet);
}
