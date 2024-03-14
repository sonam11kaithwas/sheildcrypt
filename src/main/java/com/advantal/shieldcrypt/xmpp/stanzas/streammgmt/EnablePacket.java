package com.advantal.shieldcrypt.xmpp.stanzas.streammgmt;

import com.advantal.shieldcrypt.xml.Namespace;
import com.advantal.shieldcrypt.xmpp.stanzas.AbstractStanza;

public class EnablePacket extends AbstractStanza {

	public EnablePacket() {
		super("enable");
		this.setAttribute("xmlns", Namespace.STREAM_MANAGEMENT);
		this.setAttribute("resume", "true");
	}

}
