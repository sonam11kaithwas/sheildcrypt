package com.advantal.shieldcrypt.xmpp.jingle;

import com.advantal.shieldcrypt.entities.Account;
import com.advantal.shieldcrypt.xmpp.PacketReceived;
import com.advantal.shieldcrypt.xmpp.jingle.stanzas.JinglePacket;

public interface OnJinglePacketReceived extends PacketReceived {
	void onJinglePacketReceived(Account account, JinglePacket packet);
}
