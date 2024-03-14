package com.advantal.shieldcrypt.xmpp.stanzas.csi;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class InactivePacket extends AbstractStanza {
	public InactivePacket() {
		super("inactive");
		setAttribute("xmlns", Namespace.CSI);
	}
}
