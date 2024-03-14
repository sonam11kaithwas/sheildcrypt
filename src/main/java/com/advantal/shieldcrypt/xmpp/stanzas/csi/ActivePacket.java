package com.advantal.shieldcrypt.xmpp.stanzas.csi;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class ActivePacket extends AbstractStanza {
	public ActivePacket() {
		super("active");
		setAttribute("xmlns", Namespace.CSI);
	}
}
