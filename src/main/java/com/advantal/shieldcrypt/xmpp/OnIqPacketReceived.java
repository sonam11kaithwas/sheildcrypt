package com.advantal.shieldcrypt.xmpp;

import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.xmpp.stanzas.IqPacket;

public interface OnIqPacketReceived extends PacketReceived {
	void onIqPacketReceived(Account account, IqPacket packet);
}
